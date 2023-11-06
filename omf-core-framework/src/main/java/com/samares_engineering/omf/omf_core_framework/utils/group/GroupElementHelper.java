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
            updateConnectorEndIfNeeded(selectedPorts, ModelHelper.getFirstEnd(connector), newGroupedPort);
            updateConnectorEndIfNeeded(selectedPorts, ModelHelper.getSecondEnd(connector), newGroupedPort);

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
     * Update the connector end if necessary: <br>
     * - If the connector end path contains one of the selected ports <br>
     * - If the connector end role is one of the selected ports <br>
     * The connector end is updated to match the new port (Grouped port)
     * => if the connector is connected to one of the selected ports, its end shall be updated to match the new port (Grouped port)
     * @param selectedPorts the ports to group
     * @param connectorEnd the connector end to update
     * @param newGroupedPort the new port
     */
    private void updateConnectorEndIfNeeded(List<Port> selectedPorts, ConnectorEnd connectorEnd, Port newGroupedPort) {
        ConnectableElement role = connectorEnd.getRole();
        List<Element> propertyPath = Profile._getSysml().elementPropertyPath().getPropertyPath(connectorEnd);
        
        boolean shouldUpdateConnectorEnd = selectedPorts.stream().anyMatch(propertyPath::contains) || selectedPorts.contains(role);
        // Check if the end should be updated
        if (shouldUpdateConnectorEnd) {
            applyNestedConnectorEndStereotype(connectorEnd);
            updatePartWithPortProperty(selectedPorts, connectorEnd, newGroupedPort);
            updateEndPropertyPath(selectedPorts, connectorEnd, propertyPath, newGroupedPort);
        }
    }

    /**
     * Apply the nested connector end stereotype
     * @param connectorEnd the connector end to update
     */
    private void applyNestedConnectorEndStereotype(ConnectorEnd connectorEnd) {
        Profile._getSysml().nestedConnectorEnd().apply(connectorEnd);
    }

    /**
     * Update the part with port property if necessary: <br>
     * - If the connector end role is one of the selected ports <br>
     * => if the connector is connected to one of the selected ports, the part with port property is updated to match the new port (Grouped port)
     * @param selectedPorts the ports to group
     * @param connectorEnd the connector end to update
     * @param newGroupedPort the new port
     */
    private void updatePartWithPortProperty(List<Port> selectedPorts, ConnectorEnd connectorEnd, Port newGroupedPort) {
        boolean isOneOfTheSelectedPort = selectedPorts.contains(connectorEnd.getRole());
        if(isOneOfTheSelectedPort) {
            connectorEnd.setPartWithPort(newGroupedPort);
        }
    }

    /**
     * Update the end property path to match the new port (Grouped port)
     * => Inset the new port (Grouped port) in the property path at the right index
     * @param selectedPorts the ports to group
     * @param connectorEnd the connector end to update
     * @param propertyPath the property path to update
     * @param newGroupedPort the new port
     */
    private void updateEndPropertyPath(List<Port> selectedPorts, ConnectorEnd connectorEnd, List<Element> propertyPath, Port newGroupedPort) {
        int indexToInsert = findInsertionIndex(selectedPorts, propertyPath, newGroupedPort);
        propertyPath.add(indexToInsert, newGroupedPort);
        Profile._getSysml().elementPropertyPath().setPropertyPath(connectorEnd, propertyPath);
    }

    private int findInsertionIndex(List<Port> selectedPorts, List<Element> propertyPath, Port newGroupedPort) {
        return selectedPorts.stream()
                .filter(propertyPath::contains)
                .findFirst()
                .map(propertyPath::indexOf)
                .orElse(propertyPath.size());
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
