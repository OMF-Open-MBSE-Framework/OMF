/*
 * Copyright (c) 2021. Samares-Engineering for Renault.
 * All rights reserved and granted to Renault.
 */

package com.samares_engineering.omf.omf_example_plugin.features.cloneexample.actions;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.ui.ProgressStatusRunner;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFException;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.Clone.CloneManager;
import com.samares_engineering.omf.omf_core_framework.utils.Clone.ElementGetter;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.InternalDiagramManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@DiagramAction
@DeactivateListener
@MDAction(actionName = "Group Ports", category = "Group")
public class GroupPortAction extends AUIAction {

    private ElementGetter elementGetter = new ElementGetter(); //Helper class to retrieve elements from others

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if (selectedElements.size() < 2) return false;
        Element firstPort = selectedElements.get(0);
        boolean portOwnerIsABlock = Profile._getSysml().block().is(firstPort.getOwner());
        return portOwnerIsABlock && selectedElements.stream()
                .allMatch(Port.class::isInstance) && firstPort.getOwner().getOwnedElement().containsAll(selectedElements);
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        List<Port> selectedMICPorts = selectedElements.stream().map(Port.class::cast)
                .collect(Collectors.toList());
        ProgressStatusRunner.runWithProgressStatus(progressStatus -> groupSelectedPorts(selectedMICPorts),
                "Grouping Ports in progress", false, 0);
    }



    protected void groupSelectedPorts(List<Port> selectedPorts) {
        PresentationElement partHost =  getSelectedDiagramPresentationElements().get(1).getParent();
        Element blockOwner = selectedPorts.get(0).getOwner();

        //Creating the new interface and port
        Class newInterface = SysMLFactory.getInstance().createInterfaceBlock(blockOwner);
        newInterface.setName("Grouped Interface");
        
        Port newGroupedPort = SysMLFactory.getInstance().createProxyPort(blockOwner);
        newGroupedPort.setType(newInterface);
        newGroupedPort.setName("-->Grouped Port");

        //Moving all selected ports to the new interface, nesting them
        new ArrayList<>(selectedPorts).forEach(port -> port.setOwner(newInterface));// new ArrayList<>(selectedPorts) is used to avoid ConcurrentModificationException

        //Updating all the connectors to match the new ports (especially the nested ones)
        List<Port> allNestedPorts = elementGetter.getAllNestedPortFromPort(newGroupedPort);
        List<Connector> allConnectorsFromPorts = elementGetter.getAllConnectorsFromPorts(allNestedPorts);
        List<Connector> refactoredConnectors = updatingAllConnectionsForNesting(selectedPorts, allConnectorsFromPorts, newGroupedPort);

        //Displaying all the new representation elements
        refreshAllDiagramPresentationElements(selectedPorts, refactoredConnectors, partHost, newGroupedPort);
    }

    /**
     * Update all the connectors to match the new ports (especially the nested ones)
     * NOTE: This method is using the CloneManager because the original connectors could not been modified <br>
     * So they are cloned, modified, and the original ones are deleted
     * @param selectedPorts the ports to group
     * @param allConnectorsFromPorts the connectors to update
     * @param newGroupedPort the new port
     * @return the new connectors
     */
    private List<Connector> updatingAllConnectionsForNesting(List<Port> selectedPorts, List<Connector> allConnectorsFromPorts, Port newGroupedPort) {
        List<Connector> newConnectors = new ArrayList<>();
        for (Connector connector : allConnectorsFromPorts) {
            //Cloning the connector
            CloneManager cloneManager = new CloneManager("");
            cloneManager.clonedConnector(connector);
            Connector newConnector = (Connector) cloneManager.retrieveClonedElement(connector);
            newConnectors.add(newConnector);

            //Updating the connector to match the new port
            updateConnectorToMatchNewSourcePort(selectedPorts, newConnector, newGroupedPort);

            //Deleting the original connector
            try {
                ModelElementsManager.getInstance().removeElement(connector);}
            catch (ReadOnlyElementException e) {
                OMFErrorHandler.handleException(
                        new OMFException("Cannot finalize the port grouping due to a ReadOnly Connector",
                                e, GenericException.ECriticality.CRITICAL), true);
            }
        }

        return newConnectors;
    }

    /**
     * Refresh all the diagram representation elements:
     * - Deleting the previous representation elements
     * - Displaying the newport, and all the nested ports
     * - Displaying all the connectors (including the nested ones)
     * @param selectedPorts the ports to group
     * @param allConnectorsFromPorts the connectors to update
     * @param partHost the part host
     * @param newPort the new port
     */
    private void refreshAllDiagramPresentationElements(List<Port> selectedPorts, List<Connector> allConnectorsFromPorts,
                                                       PresentationElement partHost, Port newPort) {
        DiagramPresentationElement activeDiagram = getDiagramAction().getDiagram();

        //Deleting the previous representation elements
        deletePreviousRepresentationElements(selectedPorts, allConnectorsFromPorts);

        //Displaying the newport, and all the nested ports
        List<PresentationElement> portsPEList = new ArrayList<>();
        portsPEList.addAll(InternalDiagramManagement.refreshSinglePort(newPort, (Property) partHost.getElement(), activeDiagram.getDiagram()));
        portsPEList.addAll(InternalDiagramManagement.displayAllNestedPortRecursively(newPort, activeDiagram));

        //Displaying all the connectors (including the nested ones)
        InternalDiagramManagement.displayPath(portsPEList);

    }

    /**
     * Delete the old representation elements:
     * - The selected and grouped ports
     * - The connectors connected to them
     * @param portsToDelete the ports to delete
     * @param listConnectors the connectors to delete
     */
    private void deletePreviousRepresentationElements(List<Port> portsToDelete, List<Connector> listConnectors) {
        DiagramPresentationElement diagramPE = getDiagramAction().getDiagram();
        listConnectors.forEach(connector -> InternalDiagramManagement.deleteRepresentationElement(connector, diagramPE));
        portsToDelete.forEach(port -> InternalDiagramManagement.deleteRepresentationElement(port, diagramPE));
    }

    /**
     * Actual update of the connector to match the new port:
     * - We determine which end of the connector is the one to update (the one connected to the other port)
     * - We update the connector to match the new port by replacing the attribute with the original one
     * @param selectedMICPorts the ports to group
     * @param connector the connector to update
     * @param newGroupedPort the new port
     * @return the updated connector
     */
    private Connector updateConnectorToMatchNewSourcePort(List<Port> selectedMICPorts, Connector connector, Port newGroupedPort) {
       //We determine which end of the connector is the one to update (the one connected to the other port)
        ConnectorEnd endToUpdate = ModelHelper.getFirstEnd(connector);
        ConnectableElement roleToUpdate = endToUpdate.getRole();
        //TODO: this is not working maybe use property path
        if (!selectedMICPorts.contains(roleToUpdate)) {
            endToUpdate = ModelHelper.getSecondEnd(connector);
            roleToUpdate = endToUpdate.getRole();
        }
        Profile._getSysml().nestedConnectorEnd().apply(endToUpdate); //As the connector is now nested, we need to apply the nested connector end stereotype
        boolean isOneOfTheSelectedPort = selectedMICPorts.contains(roleToUpdate);
        if(isOneOfTheSelectedPort) { //Else it is set with a port, and we don't need to update it
            endToUpdate.setPartWithPort(newGroupedPort);
        }

        //Updating the end property path
        List<Element> pathFirstEnd = new ArrayList<>(Profile._getSysml().elementPropertyPath().getPropertyPath(endToUpdate));

        int indexToInsert = selectedMICPorts.stream().filter(port -> pathFirstEnd.contains(port)).findFirst().map(pathFirstEnd::indexOf).orElse(-1);
        indexToInsert = indexToInsert == -1 ? pathFirstEnd.size() : indexToInsert;
        pathFirstEnd.add(indexToInsert, newGroupedPort);
        Profile._getSysml().elementPropertyPath().setPropertyPath(endToUpdate, pathFirstEnd);
        return connector;
    }

}
