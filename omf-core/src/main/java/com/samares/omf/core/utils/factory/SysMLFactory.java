/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.utils.factory;

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
 * The type Fa factory.
 */
public class SysMLFactory extends AMagicDrawFactory {
    /**
     * Gets instance.
     * @return the instance
     */
    public static SysMLFactory getInstance() {
        return getInstance(OMFUtils.currentProject);
    }

    public static SysMLFactory getInstance(Project project) {
        SysMLFactory.SysMLFactoryHolder.instance.setProject(project);
        return SysMLFactoryHolder.instance;
    }

    /**
     * Create interface block class.
     * @return the class
     */
    public Class createInterfaceBlock() {
        Class interfaceBlock = getMagicDrawFactory().createClassInstance();
        StereotypesHelper.addStereotype(interfaceBlock, Profile.getSysml().interfaceBlock().getStereotype());
        return interfaceBlock;
    }

    /**
     * Create a Class
     * @return the class
     */
    public Class createClass() {
        return getMagicDrawFactory().createClassInstance();
    }

    /**
     * Create an Activity instance
     * @return the Activity
     */
    public Activity createActivity() {
        return getMagicDrawFactory().createActivityInstance();
    }

    public Class createInterfaceBlock(Element owner) {
        Class interfaceBlock = createInterfaceBlock();
        interfaceBlock.setOwner(owner);
        return interfaceBlock;
    }

    /**
     * Create block class.
     *
     * @return the class
     */
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

    /**
     * Create flow property property.
     *
     * @return the property
     */
    public Property createFlowProperty() {
        Property flowProperty = getMagicDrawFactory().createPropertyInstance();
        StereotypesHelper.addStereotype(flowProperty, Profile.getSysml().flowProperty().getStereotype());
        Profile.getSysml().flowProperty().setDirection(flowProperty, SysMLProfile.FlowDirectionKindEnum.OUT);
        return flowProperty;
    }

    /**
     * Create property property.
     *
     * @param name the name
     * @return the property
     */
    public Property createProperty(String name) {
        Property prop = getMagicDrawFactory().createPropertyInstance();
        prop.setName(name);
        return prop;
    }

    public Signal createSignal(Element owner) {
        Signal signal = getMagicDrawFactory().createSignalInstance();
        signal.setOwner(owner);
        return signal;
    }

    public Port createProxyPort(Element owner) {
        Port p = createProxyPort();
        p.setOwner(owner);
        return p;
    }

    public Port createProxyPort() {
        Port port = getMagicDrawFactory().createPortInstance();
        StereotypesHelper.addStereotype(port, Profile.getSysml().proxyPort().getStereotype());
        return port;
    }

    public Connector createConnector(Port portSource, Port portTarget, List<Property> pathSource, List<Property> pathTarget, Element owner) {

        Connector connector = getMagicDrawFactory().createConnectorInstance();

        connector.setOwner(owner);

        ConnectorEnd connectorEndA = getMagicDrawFactory().createConnectorEndInstance();
        connectorEndA.setRole(portSource);
        connectorEndA.set_connectorOfEnd(connector);

        if (!Objects.equals(portSource.getOwner(), owner) && pathSource != null) {
            StereotypesHelper.addStereotype(connectorEndA, Profile.getSysml().nestedConnectorEnd().getStereotype());
            Profile.getSysml().elementPropertyPath().setPropertyPath(connectorEndA, pathSource);
        }

        ConnectorEnd connectorEndB = getMagicDrawFactory().createConnectorEndInstance();

        connectorEndB.setRole(portTarget);
        connectorEndB.set_connectorOfEnd(connector);

        if (!Objects.equals(portTarget.getOwner(), owner) && pathTarget != null) {
            StereotypesHelper.addStereotype(connectorEndB, Profile.getSysml().nestedConnectorEnd().getStereotype());
            Profile.getSysml().elementPropertyPath().setPropertyPath(connectorEndB, pathTarget);
        }

        connector.getEnd().clear();
        connector.getEnd().add(connectorEndA);
        connector.getEnd().add(connectorEndB);

        return connector;
    }

    /**
     *
     */
    private static class SysMLFactoryHolder {
        private static final SysMLFactory instance = new SysMLFactory();
    }
}
