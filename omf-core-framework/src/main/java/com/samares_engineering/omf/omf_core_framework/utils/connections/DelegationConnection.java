package com.samares_engineering.omf.omf_core_framework.utils.connections;

import com.google.common.base.Strings;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.factory.OMFFactory;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.utils.AllCreatedElements;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.clone.CloneManager;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.ConnectorUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Helper class to create delegation connection between two connectable elements.
 * Override
 */
public class DelegationConnection {

    private Port srcPort;
    private Class srcFunction;
    private Property srcPart;
    private Element srcInterfaceOwner;
    private Port targetPort;
    private Class targetFunction;
    private Property targetPart;
    private Element targetInterfaceOwner;

    /**
     * Create a connection with full port and interface delegation from a connector.
     *
     * @param connector the connector to create the delegation from.
     * @return the list of created connectors.
     */
    public List<Connector> createConnectionWithDelegation(Connector connector) {
        ConnectorEnd firstEnd = ModelHelper.getFirstEnd(connector);
        ConnectableElement srcRole = firstEnd.getRole();
        ConnectorEnd secondEnd = ModelHelper.getSecondEnd(connector);
        ConnectableElement dstRole = secondEnd.getRole();
        Element commonAncestor = connector.getOwner();


        List<Property> srcPropertyPath = ConnectorUtils.getFullPropertyPath(firstEnd);
        List<Property> dstPropertyPath = ConnectorUtils.getFullPropertyPath(secondEnd);
        return createConnectionWithDelegation(srcRole, dstRole, commonAncestor, srcPropertyPath, dstPropertyPath, connector.getOwner());

    }

