/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams;


import com.nomagic.magicdraw.context.PropertyPathChangeManager;
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyPool;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.DisplayPathElements;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.layout.composite.CompositeStructureDiagramLayouter;
import com.nomagic.magicdraw.uml.symbols.paths.ConnectorView;
import com.nomagic.magicdraw.uml.symbols.shapes.PartView;
import com.nomagic.magicdraw.uml.symbols.shapes.PortView;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.components.mdbasiccomponents.ConnectorKindEnum;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class InternalDiagramManagement {
    private InternalDiagramManagement() {}

    /**
     * Display all ports of a part in a diagram
     *
     * @param block   the block
     * @param part    the part
     * @param diagram the diagram
     */
    public static void refreshAllPorts(Class block, Property part, Diagram diagram) {
        DiagramPresentationElement diagramPresentationElement = null;
        diagramPresentationElement = DiagramUtils.getDiagram(diagram);

        try {
            PresentationElementsManager manager = PresentationElementsManager.getInstance();
            PresentationElement partPresentationElement = diagramPresentationElement.findPresentationElement(part, PartView.class);

            if (partPresentationElement != null) { // PresentationElement found
                for (Port port : block.getOwnedPort()) {
                    boolean shallCreatePortPresentationElement = partPresentationElement.getManipulatedPresentationElements().stream().noneMatch(ppe -> Objects.equals(ppe.getElement(), port));

//                    if (!partPresentationElement.getManipulatedPresentationElements().stream().filter(ppe -> ppe.getElement().equals(port)).iterator().hasNext()) {
                    if (shallCreatePortPresentationElement)
                        manager.createShapeElement(port, partPresentationElement);
                }
            }
            //this seems to work outside of a transaction as well
            diagramPresentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SHOW_OBJECT_CLASS, false));
        } catch (Exception e) {
//            Application.getInstance().getGUILog().log(e.getMessage());
        }

        setSelectedElements(DiagramUtils.getDiagram(diagram), Collections.singletonList(diagramPresentationElement));
    }

    /**
     * Display all ports hosted by a property in a diagram
     * @param portToRefresh the port to refresh
     * @param propertyHostingThePort the property hosting the port
     * @param diagram the diagram
     * @return the list of displayed ports
     */
    public static List<PresentationElement> refreshSinglePort(Port portToRefresh, Property propertyHostingThePort, Diagram diagram) {
        DiagramPresentationElement diagramPresentationElement = DiagramUtils.getDiagram(diagram);
        List<PresentationElement> displayedPorts = new ArrayList<>();
        try {
            PresentationElementsManager manager = PresentationElementsManager.getInstance();
            List<PresentationElement> allPartsPEFound = diagramPresentationElement.findPresentationElementsForPathConnecting(propertyHostingThePort, PartView.class).collect(Collectors.toList());
            allPartsPEFound.addAll(diagramPresentationElement.findPresentationElementsForPathConnecting(propertyHostingThePort, PortView.class).collect(Collectors.toList()));
            for (PresentationElement partPEE : allPartsPEFound) {
                List<PresentationElement> manipuledPEE = partPEE.getManipulatedPresentationElements();

                boolean shallCreatePortPresentationElement = manipuledPEE.stream()
                        .map(PresentationElement::getElement)
                        .filter(Objects::nonNull).noneMatch(portToRefresh::equals);
                if (shallCreatePortPresentationElement)
                    displayedPorts.add(manager.createShapeElement(portToRefresh, partPEE));
            }

            diagramPresentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SHOW_OBJECT_CLASS, false));

        } catch (Exception e) {
            LegacyErrorHandler.handleException(new LayoutException("Error during port: " + portToRefresh.getHumanName() + " displaying, please refresh it manually", e), false);
        }

        setSelectedElements(DiagramUtils.getDiagram(diagram), displayedPorts);
        return displayedPorts;
    }

    private static void setSelectedElements(DiagramPresentationElement diagram, List<PresentationElement> diagramPresentationElement) {
        diagram.setSelected(diagramPresentationElement);
    }

    public static List<PresentationElement> refreshEmbeddedPort(Port portToRefresh, Port hostPort, Diagram diagram) {
        DiagramPresentationElement diagramPresentationElement = DiagramUtils.getDiagram(diagram);
        List<PresentationElement> displayedPorts = new ArrayList<>();
        try {
            PresentationElementsManager manager = PresentationElementsManager.getInstance();
            List<PresentationElement> presentationElements = diagramPresentationElement
                    .findPresentationElementsForPathConnecting(hostPort, PortView.class).collect(Collectors.toList());

            for (PresentationElement portPEE : presentationElements) {
                boolean shallCreatePortPresentationElement = portPEE.getManipulatedPresentationElements().stream()
                        .filter(PortView.class::isInstance).noneMatch(ppe -> Objects.equals(ppe.getElement(), portToRefresh)); //Don't already exist under the hostPort

                if (shallCreatePortPresentationElement) {
                    displayedPorts.add(manager.createShapeElement(portToRefresh, portPEE));
                }
            }

            diagramPresentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SHOW_OBJECT_CLASS, false));
        } catch (Exception e) {
            LegacyErrorHandler.handleException(new LayoutException("Error during refresh of embedded port", e), false);
        }

        setSelectedElements(DiagramUtils.getDiagram(diagram), displayedPorts);
        return displayedPorts;
    }

    public static Collection<? extends PresentationElement> displayAllNestedPortRecursively(Port firstPort, DiagramPresentationElement diagramPE){
        List<PresentationElement> allDisplayedPorts = new ArrayList<>();
        List<PresentationElement> allPortToExpands = diagramPE.findPresentationElementsForPathConnecting(firstPort, PortView.class).collect(Collectors.toList());
        Class typeInterface = (Class) firstPort.getType();

        for (PresentationElement hostingPortPE : allPortToExpands) {
            List<PresentationElement> PEUnderHostingPort = hostingPortPE.getManipulatedPresentationElements();
            Predicate<Port> doesNotExist = nestedPort -> diagramPE.findPresentationElementsForPathConnecting(nestedPort, PortView.class)
                    .allMatch(nestedPortPE -> !PEUnderHostingPort.contains(nestedPortPE));

            typeInterface.getOwnedPort().stream()
                    .filter(doesNotExist).forEach(nestedPort -> {
                        PresentationElement nestedPortPE = createPortShapeElement(nestedPort, hostingPortPE);
                        allDisplayedPorts.add(nestedPortPE);
                        allDisplayedPorts.addAll(displayAllNestedPortRecursively(nestedPort, diagramPE));
                    });
        }

        return allDisplayedPorts;
    }

    private static PresentationElement createPortShapeElement(Port nestedPort, PresentationElement hostingPortPE) {
        try {
            PresentationElementsManager manager = PresentationElementsManager.getInstance();
            return manager.createShapeElement(nestedPort, hostingPortPE);
        } catch (ReadOnlyElementException e) {
            LegacyErrorHandler.handleException(new LayoutException("Error during creation of nested port", "createPortShapeElement"), false);
        }
        return null;
    }

    public static void refreshSinglePortInEveryDiagrams(Port portToRefresh, Property mbsePart) {
        for (PresentationElement pe : OMFUtils.getProject().getSymbolElementMap().getAllPresentationElements(mbsePart)) {
            Diagram diagram = pe.getDiagramPresentationElement().getDiagram();
            refreshSinglePort(portToRefresh, mbsePart, diagram);
        }
    }

    public static void refreshPortInDiagramFrontier(Port portToRefresh, Diagram diagram) {
        DiagramPresentationElement diagramPresentationElement = DiagramUtils.getDiagram(diagram);

        try {
            PresentationElementsManager manager = PresentationElementsManager.getInstance();
            PresentationElement portPresentationElement = diagramPresentationElement.findPresentationElement(portToRefresh, PortView.class);

            if (portPresentationElement == null) {
                manager.createShapeElement(portToRefresh, diagramPresentationElement.getDiagramFrame());
            }

        } catch (Exception e) {
//            Application.getInstance().getGUILog().log(e.getMessage());
        }
    }

    public static void layoutSinglePart(Property mbsePart, Diagram currentDiagram) {
        List<PresentationElement> listPartPresentationElement = new ArrayList<>();
        List<PresentationElement> allPresentationElementOfThisPart = OMFUtils.getProject().getSymbolElementMap().getAllPresentationElements(mbsePart);
        PresentationElementsManager manager = PresentationElementsManager.getInstance();
        for (PresentationElement pePart : allPresentationElementOfThisPart) {
            DiagramPresentationElement dpe = pePart.getDiagramPresentationElement();
            PresentationElement partPresentationElement = dpe.findPresentationElement(mbsePart, PartView.class);
            if (partPresentationElement != null) {
                listPartPresentationElement.add(partPresentationElement);
                setSelectedElements(dpe, listPartPresentationElement);
                dpe.layout(false, new CompositeStructureDiagramLayouter());
            }
            listPartPresentationElement.clear();
        }
    }


    public static void refreshAllConnectors(List<Connector> createdConnectors, Diagram diagram) {
        try {
            //displaysPorts
            List<Port> connectedPorts = createdConnectors.stream()
                    .map(Connector::getEnd)
                    .flatMap(Collection::stream)
                    .map(ConnectorEnd::getRole)
                    .filter(Port.class::isInstance)
                    .map(Port.class::cast)
                    .distinct()
                    .collect(Collectors.toList());


            Set<Property> usedPartsSet = new HashSet<>(OMFUtils.getAllPartsInContext((Class) diagram.getOwner(), null));

            connectedPorts
                    .forEach(port -> usedPartsSet.stream()
                            .filter(part -> part.getType() == port.getOwner())
                            .forEach(part -> refreshSinglePort(port, part, diagram)));

            createdConnectors.stream().forEach(connector -> refreshConnector(connector, diagram));
        } catch (Exception e) {
            LegacyErrorHandler.handleException(new LayoutException("Error during refresh connector", "refreshAllConnectors"), false);
        }


    }

    public static void refreshConnector(Connector connector, Diagram diagram) {

        DiagramPresentationElement diagramPresentationElement = DiagramUtils.getDiagram(diagram);
        ConnectorEnd firstEnd = Objects.requireNonNull(ModelHelper.getFirstEnd(connector), "Connector first end is null");
        ConnectorEnd secondEnd = Objects.requireNonNull(ModelHelper.getSecondEnd(connector), "Connector second end is null");


        List<Property> srcParts = OMFUtils.getPropertyPathListFromConnectorEnd(firstEnd);
        List<Property> dstParts = OMFUtils.getPropertyPathListFromConnectorEnd(secondEnd);

        boolean isSrcAPort = firstEnd.getRole() instanceof Port;
        boolean isDstAPort = secondEnd.getRole() instanceof Port;

        if (!isSrcAPort || !isDstAPort) {    //TODO improve ErrorManagement
            LegacyErrorHandler.handleException(
                    new LegacyOMFException("[Refresh] Connection with part not implemented yet", GenericException.ECriticality.ALERT), false);
            return;
        }

        Port srcPort = (Port) firstEnd.getRole();
        Port dstPort = (Port) secondEnd.getRole();

        PresentationElementsManager manager = PresentationElementsManager.getInstance();
        PresentationElement connectorPresentationElement = diagramPresentationElement.findPresentationElement(connector, ConnectorView.class);

        List<PresentationElement> srcPortViewList = diagramPresentationElement.findPresentationElementsForPathConnecting(srcPort, PortView.class).collect(Collectors.toList());
        List<PresentationElement> dstPortViewList = diagramPresentationElement.findPresentationElementsForPathConnecting(dstPort, PortView.class).collect(Collectors.toList());


        try {

            if (connector.getKind() == ConnectorKindEnum.DELEGATION) {    //shall create ALL delegation connectors
                /** A1Bis get as potential src... seems strange */
                //getParent => PortPEE->PartPEE->ContainerClassfierPEE->PartPEE->etc

                Element diagramOwner = DiagramUtils.getOpenedDiagram().getOwner();
                Element commonAncestor = diagramOwner;

                final boolean isSrcDiagramOwner = (srcPort.getOwner() == diagramOwner);
                final boolean isDstDiagramOwner = (dstPort.getOwner() == diagramOwner);
                final boolean connectedToDiagramBorder = isSrcDiagramOwner || isDstDiagramOwner;


                for (PresentationElement srcPortPEE : srcPortViewList) {
                    PresentationElement srcPartPEE = srcPortPEE.getParent();
                    PresentationElement partParentPEE = srcPartPEE.getParent().getParent();

                    boolean isNotPartOfTheConnectedParts = (srcParts.size() > 0 && !srcParts.contains(srcPartPEE.getElement())); //exception on case parent->son when no PropertyPath found
                    if (isNotPartOfTheConnectedParts)
                        continue;

                    //DIAGRAM BORDER CASES
                    if (connectedToDiagramBorder) {
                        PresentationElement dstPortPEE = null;

                        if (isSrcDiagramOwner)
                            dstPortPEE = (dstPortViewList.size() == 0) ? //if no shape => create new one
                                    manager.createShapeElement(dstPort, diagramPresentationElement.getDiagramFrame())
                                    :
                                    dstPortViewList.stream()
                                            .filter(dstPortPE -> dstParts.contains(dstPortPE.getParent().getElement()))
                                            .findFirst().get();

                        if (isDstDiagramOwner)
                            dstPortPEE = (dstPortViewList.size() == 0) ? //if no shape => create new one
                                    manager.createShapeElement(dstPort, diagramPresentationElement.getDiagramFrame())
                                    : //else select the first in the list (no ambiguity)
                                    dstPortViewList.stream().findFirst().get();


                        try {
                            manager.createPathElement(connector, srcPortPEE, dstPortPEE);
                        } catch (ReadOnlyElementException e) {
                            LegacyErrorHandler.handleException(e, false);
                        }
                    }

                    dstPortViewList.stream()
                            .filter(dstPortPEE -> partParentPEE.equals(dstPortPEE.getParent()) || dstPortPEE.getParent().getParent().getParent().equals(srcPartPEE)) //PartParentPEE equals dst Part
                            .forEach(dstPortPEE -> {
                                PresentationElement connectorPEE = null;
                                try {
                                    connectorPEE = manager.createPathElement(connector, srcPortPEE, dstPortPEE);
                                } catch (ReadOnlyElementException e) {
                                    LegacyErrorHandler.handleException(e, false);
                                }
                            });
                }

            } else {  //ASSEMBLY

                PresentationElement portSourceView = diagramPresentationElement.findPresentationElement(srcPort, PortView.class);
                PresentationElement portSupplierView = diagramPresentationElement.findPresentationElement(dstPort, PortView.class);

                for (PresentationElement srcPortPEE : srcPortViewList) {
                    PresentationElement srcPartPEE = srcPortPEE.getParent();
                    PresentationElement partParentPEE = srcPartPEE.getParent().getParent();

                    boolean isNotPartOfTheConnectedParts = !(srcParts.size() > 0 && srcParts.contains(srcPartPEE.getElement())); //exception on case parent->son when no PropertyPath found
                    if (isNotPartOfTheConnectedParts)
                        continue;
                    //NOT WORKING WITH NESTED PORTS: hypothesis was, port.getParent.getParent was a part, which is not the case when connecting nested ports
                    dstPortViewList.stream()
                            .filter(dstPortPEE -> partParentPEE.equals(dstPortPEE.getParent().getParent().getParent())) //SAME OWNER=> PartParentPEE SRC equals dst PartParentPEE DST
                            .filter(dstPortPEE -> (dstParts.size() > 0 && dstParts.contains(dstPortPEE.getParent().getElement())))
                            .forEach(dstPortPEE -> {
                                PresentationElement connectorPEE = null;
                                try {
                                    connectorPEE = manager.createPathElement(connector, srcPortPEE, dstPortPEE);
                                } catch (ReadOnlyElementException e) {
                                    LegacyErrorHandler.handleException(e, false);
                                }
                            });
                }
            }


            //SHOW ME RED
//
//            diagramPresentationElement.findPresentationElementsForPathConnecting(connector, ConnectorView.class)
//                    .forEach(connectorPEE->{
//                        connectorPEE.setLineColor(Color.BLUE);
//                        connectorPEE.setLineWidth(3);
//                    });
            //this seems to work outside of a transaction as well
            diagramPresentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SHOW_OBJECT_CLASS, false));
        } catch (Exception e) {
            LegacyErrorHandler.handleException(e, false);
        }
    }

    public static void displayPath(Collection<PresentationElement> presentationElements){
        String var0 = "CREATE_PATHS";
        if (presentationElements != null && !presentationElements.isEmpty()) {
            PropertyPathChangeManager.getInstance(OMFUtils.getProject()).executeWithoutUpdatingPropertyPath(() -> {
                createConnectorPaths(var0, presentationElements);
            });
        }
    }

    private static void createConnectorPaths(String var0, Collection<PresentationElement> peWithPathToDisplay) {
        DisplayPathElements.DisplayPathOptions displayPathOption = new DisplayPathElements.DisplayPathOptions();
        if ("CREATE_PATHS".equals(var0)) {
            displayPathOption.setDisplayPathsToSelf(true);
        } else {
            peWithPathToDisplay = peWithPathToDisplay.size() > 1 ? peWithPathToDisplay : null;
            displayPathOption.setOnlyBetweenThese(peWithPathToDisplay);
            displayPathOption.setDisplayPathsToSelf(false);
        }

        DisplayPathElements.displayPathElements(peWithPathToDisplay, displayPathOption);
    }

    public static void refreshConnector2(Connector connector, Diagram diagram) {

        DiagramPresentationElement diagramPresentationElement = DiagramUtils.getDiagram(diagram);
        ConnectorEnd firstEnd = Objects.requireNonNull(ModelHelper.getFirstEnd(connector), "Connector first end is null");
        ConnectorEnd secondEnd = Objects.requireNonNull(ModelHelper.getSecondEnd(connector), "Connector second end is null");


        List<Property> srcParts = OMFUtils.getPropertyPathListFromConnectorEnd(firstEnd);
        List<Property> dstParts = OMFUtils.getPropertyPathListFromConnectorEnd(secondEnd);

        boolean isSrcAPort = firstEnd.getRole() instanceof Port;
        boolean isDstAPort = secondEnd.getRole() instanceof Port;

        if (!isSrcAPort || !isDstAPort) {    //TODO improve ErrorManagement
            LegacyErrorHandler.handleException(
                    new LegacyOMFException("[Refresh] Connection with part not implemented yet", GenericException.ECriticality.ALERT), false);
            return;
        }

        Port srcPort = (Port) firstEnd.getRole();
        Port dstPort = (Port) secondEnd.getRole();

        PresentationElementsManager manager = PresentationElementsManager.getInstance();
        PresentationElement connectorPresentationElement = diagramPresentationElement.findPresentationElement(connector, ConnectorView.class);

        List<PresentationElement> srcPortViewList = diagramPresentationElement.findPresentationElementsForPathConnecting(srcPort, PortView.class).collect(Collectors.toList());
        List<PresentationElement> dstPortViewList = diagramPresentationElement.findPresentationElementsForPathConnecting(dstPort, PortView.class).collect(Collectors.toList());


        try {

            if (connector.getKind() == ConnectorKindEnum.DELEGATION) {    //shall create ALL delegation connectors
                /** A1Bis get as potential src... seems strange */
                //getParent => PortPEE->PartPEE->ContainerClassfierPEE->PartPEE->etc

                Element diagramOwner = DiagramUtils.getOpenedDiagram().getOwner();
                Element commonAncestor = diagramOwner;

                final boolean isSrcDiagramOwner = (srcPort.getOwner() == diagramOwner);
                final boolean isDstDiagramOwner = (dstPort.getOwner() == diagramOwner);
                final boolean connectedToDiagramBorder = isSrcDiagramOwner || isDstDiagramOwner;


                for (PresentationElement srcPortPEE : srcPortViewList) {
                    PresentationElement srcPartPEE = srcPortPEE.getParent();
                    PresentationElement partParentPEE = srcPartPEE.getParent().getParent();

                    boolean isNotPartOfTheConnectedParts = (srcParts.size() > 0 && !srcParts.contains(srcPartPEE.getElement())); //exception on case parent->son when no PropertyPath found
                    if (isNotPartOfTheConnectedParts)
                        continue;

                    //DIAGRAM BORDER CASES
                    if (connectedToDiagramBorder) {
                        PresentationElement dstPortPEE = null;

                        if (isSrcDiagramOwner)
                            dstPortPEE = (dstPortViewList.size() == 0) ? //if no shape => create new one
                                    manager.createShapeElement(dstPort, diagramPresentationElement.getDiagramFrame())
                                    :
                                    dstPortViewList.stream()
                                            .filter(dstPortPE -> dstParts.contains(dstPortPE.getParent().getElement()))
                                            .findFirst().get();

                        if (isDstDiagramOwner)
                            dstPortPEE = (dstPortViewList.size() == 0) ? //if no shape => create new one
                                    manager.createShapeElement(dstPort, diagramPresentationElement.getDiagramFrame())
                                    : //else select the first in the list (no ambiguity)
                                    dstPortViewList.stream().findFirst().get();


                        try {
                            manager.createPathElement(connector, srcPortPEE, dstPortPEE);
                        } catch (ReadOnlyElementException e) {
                            LegacyErrorHandler.handleException(e, false);
                        }
                    }

                    dstPortViewList.stream()
                            .filter(dstPortPEE -> partParentPEE.equals(dstPortPEE.getParent()) || dstPortPEE.getParent().getParent().getParent().equals(srcPartPEE)) //PartParentPEE equals dst Part
                            .forEach(dstPortPEE -> {
                                PresentationElement connectorPEE = null;
                                try {
                                    connectorPEE = manager.createPathElement(connector, srcPortPEE, dstPortPEE);
                                } catch (ReadOnlyElementException e) {
                                    LegacyErrorHandler.handleException(e, false);
                                }
                            });
                }

            } else {  //ASSEMBLY

                PresentationElement portSourceView = diagramPresentationElement.findPresentationElement(srcPort, PortView.class);
                PresentationElement portSupplierView = diagramPresentationElement.findPresentationElement(dstPort, PortView.class);

                for (PresentationElement srcPortPEE : srcPortViewList) {
                    PresentationElement srcPartPEE = srcPortPEE.getParent();
                    PresentationElement partParentPEE = srcPartPEE.getParent().getParent();

                    boolean isNotPartOfTheConnectedParts = !(srcParts.size() > 0 && srcParts.contains(srcPartPEE.getElement())); //exception on case parent->son when no PropertyPath found
                    if (isNotPartOfTheConnectedParts)
                        continue;

                    dstPortViewList.stream()
//                            .filter(dstPortPEE -> partParentPEE.equals(dstPortPEE.getParent().getParent().getParent())) //SAME OWNER=> PartParentPEE SRC equals dst PartParentPEE DST
                            .filter(dstPortPEE -> (dstParts.size() > 0 && dstParts.contains(dstPortPEE.getParent().getElement()))) //Src and DST shall not be on the same propertyPath
                            .forEach(dstPortPEE -> {
                                PresentationElement connectorPEE = null;
                                try {
                                    connectorPEE = manager.createPathElement(connector, srcPortPEE, dstPortPEE);
                                } catch (ReadOnlyElementException e) {
                                    LegacyErrorHandler.handleException(e, false);
                                }
                            });
                }
            }


            //SHOW ME RED
//
//            diagramPresentationElement.findPresentationElementsForPathConnecting(connector, ConnectorView.class)
//                    .forEach(connectorPEE->{
//                        connectorPEE.setLineColor(Color.BLUE);
//                        connectorPEE.setLineWidth(3);
//                    });
            //this seems to work outside of a transaction as well
            diagramPresentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SHOW_OBJECT_CLASS, false));
        } catch (Exception e) {
            LegacyErrorHandler.handleException(e, false);
        }
    }

    public static void deleteRepresentationElement(Port port, DiagramPresentationElement diagramPE) {
        diagramPE.findPresentationElementsForPathConnecting(port, PortView.class)
                .forEach(InternalDiagramManagement::deletePresentationElement);
    }

    public static void deleteRepresentationElement(Connector connector, DiagramPresentationElement diagramPE) {
        PresentationElement presentationElement = diagramPE.findPresentationElement(connector, ConnectorView.class);
        deletePresentationElement(presentationElement);
    }

    private static void deletePresentationElement(PresentationElement presentationElement) {
        if (presentationElement != null) {
            try {
                PresentationElementsManager.getInstance().deletePresentationElement(presentationElement);
            } catch (Exception e) {
                LegacyErrorHandler.handleException(new LayoutException("Error during PresentationElement deletion"), false);
            }
        }
    }

    public static void layoutCompositeInternalDiagram(Diagram diagram) {
        DiagramPresentationElement diagramPresentationElement = DiagramUtils.getDiagram(diagram);
        List<PresentationElement> listPresentationElement = new ArrayList<>();
        setSelectedElements(diagramPresentationElement, listPresentationElement);
        diagramPresentationElement.layout(false, new CompositeStructureDiagramLayouter());
    }
}
