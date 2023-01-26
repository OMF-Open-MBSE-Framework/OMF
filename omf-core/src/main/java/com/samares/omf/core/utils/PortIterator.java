/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */

package com.samares.omf.core.utils;

import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;

import java.util.*;
import java.util.stream.Collectors;

public class PortIterator implements Iterator<List<ConnectableElement>> {

    private final Set<Connector> edgeDone;
    private List<ConnectableElement> connectableElementList;

    public PortIterator(List<ConnectableElement> connectableElementList){
        this.connectableElementList =  connectableElementList;
        edgeDone = new HashSet<>();
    }

    @Override
    public boolean hasNext() {
        return connectableElementList
                .stream()
                .map(ConnectableElement::getEnd)
                .flatMap(Collection::stream)
                .map(ConnectorEnd::get_connectorOfEnd)
                .filter(connector -> !edgeDone.contains(connector))
                .count() > 0;
    }

    @Override
    public List<ConnectableElement> next() {
        List<ConnectorEnd> l_oppositeEnd = connectableElementList
                .stream()
                .map(ConnectableElement::getEnd)
                .flatMap(Collection::stream)
                .map(ModelHelper::getOppositeEnd)
                .filter(connectorEnd -> !edgeDone.contains(connectorEnd.get_connectorOfEnd()))
                .collect(Collectors.toList());
        l_oppositeEnd.stream().map(ConnectorEnd::get_connectorOfEnd).forEach(edgeDone::add);
        connectableElementList = l_oppositeEnd.stream().map(ConnectorEnd::getRole).collect(Collectors.toList());
        return connectableElementList;
    }
}