    /**
     * create a connection between two connectableElements, creating if needed all delegation ports with their interfaces.
     * General algorithm: Src Port (if exist, else DST Port) will be used to create the delegation through all source parents functions,
     * then it will create a conjugate which will be used to create the destination delegation.
     * NOTE: in case of Port2port connection, destination port will be replaced by the created delegation;
     * CASES:
     * --- Port to Port
     * --- Part to Port/Port to part
     * --- Part to Part
     * --- Part to diagram border
     * --- port to Diagram border
     * --- diagram border to port
     * --- diagram border to part
     * --- all listed cases take into account the following situation
     * - child to mother
     * - mother to child
     * - cousins
     * - sisters
     * - Single instance
     * - Multi Instances: when the SAME part is used multiple times in the same context. E.g Car has 4 wheels typed by WHEEL, WHEEL contains a part tyre:TYRE, there is only one tyre part in this context. For maximum complexity add another level.
     *
     * @param src:             Connectable element, a part or a port.
     * @param dst:             Connectable element, a part or a port.
     * @param commonAncestor:  common ancestor between the two connectable elements, generally the connector owner.
     * @param propertyPathSRC: the source property path list indicating the exact part to connect
     * @param propertyPathDST: the target property path list indicating the exact part to connect
     * @param contextOwner:    the owner of the context, generally the diagram owner.
     * @return : the list of created connectors
     */
    public List<Connector> createConnectionWithDelegation(Element src, Element dst,
                                                          Element commonAncestor,
                                                          List<Property> propertyPathSRC, List<Property> propertyPathDST,
                                                          Element contextOwner) {
        final boolean isSrcProxyPort = Profile._getSysml().proxyPort().is(src);
        final boolean isDstProxyPort = Profile._getSysml().proxyPort().is(dst);
        final boolean isSrcPartProperty = src instanceof Property && Profile._getSysmlAdditionalStereotypes().partProperty().is(src);
        final boolean isDstPartProperty = dst instanceof Property && Profile._getSysmlAdditionalStereotypes().partProperty().is(dst);

        Class originalInterfaceBlock = null;

        final boolean isSrcDiagramOwner = contextOwner != null && isSrcProxyPort && src.getOwner() == contextOwner;
        final boolean isDstDiagramOwner = contextOwner != null && isDstProxyPort && dst.getOwner() == contextOwner;
        final boolean isEndsDiagramOwner = isSrcDiagramOwner || isDstDiagramOwner;

        //---- INIT: create port on parts

        //Part to * => Port to *
        if (isSrcPartProperty) {
            srcFunction = (Class) ((Property) src).getType();
            srcPort = createPort(null, srcFunction);
            srcPart = (Property) src;
        }
        //Port to *
        if (isSrcProxyPort) {
            srcPort = (Port) src;
            srcFunction = (Class) srcPort.getOwner();
        }
        srcInterfaceOwner = srcFunction;

        //* to Part => * to Port
        if (isDstPartProperty) {
            targetFunction = (Class) ((Property) dst).getType();
            targetPort = createPort(null, targetFunction);
            targetPart = (Property) dst;
        }
        //* to Port
        if (isDstProxyPort) {
            targetPort = (Port) dst;
            targetFunction = (Class) targetPort.getOwner();
        }
        targetInterfaceOwner = targetFunction;

        originalInterfaceBlock = (Class) srcPort.getType();


        //isSrcDiagramOwner
        if (isSrcDiagramOwner && srcPort == null && srcPart == null)
            srcPort = clonePortForDelegation(targetPort, contextOwner);

        //isDstDiagramOwner
        if (isDstDiagramOwner && targetPort == null && targetPart == null)
            targetPort = clonePortForDelegation(srcPort, contextOwner);


        //------------------------------------------------------------
        if (null == srcPart) {
            srcPart = !propertyPathSRC.isEmpty() ? ConnectorUtils.getPartFromPropertyPath(srcPort.getOwner(), propertyPathSRC) :
                    OMFUtils.getPartInContext(srcPort.getOwner(), OMFUtils.getAllPartsInContext((Class) contextOwner, new ArrayList<>()));
        }
        if (null == targetPart) {
            targetPart = !propertyPathDST.isEmpty() ?
                    ConnectorUtils.getPartFromPropertyPath(targetPort.getOwner(), propertyPathDST) :
                    OMFUtils.getPartInContext(targetPort.getOwner(),
                            OMFUtils.getAllPartsInContext((Class) contextOwner, new ArrayList<>()));
        }

        boolean srcIsTypedByInterfaceBlock = originalInterfaceBlock != null && interfaceBlockHasStereotype(originalInterfaceBlock);
        boolean targetTypedByInterface = targetPort.getType() != null && interfaceBlockHasStereotype(targetPort.getType());
        final boolean isBothInterfaceNull = !srcIsTypedByInterfaceBlock && !targetTypedByInterface;
        final boolean isSrcConnectionDirectFromMotherToPart = isSrcDiagramOwner || (srcPart.getType() == commonAncestor);
        final boolean isTargetConnectionDirectFromMotherToPart = isDstDiagramOwner || (targetPart.getType() == commonAncestor);

        //---- Create conjugated Interface
        //Create default interface and SRC will be producing function
        if (isBothInterfaceNull) {
            if (isSrcConnectionDirectFromMotherToPart)
                originalInterfaceBlock = createDefaultInterfaceBlock(srcPort, srcInterfaceOwner, SysMLProfile.FlowDirectionKindEnum.IN);

            if (isTargetConnectionDirectFromMotherToPart)
                originalInterfaceBlock = createDefaultInterfaceBlock(srcPort, srcInterfaceOwner, SysMLProfile.FlowDirectionKindEnum.OUT);

            srcPort.setType(originalInterfaceBlock);
            srcIsTypedByInterfaceBlock = true;
        }

        //Conjugate target Interface
        targetTypedByInterface = executeTargetInterfaceConjugation(srcIsTypedByInterfaceBlock, isSrcConnectionDirectFromMotherToPart, isTargetConnectionDirectFromMotherToPart, originalInterfaceBlock, targetTypedByInterface);

        //Conjugate source Interface
        executeSrcInterfaceConjugation(targetTypedByInterface, isSrcConnectionDirectFromMotherToPart, isTargetConnectionDirectFromMotherToPart);

        //Actual connection delegation creation
        List<Connector> allConnectors = createActualConnectorDelegation(commonAncestor, propertyPathSRC, propertyPathDST, isSrcDiagramOwner, isDstDiagramOwner);
        return allConnectors;
    }

