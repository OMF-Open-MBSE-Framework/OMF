/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.factory;


import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.PartView;
import com.nomagic.magicdraw.uml.symbols.shapes.PortView;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares.omf.core.utils.OMFConstants;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.utils.diagrams.InternalDiagramManagement;
import com.samares.omf.core.utils.errorManagement.exceptions.GenericException;
import com.samares.omf.core.utils.errorManagement.exceptions.OMFException;
import com.samares.omf.core.utils.profile.Profile;

import java.util.*;
import java.util.stream.Collectors;

public class OMFFactory extends AMagicDrawFactory {
    private OMFFactory() {
        setProject(OMFUtils.currentProject);
    }

    public static OMFFactory getInstance() {
        return OMFHelperHolder.instance;
    }

    public static OMFFactory getInstance(Project project) {
        OMFHelperHolder.instance.setProject(project);
        return OMFHelperHolder.instance;
    }

    public ArrayList<Property> computeDelegationPathPropertyUsingPEE(Property son, Property mother, Diagram diagram) throws OMFException {
        PresentationElementsManager manager = PresentationElementsManager.getInstance();
        DiagramPresentationElement diagramPEE = OMFUtils.getDiagram(diagram);

        java.lang.Class ClassSon = son instanceof Port ? PortView.class : PartView.class;
        PresentationElement sonPEE = diagramPEE.findPresentationElement(son, ClassSon);

        boolean isMotherDiagramBorder = mother instanceof Port && mother.getOwner() == diagram.getOwner();
        Element elementToFind = mother instanceof Port ? mother.getOwner() : mother;

        ArrayList<Property> l_parts = new ArrayList<Property>();
        PresentationElement parentItemPEE = sonPEE.getParent();

        while (parentItemPEE.getElement() == elementToFind) {
            if (parentItemPEE.getElement() == diagram.getOwner() && !isMotherDiagramBorder)
                throw new OMFException("[Connection] no presentation element for mother element: " + mother.getName() + " and son: " + son.getName(), GenericException.ECriticality.CRITICAL);

            if (sonPEE instanceof PartView)
                l_parts.add(((PartView) sonPEE).getElement());

            parentItemPEE = sonPEE.getParent();
        }

        if (!(mother instanceof Port))   //Mother == Part property => last element of the list
            l_parts.add(mother);
        Collections.reverse(l_parts);

        return l_parts;
    }

    public void setInterfaceDirection(Class originalIinterface, SysMLProfile.FlowDirectionKindEnum direction) {
        originalIinterface.getOwnedAttribute().forEach(property -> Profile.getSysml().flowProperty().setDirection(property, direction));
    }


    //-----------------------------------------------------------------------//
    //Update Direction

    public Boolean isInterfaceOut(Type type) {
        boolean isIn = type.getOwnedElement().stream().filter(flow -> flow instanceof Property && !(flow instanceof Port)).anyMatch(flow -> Objects.equals(Profile.getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.IN));
        boolean isOut = type.getOwnedElement().stream().filter(flow -> flow instanceof Property && !(flow instanceof Port)).anyMatch(flow -> Objects.equals(Profile.getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.OUT));
        boolean isInOut = type.getOwnedElement().stream().filter(flow -> flow instanceof Property && !(flow instanceof Port)).anyMatch(flow -> Objects.equals(Profile.getSysml().flowProperty().getDirection(flow), SysMLProfile.FlowDirectionKindEnum.INOUT));

        if (isIn && isInOut || isInOut)
            return false;

        if (isIn)
            return false;

        return true;
    }

    public void conjugatePort(Port port) {
        System.out.println("Port Conjugating not implemented");
    }

    public void conjugateFlowProperty(Property flowProperty) {
//        SysML.FlowProperty.setName(NamingRules.getFfpBuilderName().name(SysML.FlowProperty.getName()).switchPrefixFromName().switchSuffixFromName().build());
        if(Profile.getSysml().flowProperty().getDirection(flowProperty) == SysMLProfile.FlowDirectionKindEnum.IN) {
            Profile.getSysml().flowProperty().setDirection(flowProperty, SysMLProfile.FlowDirectionKindEnum.OUT);
//            SysML.FlowProperty.setName(NamingRules.getFfpBuilderName().name(SysML.FlowProperty.getName()).outPrefix().outSuffix().build());
        } else {
            Profile.getSysml().flowProperty().setDirection(flowProperty, SysMLProfile.FlowDirectionKindEnum.IN);
//            SysML.FlowProperty.setName(NamingRules.getFfpBuilderName().name(SysML.FlowProperty.getName()).inPrefix().inSuffix().build());
        }
    }

