package com.samares_engineering.omf.omf_core_framework.utils.Clone;

import com.nomagic.magicdraw.uml2.Connectors;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;

import java.util.*;
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
     * @param element
     * @return
     */
    public List<Element> getRelationshipsFromElement(Element element) {
        List<Element> traceability = new ArrayList<>(element.get_directedRelationshipOfSource());
        traceability.addAll(element.get_directedRelationshipOfTarget());
        return traceability;
    }

    /**
     * Get all the connected nested ports from the port
     * @param port
     * @return
     */
    public List<Port> getAllConnectedNestedPorts(Port port) {
        List<Port> allPortFromInterface = new ArrayList<>();
        getAllNestedPortFromPort(port, allPortFromInterface);
        allPortFromInterface.add(port);
        return allPortFromInterface;
    }

    /**
     * Get all the nested ports from the port using Interfaces, and add them to the list
     * @param port the port to get the nested ports from
     * @param portList the list of nested ports
     */
    public void getAllNestedPortFromPort(Port port, List<Port> portList){
        ((Class) port.getType()).getOwnedPort()
                .stream()
                .forEach(ownedPort  ->{ portList.add(ownedPort); getAllNestedPortFromPort(ownedPort, portList);});

    }


    /**
     * Get all the connectors from the ports
     * @param list
     * @return
     */
    public List<Connector> getAllConnectorsFromPorts(List<Port> list) {
        return list.stream().map(Connectors::collectConnectors)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

}
