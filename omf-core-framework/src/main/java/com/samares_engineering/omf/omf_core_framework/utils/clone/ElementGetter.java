package com.samares_engineering.omf.omf_core_framework.utils.clone;

import com.nomagic.magicdraw.uml2.Connectors;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ElementGetter {


    /**
     * Get the all the connector elements from the part, including the relationship links.
     * @param part the part to get the connector elements from
     * @return the connector elements
     */
    public Collection<? extends Element> getConnectorElementsFromPart(Property part) {
        Element sharedOwner = part.getOwner();
        Collection<Connector> connectors = part.get_connectorEndOfPartWithPort().stream()
                .map(ConnectorEnd::get_connectorOfEnd)
                .filter(connector -> sharedOwner == connector.getOwner())
                .collect(Collectors.toList());

        return getConnectorElements(connectors);
    }


    /**
     * Get the all the connector elements from the port, including the relationship links.
     * @param port the port to get the connector elements from
     * @return the connector elements
     */
    public Collection<? extends Element> getConnectorFromPort(Port port) {
        Collection<Connector> connectors = Connectors.collectConnectors(port);
        return getConnectorElements(connectors);
    }

    /**
     * Get All connector elements from the connector, including the relationship links.
     * @param connector the connector to get the elements from
     * @return the connector elements
     */
    public List<Element> getConnectorElements(Connector connector) {
        List<Element> connectorElements = getRelationshipsFromElement(connector);
        connectorElements.add(connector);
        return connectorElements;
    }

    /**
     * Get All connector elements from the connectors, including the relationship links.
     * @param connectors the connectors to get the elements from
     * @return the connector elements
     */
    public List<Element> getConnectorElements(Collection<Connector> connectors) {
        return Stream.concat(
                        connectors.stream()
                                .map(this::getRelationshipsFromElement)
                                .flatMap(Collection::stream),
                        connectors.stream())
                .collect(Collectors.toList());
    }


    /**
     * Get all the relationship links from the element (source and target)
     * @param element the element to get the relationship links from
     * @return the relationship links
     */
    public List<Element> getRelationshipsFromElement(Element element) {
        List<Element> traceability = new ArrayList<>(element.get_directedRelationshipOfSource());
        traceability.addAll(element.get_directedRelationshipOfTarget());
        return traceability;
    }


    /**
     * Get all the nested ports from the port using Interfaces, and return them as a new list.
     * @param port the port to get the nested ports from
     * @return a new list containing all nested ports
     */
    public List<Port> getAllNestedPortFromPort(Port port){
        List<Port> portList = new ArrayList<>();
        Class type = (Class) port.getType();
        if (type == null) return portList;

        type.getOwnedPort()
                .forEach(ownedPort -> {
                    portList.add(ownedPort);
                    portList.addAll(getAllNestedPortFromPort(ownedPort));
                });

        return portList;
    }


    /**
     * Get all the connectors from the ports
     * @param list the list of ports to get the connectors from
     * @return the connectors
     */
    public List<Connector> getAllConnectorsFromPorts(List<Port> list) {
        return list.stream().map(Connectors::collectConnectors)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }
    /**
     * Get all the connectors from the ports
     * @param port the port to get the connectors from
     * @return the connectors
     */
    public List<Connector> getAllConnectorsFromPort(Port port) {
        return (List<Connector>) Connectors.collectConnectors(port);
    }
    public Collection<? extends Element> getAllConnectorsFromProperty(Property property) {
        return Connectors.collectConnectors(property);
    }


    /**
     * Get all the traceability relationships from the provided connectors
     * @param connectorList the connectors to get the traceability relationships from
     * @return the traceability relationships
     */
    public Collection<? extends Element> getAllRelationFromConnectors(List<Connector> connectorList) {
        return Stream.concat(
                        connectorList.stream().map(Connector::getClientDependency).flatMap(Collection::stream),
                        connectorList.stream().map(Connector::getSupplierDependency).flatMap(Collection::stream))
                .collect(Collectors.toList());
    }


    /**
     * Get all the traceability relationships from the provided connector
     * @param connector the connector to get the traceability relationships from
     * @return the traceability relationships
     */
    public Collection<? extends Element> getAllRelationFromConnector(Connector connector) {
        return Stream.concat(
                        connector.getClientDependency().stream(),
                        connector.getSupplierDependency().stream())
                .collect(Collectors.toList());
    }


}
