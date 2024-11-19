/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils;

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
                .map(ConnectorEnd::get_connectorOfEnd).anyMatch(connector -> !edgeDone.contains(connector));
    }

    @Override
    public List<ConnectableElement> next() {
        List<ConnectorEnd> oppositeEnd = connectableElementList
                .stream()
                .map(ConnectableElement::getEnd)
                .flatMap(Collection::stream)
                .map(ModelHelper::getOppositeEnd)
                .filter(connectorEnd -> !edgeDone.contains(connectorEnd.get_connectorOfEnd()))
                .collect(Collectors.toList());
        oppositeEnd.stream().map(ConnectorEnd::get_connectorOfEnd).forEach(edgeDone::add);
        connectableElementList = oppositeEnd.stream().map(ConnectorEnd::getRole).collect(Collectors.toList());
        return connectableElementList;
    }
}
