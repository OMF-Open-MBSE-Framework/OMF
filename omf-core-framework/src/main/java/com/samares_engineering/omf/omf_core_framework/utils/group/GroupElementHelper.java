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
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
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
            cloneManager.cloneConnector(connector);
            Connector newConnector = (Connector) cloneManager.retrieveClonedElement(connector);
            newConnectors.add(newConnector);

            //Updating the connector to match the new port
            updateConnectorToMatchNewSourcePort(selectedPorts, newConnector, newGroupedPort);

            //Deleting the original connector
            try {
                ModelElementsManager.getInstance().removeElement(connector);}
            catch (ReadOnlyElementException e) {
                throw new OMFCriticalException("Cannot finalize the port grouping due to a ReadOnly Connector",
                                e);
            }
        }

        return newConnectors;
    }

    private ConnectorEnd updateConnectorEnd(ConnectorEnd connectorEnd, List<Port> selectedPorts, Port newGroupedPort) {
        ConnectableElement role = connectorEnd.getRole();
        List<Element> path = Profile._getSysml().elementPropertyPath().getPropertyPath(connectorEnd);

        boolean isEndConnectedToSelectedPort = selectedPorts.stream().anyMatch(port -> path.contains(port) || port.equals(role));
        if (isEndConnectedToSelectedPort) {
            Profile._getSysml().nestedConnectorEnd().apply(connectorEnd); // Apply the nested connector end stereotype
            if (selectedPorts.contains(role)) {
                connectorEnd.setPartWithPort(newGroupedPort); // Update the part with port if it's one of the selected ports
            }

            // Update the end property path
            int indexToInsert = selectedPorts.stream()
                    .filter(path::contains)
                    .findFirst()
                    .map(path::indexOf)
                    .orElse(path.size());

            path.add(indexToInsert, newGroupedPort);
            Profile._getSysml().elementPropertyPath().setPropertyPath(connectorEnd, path);
        }
        return connectorEnd;
    }

    private Connector updateConnectorToMatchNewSourcePort(List<Port> selectedPorts, Connector connector, Port newGroupedPort) {
        ConnectorEnd firstEnd = ModelHelper.getFirstEnd(connector);
        ConnectorEnd secondEnd = ModelHelper.getSecondEnd(connector);

        updateConnectorEnd(firstEnd, selectedPorts, newGroupedPort);
        updateConnectorEnd(secondEnd, selectedPorts, newGroupedPort);

        return connector; // Assuming the connectorEnd changes are reflected in the connector itself
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