    /**
     * Execute the target interface conjugation.
     * Default algorithm: if the source is typed by an interface, the target will be typed by the same interface.
     * Target delegation will create conjugated interface
     *
     * @param srcIsTypedByInterfaceBlock               if true, the source is typed by an interface.
     * @param isSrcConnectionDirectFromMotherToPart    if true, the source connection is direct from mother to part.
     * @param isTargetConnectionDirectFromMotherToPart if true, the target connection is direct from mother to part.
     * @param originalInterfaceBlock                   the original interface block.
     * @param targetTypedByInterface                   if true, the target is typed by an interface.
     * @return true if the target is typed by an interface.
     */
    private boolean executeTargetInterfaceConjugation(boolean srcIsTypedByInterfaceBlock, boolean isSrcConnectionDirectFromMotherToPart, boolean isTargetConnectionDirectFromMotherToPart, Class originalInterfaceBlock, boolean targetTypedByInterface) {
        if (srcIsTypedByInterfaceBlock) {
            Class targetInterface;
            if (isSrcConnectionDirectFromMotherToPart || isTargetConnectionDirectFromMotherToPart) {
                targetInterface = cloneInterfaceBlockForDelegation(srcPort, originalInterfaceBlock, targetInterfaceOwner);
            } else {
                targetInterface = cloneInterfaceBlockForDelegation(srcPort, originalInterfaceBlock, targetInterfaceOwner);
                conjugatePortAndInterface(targetPort, targetInterface);
            }

            targetPort.setType(targetInterface);
            targetPort.setName(targetInterface.getName());
            targetTypedByInterface = false;
        }
        return targetTypedByInterface;
    }

    /**
     * Execute the source interface conjugation.
     *
     * @param targetTypedByInterface                   if true, the target is typed by an interface.
     * @param isSrcConnectionDirectFromMotherToPart    if true, the source connection is direct from mother to part.
     * @param isTargetConnectionDirectFromMotherToPart if true, the target connection is direct from mother to part.
     */
    private void executeSrcInterfaceConjugation(boolean targetTypedByInterface, boolean isSrcConnectionDirectFromMotherToPart, boolean isTargetConnectionDirectFromMotherToPart) {
        Class originalInterfaceBlock;
        if (targetTypedByInterface) {
            originalInterfaceBlock = (Class) targetPort.getType();

            Class srcInterface;
            if (isSrcConnectionDirectFromMotherToPart || isTargetConnectionDirectFromMotherToPart) {
                srcInterface = cloneInterfaceBlockForDelegation(srcPort, originalInterfaceBlock, srcInterfaceOwner);
            } else {
                srcInterface = cloneConjugatedInterfaceBlockForDelegation(srcPort, originalInterfaceBlock, srcInterfaceOwner);
            }

            srcPort.setType(srcInterface);
            srcPort.setName(srcInterface.getName());
        }
    }

