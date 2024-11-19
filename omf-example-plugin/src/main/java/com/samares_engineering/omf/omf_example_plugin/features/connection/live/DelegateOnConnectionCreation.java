/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.connection.live;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction;
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.connections.DelegationConnection;
import com.samares_engineering.omf.omf_core_framework.utils.connections.PartOrdering;
import com.samares_engineering.omf.omf_core_framework.utils.utils.ConnectorUtils;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.LayoutManager;
import com.samares_engineering.omf.omf_example_plugin.features.connection.ConnectionFeatureExample;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Automatically delegates the connection when a new connection is created.
 * Creating new ports, interfaces, and connectors for each layer of the connection.
 */
public class DelegateOnConnectionCreation extends ALiveAction {
    @Override
    protected boolean eventMatches(PropertyChangeEvent evt) {
        ConnectionFeatureExample feature = (ConnectionFeatureExample) getFeature();
        if(!feature.getEnvOptionsHelper().isAutoDelegationOnCreationActivated()) return false;

        return new EventChecker()
                .isElementCreated()
                .isConnector()
                .test(evt);
    }


    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        try {
            Connector connector = (Connector) e.getSource();
            List<Connector> connectors = new DelegationConnection().createConnectionWithDelegation(connector);
            Element owner = connector.getOwner();
            ModelElementsManager.getInstance().removeElement(connector);
            displayConnectedElements(OMFUtils.getProject().getActiveDiagram().getDiagram(), connectors);

        }catch (Exception ex) {
            OMFLogger.err(ex);
        }

        return e;
    }

    /**
     * Display all the elements connected by the connectors:
     * - Refresh the parts
     * - Refresh the ports
     * - Display all the nested ports
     * - Display all the connection between the elements
     * @param diagram the diagram to display the elements
     * @param connectors the connectors to display
     */
    private void displayConnectedElements(Diagram diagram, List<Connector> connectors) {
        DiagramPresentationElement diagramPresentationElement = OMFUtils.getProject().getDiagram(diagram);
        LayoutManager layoutManager = new LayoutManager(diagramPresentationElement);
        List<Property> allPartsToDisplay = connectors.stream()
                .map(ConnectorUtils::getPartsFromConnector)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        ;
        List<Property> orderedPartByDepth = new PartOrdering().getOrderedPartByDepth(diagram.getOwner(), allPartsToDisplay);
        orderedPartByDepth.forEach(layoutManager::refreshPart);
        orderedPartByDepth.forEach(part -> layoutManager.refreshAllPorts((Class) part.getType(), part));
        orderedPartByDepth.stream()
                .map(Property::getType)
                .filter(Objects::nonNull)
                .map(Type::getOwnedElement)
                .flatMap(Collection::stream)
                .filter(Port.class::isInstance)
                .map(Port.class::cast)
                .forEach(layoutManager::displayAllNestedPortRecursively);
        layoutManager.displayAllPaths(layoutManager.getAllDisplayedElements());
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