    //Compatibility
    public Port getCompatibleFlow(Port pa, List<Port> l_fiPorts) {
        List<Port> compatiblePort = getElementWithCompatibleType(pa, l_fiPorts);


        if (null == compatiblePort || compatiblePort.size() > 1)
            compatiblePort = getElementWithCompatibleName(pa, l_fiPorts);

        if (null == compatiblePort || compatiblePort.size() > 1)
            return null;
        return compatiblePort.get(0);
    }

    public List<Port> getElementWithCompatibleType(Port portA, List<Port> ports) {
        Type portAType = Objects.requireNonNull(portA.getType(), "Can't find type on port " + portA.getName());
        Optional<Property> optPropA = portAType.getOwnedElement().stream()
                .filter(Profile.getSysml().flowProperty()::is)
                .map(Property.class::cast)
                .findFirst();

        if (optPropA.isEmpty()) return Collections.emptyList();

        Property flowA = optPropA.get();
        if (flowA.getType() == null)
            return Collections.emptyList();

        ArrayList<Port> matchPort = new ArrayList<>();
        for (Port portB : ports) {
            Type portBType = Objects.requireNonNull(portB.getType(), "Can't find type on port " + portB.getName());
            if (portBType.getOwnedElement().stream().allMatch(Profile.getSysml().flowProperty()::is))
                break;

            Property flowB = (Property) portBType.getOwnedElement().iterator().next();
            if (areFlowPropertyCompatible(flowA, flowB))
                matchPort.add(portB);
        }

        return matchPort;
    }

    public List<Port> getElementWithCompatibleName(Port a, List<Port> list) {
        return list.stream().filter(pb -> pb.getName().equals(a.getName())).collect(Collectors.toList());
    }

    public void arePortCompatible(Port pA, Port pB, Element context) {
//        Optional<Element> optA = pA.getType() == null? pA.getType().getOwnedElement().stream().filter(fp->SysML.isFlowProperty(fp)).findFirst(): null;
//        if(opt)
//        final boolean bothNoType     = fpA.getType() == null && fpB.getType() == null;
//        final boolean oneWithoutType = (fpA.getType() == null || fpB.getType() == null) && !bothNoType;
//        final boolean bothTyped      = !bothNoType && !oneWithoutType;
//        final boolean bothSameTyped  = bothTyped && fpA.getType() == fpB.getType();
//        Connector connector = SysMLFactory.getInstance().createConnector(pA, pB, null, null, context);

//        ValidationHelper.validateElement(connector)

    }

    public boolean areFlowPropertyCompatible(Property fpA, Property fpB) {
        final boolean bothNoType = fpA.getType() == null && fpB.getType() == null;
        final boolean oneWithoutType = (fpA.getType() == null || fpB.getType() == null) && !bothNoType;
        final boolean bothTyped = !bothNoType && !oneWithoutType;
        final boolean bothSameTyped = bothTyped && fpA.getType() == fpB.getType();
        final boolean bothSameName = fpA.getName().equals(fpB.getName());
        final boolean areCompatible = bothNoType || (bothSameName && bothSameTyped);
//        final boolean areCompatible = bothSameName;
        return areCompatible;

//        ValidationHelper.validateElement(connector)

    }

    //Connection
    public void setConnectorEnd(ConnectorEnd ce, Property part, Port port, List<Property> availableParts) throws OMFException {
        List<Element> pathSource = null;
        Class owner = (Class) Objects.requireNonNull(ce.get_connectorOfEnd(), "Can't find connector associated " +
                "with connector end").getOwner();
        Objects.requireNonNull(owner, "Can't find owner of connector");
        ce.setRole(port);

        if (part != null)
            pathSource = new ArrayList<>(OMFUtils.calculateNestedPath(new ArrayList<>(), part, owner, availableParts));

        if (port != null && port.getOwner() != null && !owner.equals(port.getOwner()) && pathSource != null) {
            StereotypesHelper.addStereotype(ce, Profile.getSysml().nestedConnectorEnd().getStereotype());
            Profile.getSysml().elementPropertyPath().setPropertyPath(ce, pathSource);
            ce.setPartWithPort(part);
        }
    }

    public void setNestedConnectorEnd(ConnectorEnd ce, Property part, Port port) {
        ce.setRole(port);

        StereotypesHelper.addStereotype(ce, Profile.getSysml().nestedConnectorEnd().getStereotype());
        StereotypesHelper.setStereotypePropertyValue(ce, StereotypesHelper.getFirstVisibleStereotype(ce), "propertyPath", part);

        ce.setPartWithPort(part);
    }

