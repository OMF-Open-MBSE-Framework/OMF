/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.factory;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdcommunications.Signal;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

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
        StereotypesHelper.addStereotype(interfaceBlock, Profile.getInstance().getSysml().interfaceBlock().getStereotype());
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
        StereotypesHelper.addStereotype(block, Profile.getInstance().getSysml().block().getStereotype());
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

    public Property createProperty() {
        return getMagicDrawFactory().createPropertyInstance();
    }

    public Property createProperty(Element owner) {
        Property property = createProperty();
        property.setOwner(owner);
        return property;
    }

    public Property createFlowProperty() {
        Property flowProperty = createProperty();
        StereotypesHelper.addStereotype(flowProperty, Profile.getInstance().getSysml().flowProperty().getStereotype());
        Profile.getInstance().getSysml().flowProperty().setDirection(flowProperty, SysMLProfile.FlowDirectionKindEnum.OUT);
        return flowProperty;
    }

    public Property createFlowProperty(Element owner) {
        Property flowProperty = createFlowProperty();
        flowProperty.setOwner(owner);
        return flowProperty;
    }

    public Property createPartProperty() {
        Property partProperty = createProperty();
        StereotypesHelper.addStereotype(partProperty, Profile.getInstance().getMDCustomSysml().partProperty().getStereotype());
        return partProperty;
    }

    public Property createPartProperty(Element owner) {
        Property partProperty = createPartProperty();
        partProperty.setOwner(owner);
        return partProperty;
    }

    public Property createPartProperty(Element owner, Type type) {
        Property partProperty = createPartProperty(owner);
        partProperty.setType(type);
        return partProperty;
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
        StereotypesHelper.addStereotype(port, Profile.getInstance().getSysml().proxyPort().getStereotype());
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
            StereotypesHelper.addStereotype(connectorEnd, Profile.getInstance().getSysml().nestedConnectorEnd().getStereotype());
            Profile.getInstance().getSysml().elementPropertyPath().setPropertyPath(connectorEnd, path);
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
