package com.samares_engineering.omf.omf_core_framework.utils.group;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.openapi.uml.ReadOnlyElementException;
import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.utils.clone.CloneManager;
import com.samares_engineering.omf.omf_core_framework.utils.clone.ElementGetter;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to group elements:
 * - Group ports by nesting them in a new interface
 */
public class GroupElementHelper {

    ElementGetter elementGetter;
    List<Connector> refactoredConnectors;
    Port newGroupedPort;
    Class newInterface;

    public GroupElementHelper() {
        elementGetter = new ElementGetter();
    }

    /**
     * Group the given ports by nesting them in a new interface.
     * Elements are moved to the new interface, and the connectors are updated to match the new ports
     * Grouped elements can be retrieved with the getters.
     * @param portsToGroup the ports to group
     * @return this
     */
    public GroupElementHelper groupPorts(List<Port> portsToGroup) {
        Element blockOwner = portsToGroup.get(0).getOwner();
        //Creating the new interface and port
        newInterface = SysMLFactory.getInstance().createInterfaceBlock(blockOwner);
        newInterface.setName("Grouped Interface");

        newGroupedPort = SysMLFactory.getInstance().createProxyPort(blockOwner);
        newGroupedPort.setType(newInterface);
        newGroupedPort.setName("-->Grouped Port");

        //Moving all selected ports to the new interface, nesting them
        new ArrayList<>(portsToGroup).forEach(port -> port.setOwner(newInterface));// new ArrayList<>(portsToGroup) is used to avoid ConcurrentModificationException

        //Updating all the connectors to match the new ports (especially the nested ones)
        List<Port> allNestedPorts = elementGetter.getAllNestedPortFromPort(newGroupedPort);
        List<Connector> allConnectorsFromPorts = elementGetter.getAllConnectorsFromPorts(allNestedPorts);
        refactoredConnectors = updatingAllConnectionsForNesting(portsToGroup, allConnectorsFromPorts, newGroupedPort);


        return this;
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
        List<Element> pathEnd = Profile._getSysml().elementPropertyPath().getPropertyPath(endToUpdate);

        boolean isSelectedPortInPropertyPath = selectedMICPorts.stream().anyMatch(pathEnd::contains);
        if (!isSelectedPortInPropertyPath && !selectedMICPorts.contains(roleToUpdate)){
            endToUpdate = ModelHelper.getSecondEnd(connector);
            roleToUpdate = endToUpdate.getRole();
            pathEnd = new ArrayList<>(Profile._getSysml().elementPropertyPath().getPropertyPath(endToUpdate));
        }
        Profile._getSysml().nestedConnectorEnd().apply(endToUpdate); //As the connector is now nested, we need to apply the nested connector end stereotype
        boolean isOneOfTheSelectedPort = selectedMICPorts.contains(roleToUpdate);
        if(isOneOfTheSelectedPort) { //Else it is set with a port, and we don't need to update it
            endToUpdate.setPartWithPort(newGroupedPort);
        }

        //Updating the end property path
        List<Element> finalPathEnd = pathEnd;
        int indexToInsert = selectedMICPorts.stream().filter(port -> finalPathEnd.contains(port)).findFirst().map(pathEnd::indexOf).orElse(-1);
        indexToInsert = indexToInsert == -1 ? pathEnd.size() : indexToInsert;
        pathEnd.add(indexToInsert, newGroupedPort);
        Profile._getSysml().elementPropertyPath().setPropertyPath(endToUpdate, pathEnd);
        return connector;
    }

    public List<Connector> getRefactoredConnectors() {
        return refactoredConnectors;
    }

    public void setRefactoredConnectors(List<Connector> refactoredConnectors) {
        this.refactoredConnectors = refactoredConnectors;
    }

    public Port getNewGroupedPort() {
        return newGroupedPort;
    }

    public void setNewGroupedPort(Port newGroupedPort) {
        this.newGroupedPort = newGroupedPort;
    }

    public Class getNewInterface() {
        return newInterface;
    }

    public void setNewInterface(Class newInterface) {
        this.newInterface = newInterface;
    }
}