    /**
     * Create the actual connectors for the delegation connection.
     *
     * @param commonAncestor    the common ancestor between the two elements.
     * @param propertyPathSRC   the property path list of the source element.
     * @param propertyPathDST   the property path list of the destination element.
     * @param isSrcDiagramOwner if true the source element is the diagram owner.
     * @param isDstDiagramOwner if true the destination element is the diagram owner.
     * @return the list of created connectors.
     */
    private List<Connector> createActualConnectorDelegation(Element commonAncestor,
                                                            List<Property> propertyPathSRC,
                                                            List<Property> propertyPathDST,
                                                            boolean isSrcDiagramOwner,
                                                            boolean isDstDiagramOwner) {
        //-----------------------------------------------------
        //   Connector Part
        // -----------------------------------------------------
        List<Connector> l_inConnector = new ArrayList<>();
        List<Connector> l_outConnector = new ArrayList<>();

        //Connecting all SRC until the top
        if (!isSrcDiagramOwner)
            l_inConnector.addAll(connectDelegationConnectionFromSonToMother(srcPart, srcPort, commonAncestor, propertyPathSRC, propertyPathDST, true));

        //Get Mother src part and port
        ConnectableElement role;
        Port srcMotherPort = null;
        Property srcMotherPart = null;
        if (l_inConnector.isEmpty()) {
            srcMotherPort = srcPort;
            srcMotherPart = srcPart;
        } else {
            role = ConnectorUtils.getHighestConnectableElementFromConnectorList(l_inConnector, commonAncestor, propertyPathSRC);
            if (null == role)
                System.out.println("[Full Connection Path] ERR Role not found from connector list"); //TODO: throw exception

            srcMotherPort = (Port) role;
            srcMotherPart = ConnectorUtils.getPartFromPropertyPath(srcMotherPort.getOwner(), propertyPathSRC);

        }

        //Connecting all DST until the top
        if (!isDstDiagramOwner)
            l_outConnector.addAll(connectDelegationConnectionFromSonToMother(targetPart, targetPort, commonAncestor, propertyPathSRC, propertyPathDST, false));

        //Get Mother dst part and port
        Port targetMotherPort = null;
        Property targetMotherPart = null;

        //SonToMother
        if (l_outConnector.isEmpty()) {
            targetMotherPort = targetPort;
            targetMotherPart = targetPart;
        } else {
            role = ConnectorUtils.getHighestConnectableElementFromConnectorList(l_outConnector, commonAncestor, propertyPathDST);
            if (null == role) {
                role = ConnectorUtils.getHighestConnectableElementFromConnectorList(l_outConnector, commonAncestor, OMFUtils.getAllPartsInContext((Class) commonAncestor, new ArrayList<>()));
                System.out.println("[Full Connection Path] ERR Role not found from connector list");//TODO: throw exception
            }

            targetMotherPort = (Port) role;
            targetMotherPart = ConnectorUtils.getPartFromPropertyPath(targetMotherPort.getOwner(), propertyPathDST);
            if (targetMotherPart == null)//Mother port 2 son part -> shall be applied to all parts
                targetMotherPart = OMFUtils.getPartInContext(targetMotherPort.getOwner(), OMFUtils.getAllPartsInContext((Class) commonAncestor, new ArrayList<>()));
        }
        List<Connector> allConnectors = l_inConnector;
        allConnectors.addAll(l_outConnector);
        //Final Connector
        allConnectors.add(OMFFactory.getInstance().createDirectConnectorPath(srcMotherPart, srcMotherPort, targetMotherPart, targetMotherPort, (Class) commonAncestor, propertyPathSRC, propertyPathDST));


        AllCreatedElements.registerCreatedElements(allConnectors.stream().map(Element.class::cast).collect(Collectors.toList()));
        return allConnectors;
    }

    /**
     * Clone the interface block for delegation with conjugation.
     * Override this method to create a custom interface block when delegating a connection.
     *
     * @param port                   the port to clone the interface block from.
     * @param originalInterfaceBlock the original interface block to clone.
     * @param interfaceOwner         the owner of the new interface block.
     * @return the cloned interface block.
     */
    public Class cloneConjugatedInterfaceBlockForDelegation(Port port, Class originalInterfaceBlock, Element interfaceOwner) {
        Class clonedInterfaceBlock = (Class) new CloneManager("").cloneType(originalInterfaceBlock).get(originalInterfaceBlock);
        clonedInterfaceBlock.setOwner(interfaceOwner);
        port.setType(clonedInterfaceBlock);
        conjugatePortAndInterface(port, clonedInterfaceBlock);
        return clonedInterfaceBlock;
    }

    /**
     * Clone the interface block for delegation.
     * Override this method to create a custom interface block when delegating a connection.
     *
     * @param port                   the port to clone the interface block from.
     * @param originalInterfaceBlock the original interface block to clone.
     * @param interfaceOwner         the owner of the new interface block.
     * @return the cloned interface block.
     */
    public Class cloneInterfaceBlockForDelegation(Port port, Class originalInterfaceBlock, Element interfaceOwner) {
        return (Class) new CloneManager("").cloneType(originalInterfaceBlock).get(originalInterfaceBlock);
    }

    /**
     * Conjugate the port and the interface block.
     * Override this method to conjugate a custom port and interface block.
     *
     * @param port            the port to conjugate.
     * @param targetInterface the interface block to conjugate.
     */
    public void conjugatePortAndInterface(Port port, Class targetInterface) {
//        if (!port.equals(sourcePort) && !port.equals(targetPort)) {
        port.setConjugated(!port.isConjugated());
    }


