/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams;


import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.properties.PropertyID;
import com.nomagic.magicdraw.properties.PropertyPool;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.layout.composite.CompositeStructureDiagramLayouter;
import com.nomagic.magicdraw.uml.symbols.paths.ConnectorView;
import com.nomagic.magicdraw.uml.symbols.shapes.PartView;
import com.nomagic.magicdraw.uml.symbols.shapes.PortView;
import com.nomagic.magicdraw.uml.symbols.shapes.ShapeElement;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.components.mdbasiccomponents.ConnectorKindEnum;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.utils.ColorPrinter;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFException;

import java.util.*;
import java.util.stream.Collectors;

public class InternalDiagramManagement {
    private InternalDiagramManagement() {}

    /**
     * Refresh port.
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
                for (Port p : block.getOwnedPort()) {
                    boolean shallCreatePortPresentationElement = partPresentationElement.getManipulatedPresentationElements().stream()
                            .filter(ppe -> ppe.getElement().equals(p))
                            .count() == 0;

//                    if (!partPresentationElement.getManipulatedPresentationElements().stream().filter(ppe -> ppe.getElement().equals(p)).iterator().hasNext()) {
                    if (shallCreatePortPresentationElement)
                        manager.createShapeElement(p, partPresentationElement);
                }
            }
            //this seems to work outside of a transaction as well
            diagramPresentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SHOW_OBJECT_CLASS, false));
        } catch (Exception e) {
//            Application.getInstance().getGUILog().log(e.getMessage());
        }

        DiagramUtils.getDiagram(diagram).setSelected(Collections.singletonList(diagramPresentationElement));
    }

    public static void refreshSinglePort(Port portToRefresh, Property mbsePart, Diagram diagram) {
        DiagramPresentationElement diagramPresentationElement = null;
        diagramPresentationElement = DiagramUtils.getDiagram(diagram);

        try {
            PresentationElementsManager manager = PresentationElementsManager.getInstance();
            List<PresentationElement> test = diagramPresentationElement.findPresentationElementsForPathConnecting(mbsePart, PartView.class).collect(Collectors.toList());
//            PresentationElement partPresentationElement = diagramPresentationElement.findPresentationElement(mbsePart, PartView.class);

            for (PresentationElement partPEE : test) {
                boolean shallCreatePortPresentationElement = partPEE.getManipulatedPresentationElements().stream()
                        .filter(ppe -> ppe.getElement().equals(portToRefresh))
                        .count() == 0;
                if (shallCreatePortPresentationElement)
                    manager.createShapeElement(portToRefresh, partPEE);
            }

            //this seems to work outside of a transaction as well
            diagramPresentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SHOW_OBJECT_CLASS, false));

        } catch (Exception e) {
//            Application.getInstance().getGUILog().log(e.getMessage());
        }

        DiagramUtils.getDiagram(diagram).setSelected(Collections.singletonList(diagramPresentationElement));
    }

    public static void refreshEmbeddedPort(Port portToRefresh, Port hostPort, Diagram diagram) {
        DiagramPresentationElement diagramPresentationElement = DiagramUtils.getDiagram(diagram);
        try {
            PresentationElementsManager manager = PresentationElementsManager.getInstance();
            List<PresentationElement> presentationElements = diagramPresentationElement
                    .findPresentationElementsForPathConnecting(hostPort, PortView.class).collect(Collectors.toList());

            for (PresentationElement portPEE : presentationElements) {
                boolean shallCreatePortPresentationElement = portPEE.getManipulatedPresentationElements().stream()
                        .filter(PortView.class::isInstance)
                        .filter(ppe -> ppe.getElement().equals(portToRefresh))
                        .count() == 0;
                ShapeElement shapeElementCreated;
                if (shallCreatePortPresentationElement) {
                    shapeElementCreated = manager.createShapeElement(portToRefresh, portPEE);
                }
            }

            //this seems to work outside of a transaction as well
            diagramPresentationElement.addProperty(PropertyPool.getBooleanProperty(PropertyID.SHOW_OBJECT_CLASS, false));
        } catch (Exception e) {
            ColorPrinter.warn("Error during refresh of embedded port: \n" + e.getMessage());
            OMFErrorHandler.handleException(e, false);

        }
        DiagramUtils.getDiagram(diagram).setSelected(Collections.singletonList(diagramPresentationElement));
    }

    public static void refreshSinglePortInEveryDiagrams(Port portToRefresh, Property mbsePart) {
        for (PresentationElement pe : OMFUtils.currentProject.getSymbolElementMap().getAllPresentationElements(mbsePart)) {
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
        List<PresentationElement> allPresentationElementOfThisPart = OMFUtils.currentProject.getSymbolElementMap().getAllPresentationElements(mbsePart);
        PresentationElementsManager manager = PresentationElementsManager.getInstance();
        for (PresentationElement pePart : allPresentationElementOfThisPart) {
            DiagramPresentationElement dpe = pePart.getDiagramPresentationElement();
            PresentationElement partPresentationElement = dpe.findPresentationElement(mbsePart, PartView.class);
            if (partPresentationElement != null) {
                listPartPresentationElement.add(partPresentationElement);
                dpe.setSelected(listPartPresentationElement);
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
            OMFErrorHandler.handleException(new LayoutException("Error during refresh connector", "refreshAllConnectors"), false);
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
            OMFErrorHandler.handleException(
                    new OMFException("[Refresh] Connection with part not implemented yet", GenericException.ECriticality.ALERT), false);
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
                            OMFErrorHandler.handleException(e, false);
                        }
                    }

                    dstPortViewList.stream()
                            .filter(dstPortPEE -> partParentPEE.equals(dstPortPEE.getParent()) || dstPortPEE.getParent().getParent().getParent().equals(srcPartPEE)) //PartParentPEE equals dst Part
                            .forEach(dstPortPEE -> {
                                PresentationElement connectorPEE = null;
                                try {
                                    connectorPEE = manager.createPathElement(connector, srcPortPEE, dstPortPEE);
                                } catch (ReadOnlyElementException e) {
                                    OMFErrorHandler.handleException(e, false);
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
                            .filter(dstPortPEE -> partParentPEE.equals(dstPortPEE.getParent().getParent().getParent())) //SAME OWNER=> PartParentPEE SRC equals dst PartParentPEE DST
                            .filter(dstPortPEE -> (dstParts.size() > 0 && dstParts.contains(dstPortPEE.getParent().getElement())))
                            .forEach(dstPortPEE -> {
                                PresentationElement connectorPEE = null;
                                try {
                                    connectorPEE = manager.createPathElement(connector, srcPortPEE, dstPortPEE);
                                } catch (ReadOnlyElementException e) {
                                    OMFErrorHandler.handleException(e, false);
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
            OMFErrorHandler.handleException(e, false);
        }
    }

    public static void layoutCompositeInternalDiagram(Diagram diagram) {
        DiagramPresentationElement diagramPresentationElement = DiagramUtils.getDiagram(diagram);
        List<PresentationElement> listPresentationElement = new ArrayList<>();
        diagramPresentationElement.setSelected(listPresentationElement);
        diagramPresentationElement.layout(false, new CompositeStructureDiagramLayouter());
    }
}