    public Connector old_createDirectConnectorPath(Property srcPart, Port srcPort, Property targetPart, Port targetPort, Class connectorOwner, boolean withRefresh) throws OMFException {
        Connector connector = getMagicDrawFactory().createConnectorInstance();
        ConnectorEnd ce1 = Objects.requireNonNull(ModelHelper.getFirstEnd(connector), "Connector first end is null");
        ConnectorEnd ce2 = Objects.requireNonNull(ModelHelper.getSecondEnd(connector), "Connector second end is null");

        connector.setOwner(connectorOwner);
        ce1.set_connectorOfEnd(connector);
        ce2.set_connectorOfEnd(connector);

        setConnectorEnd(ce1, srcPart, srcPort, OMFUtils.getAllPartsInContext(connectorOwner, null));
        setConnectorEnd(ce2, targetPart, targetPort, OMFUtils.getAllPartsInContext(connectorOwner, null));


        Diagram diagram = OMFUtils.getOpenedDiagram();
        if (null != diagram && withRefresh) {
            InternalDiagramManagement.refreshSinglePort(srcPort, srcPart, diagram);
            InternalDiagramManagement.refreshSinglePort(targetPort, targetPart, diagram);
        }

        return connector;
    }

    public Connector createDirectConnectorPath_singleInstance(Property srcPart, Port srcPort, Property targetPart, Port targetPort, Class connectorOwner, boolean withRefresh, List<Property> availableParts) throws OMFException {
        Connector connector = getMagicDrawFactory().createConnectorInstance();
        ConnectorEnd ce1 = Objects.requireNonNull(ModelHelper.getFirstEnd(connector), "Connector first end is null");
        ConnectorEnd ce2 = Objects.requireNonNull(ModelHelper.getSecondEnd(connector), "Connector second end is null");

        connector.setOwner(connectorOwner);
        ce1.set_connectorOfEnd(connector);
        ce2.set_connectorOfEnd(connector);
        
        setConnectorEnd(ce1, srcPart, srcPort, availableParts);
        setConnectorEnd(ce2, targetPart, targetPort, availableParts);

        Diagram diagram = OMFUtils.getOpenedDiagram();
        if (null != diagram && withRefresh) {
//            InternalDiagramManagement.refreshSinglePort(srcPort, srcPart, diagram);
//            InternalDiagramManagement.refreshSinglePort(targetPort, targetPart, diagram);
        }

        return connector;
    }

    public Connector createDirectConnectorPath(Property srcPart, Port srcPort, Property targetPart, Port targetPort, Class connectorOwner, List<Property> srcPropertyPathList, List<Property> dstPropertyPathList) throws OMFException {
        Connector connector = getMagicDrawFactory().createConnectorInstance();
        ConnectorEnd ce1 = Objects.requireNonNull(ModelHelper.getFirstEnd(connector), "Connector first end is null");
        ConnectorEnd ce2 = Objects.requireNonNull(ModelHelper.getSecondEnd(connector), "Connector second end is null");

        connector.setOwner(connectorOwner);
        ce1.set_connectorOfEnd(connector);
        ce2.set_connectorOfEnd(connector);

        setConnectorEnd(ce1, srcPart, srcPort, srcPropertyPathList);
        setConnectorEnd(ce2, targetPart, targetPort, dstPropertyPathList);

        Diagram diagram = OMFUtils.getOpenedDiagram();
        return connector;
    }


    /**
     * Return the list of ports from a connector list
     * @param connectorsList List<Connector>
     * @return List<Port>
     */
    public List<Port> getPortsListFromConnectorsList(List<Connector> connectorsList){
        return connectorsList.stream()
                .map(Connector::getEnd)
                .flatMap(Collection::stream)
                .map(ConnectorEnd::getRole)
                .filter(end -> end instanceof Port)
                .map(port -> (Port) port)
                .distinct()
                .collect(Collectors.toList());
    }

    //Packages
    public Package getSysMLTypeLibraryPackage() {
        Model sysmlModel = Finder.byNameRecursively().find(OMFUtils.currentProject, Model.class, OMFConstants.SYSML_PACKAGE_NAME);
        return Finder.byNameRecursively().find(sysmlModel, com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package.class, OMFConstants.SYSML_LIBRARY_PACKAGE_NAME);
    }

    private static class OMFHelperHolder {
        private static final OMFFactory instance = new OMFFactory();
    }


}