    /**
     * Create a default interface block with a flow property.
     * Override this method to create a custom interface block.
     *
     * @param port              the port to create the interface block from.
     * @param srcInterfaceOwner the owner of the interface block.
     * @param direction         the direction of the flow property.
     * @return the created interface block.
     */
    public Class createDefaultInterfaceBlock(Port port, Element srcInterfaceOwner, SysMLProfile.FlowDirectionKindEnum direction) {
        Class interfaceBlock = SysMLFactory.getInstance().createInterfaceBlock(srcInterfaceOwner);
        Property flowProperty = SysMLFactory.getInstance().createFlowProperty(interfaceBlock);
        if (direction == SysMLProfile.FlowDirectionKindEnum.IN)
            port.setConjugated(true);
        return interfaceBlock;
    }

    /**
     * Check if the interface block has the stereotype.
     * Override this method to check for a custom stereotype.
     *
     * @param originalInterfaceBlock the interface block to check.
     * @return true if the interface block has the stereotype.
     */
    public boolean interfaceBlockHasStereotype(Type originalInterfaceBlock) {
        return Profile._getSysml().interfaceBlock().is(originalInterfaceBlock);
    }


    /**
     * will create the all delegations fom son to mother (commonAncestor) following the propertyPath list
     *
     * @param sonPart             the part to connect
     * @param sonPort             the port to connect
     * @param commonAncestor      the common ancestor between the two elements
     * @param listPropertyPathSRC the property path list of the source element
     * @param listPropertyPathDST the property path list of the destination element
     * @param isSRC               if true the connection is from son to mother, else from mother to son
     * @return the set of created connectors
     */
    public Set<Connector> connectDelegationConnectionFromSonToMother(Property sonPart, Port sonPort,
                                                                     Element commonAncestor,
                                                                     List<Property> listPropertyPathSRC, List<Property> listPropertyPathDST,
                                                                     boolean isSRC) {
        Element motherElement = sonPart.getOwner();
        Set<Connector> setConnector = new HashSet<>();

        boolean motherIsNotCommonAncestor = !motherElement.equals(commonAncestor) && !sonPort.getOwner().equals(commonAncestor);

        while (motherIsNotCommonAncestor) {
            Port motherPort = clonePortForDelegation(sonPort, motherElement);
//            motherPort.getType().setName(motherPort.getName());
            Property motherPart;
            if (isSRC) {
                motherPart = ConnectorUtils.getPartFromPropertyPath(motherElement, listPropertyPathSRC);

            } else {
                motherPart = ConnectorUtils.getPartFromPropertyPath(motherElement, listPropertyPathDST);
            }

            if (motherPart == null)//Some delegation case Mother port to son part
                motherPart = OMFUtils.getPartInContext(motherElement, OMFUtils.getAllPartsInContext((Class) commonAncestor, new ArrayList<>()));

            Connector connector = OMFFactory.getInstance().createDirectConnectorPath(sonPart, sonPort, motherPart, motherPort, (Class) motherElement, listPropertyPathSRC, listPropertyPathDST);
            setConnector.add(connector);

            sonPart = motherPart;
            motherElement = motherPart.getOwner();
            sonPort = motherPort;

            motherIsNotCommonAncestor = !motherElement.equals(commonAncestor) && !sonPart.getType().equals(commonAncestor);
        }
        return setConnector;
    }

    /**
     * Create a proxy port with an interface block and a flow property.
     * Override this method to create a custom ports
     *
     * @param name      the name of the port, if null the name will be the same as the interface block.
     * @param portOwner the owner of the port.
     * @return the created port.
     */
    public Port createPort(String name, Class portOwner) {
        Port port = SysMLFactory.getInstance().createProxyPort(portOwner);
        Class interfaceBlock = SysMLFactory.getInstance().createInterfaceBlock(portOwner);
        port.setType(interfaceBlock);
        Property flowProperty = SysMLFactory.getInstance().createFlowProperty(interfaceBlock);


        if (!Strings.isNullOrEmpty(name)) {
            port.setName(name);
            interfaceBlock.setName(name);
            flowProperty.setName(name);
        }
        return port;
    }

    /**
     * Clone the port for delegation with its type.
     * Override this method to create a custom port when delegating a connection.
     *
     * @param originalPort the original port to clone.
     * @param Owner        the owner of the new port.
     * @return the cloned port.
     */
    public Port clonePortForDelegation(Port originalPort, Element Owner) {
        Port port = (Port) new CloneManager("").clonePort(originalPort).get(originalPort);
        port.setOwner(Owner);
        return port;
    }

}
