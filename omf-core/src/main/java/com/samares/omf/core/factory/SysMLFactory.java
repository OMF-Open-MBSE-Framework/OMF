/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.factory;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdcommunications.Signal;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.utils.profile.Profile;

import java.util.List;
import java.util.Objects;

/**
 * Factory class to facilitate the creation of Sysml elements in MagicDraw
 */
public class SysMLFactory extends AMagicDrawFactory {
    public static SysMLFactory getInstance() {
        return getInstance(OMFUtils.currentProject);
    }

    public static SysMLFactory getInstance(Project project) {
        SysMLFactory.SysMLFactoryHolder.instance.setProject(project);
        return SysMLFactoryHolder.instance;
    }

    private static class SysMLFactoryHolder {
        private static final SysMLFactory instance = new SysMLFactory();
    }

    /*
    Activity
     */

    public Activity createActivity() {
        return getMagicDrawFactory().createActivityInstance();
    }

    public Activity createActivity(Element owner) {
        Activity activity = getMagicDrawFactory().createActivityInstance();
        activity.setOwner(owner);
        return activity;
    }

    /*
    Class
     */

    public Class createInterfaceBlock() {
        Class interfaceBlock = getMagicDrawFactory().createClassInstance();
        StereotypesHelper.addStereotype(interfaceBlock, Profile.getSysml().interfaceBlock().getStereotype());
        return interfaceBlock;
    }

    public Class createInterfaceBlock(Element owner) {
        Class interfaceBlock = createInterfaceBlock();
        interfaceBlock.setOwner(owner);
        return interfaceBlock;
    }

    public Class createClass() {
        return getMagicDrawFactory().createClassInstance();
    }

    public Class createClass(Element owner) {
        Class mdClass = createClass();
        mdClass.setOwner(owner);
        return mdClass;
    }

    public Class createBlock() {
        Class block = getMagicDrawFactory().createClassInstance();
        StereotypesHelper.addStereotype(block, Profile.getSysml().block().getStereotype());
        return block;
    }

    public Class createBlock(Element owner) {
        Class block = createBlock();
        block.setOwner(owner);
        return block;
    }

    /*
    Property
     */

    public Property createFlowProperty() {
        Property flowProperty = getMagicDrawFactory().createPropertyInstance();
        StereotypesHelper.addStereotype(flowProperty, Profile.getSysml().flowProperty().getStereotype());
        Profile.getSysml().flowProperty().setDirection(flowProperty, SysMLProfile.FlowDirectionKindEnum.OUT);
        return flowProperty;
    }

    public Property createFlowProperty(Element owner) {
        Property flowProperty = createFlowProperty();
        flowProperty.setOwner(owner);
        return flowProperty;
    }

    public Property createProperty() {
        return getMagicDrawFactory().createPropertyInstance();
    }

    public Property createProperty(Element owner) {
        Property property = createProperty();
        property.setOwner(owner);
        return property;
    }

    /*
    Signal
     */

    public Signal createSignal() {
        return getMagicDrawFactory().createSignalInstance();
    }

    public Signal createSignal(Element owner) {
        Signal signal = createSignal();
        signal.setOwner(owner);
        return signal;
    }

    /*
    Port
     */

    public Port createProxyPort() {
        Port port = getMagicDrawFactory().createPortInstance();
        StereotypesHelper.addStereotype(port, Profile.getSysml().proxyPort().getStereotype());
        return port;
    }

    public Port createProxyPort(Element owner) {
        Port p = createProxyPort();
        p.setOwner(owner);
        return p;
    }

    /*
    Connector
     */

    public Connector createConnector() {
        return getMagicDrawFactory().createConnectorInstance();
    }

    public Connector createConnector(Element owner) {
        Connector connector = createConnector();
        connector.setOwner(owner);
        return connector;
    }

    public Connector createConnectorBetweenPorts(Port portSource, Port portTarget,
                                                 List<Property> pathSource, List<Property> pathTarget,
                                                 Element owner) {
        Connector connector = createConnector(owner);
        ConnectorEnd connectorEndSource = createConnectorEnd(portSource, pathSource, owner, connector);

        ConnectorEnd connectorEndTarget = createConnectorEnd(portTarget, pathTarget, owner, connector);

        connector.getEnd().clear();
        connector.getEnd().add(connectorEndSource);
        connector.getEnd().add(connectorEndTarget);

        return connector;
    }

    private ConnectorEnd createConnectorEnd(Port port, List<Property> path, Element owner, Connector connector) {
        ConnectorEnd connectorEnd = createConnectorEnd(port, connector);

        if (!Objects.equals(port.getOwner(), owner) && path != null) {
            StereotypesHelper.addStereotype(connectorEnd, Profile.getSysml().nestedConnectorEnd().getStereotype());
            Profile.getSysml().elementPropertyPath().setPropertyPath(connectorEnd, path);
        }
        return connectorEnd;
    }

    private ConnectorEnd createConnectorEnd(Port port, Connector connector) {
        ConnectorEnd connectorEnd = getMagicDrawFactory().createConnectorEndInstance();
        connectorEnd.setRole(port);
        connectorEnd.set_connectorOfEnd(connector);
        return connectorEnd;
    }
}
