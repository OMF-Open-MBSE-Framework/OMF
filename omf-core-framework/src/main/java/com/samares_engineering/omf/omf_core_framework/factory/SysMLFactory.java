/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.factory;

import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.magicdraw.uml.Finder;
import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.activities.mdfundamentalactivities.Activity;
import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdcommunications.Signal;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.utils.OMFConstants;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.List;
import java.util.Objects;

/**
 * Factory class to facilitate the creation of Sysml elements in MagicDraw
 */
public class SysMLFactory extends AMagicDrawFactory {
    public static SysMLFactory getInstance() {
        return getInstance(OMFUtils.getProject());
    }

    public static SysMLFactory getInstance(Project project) {
        SysMLFactory.SysMLFactoryHolder.instance.setProject(project);
        return SysMLFactoryHolder.instance;
    }

    public Package createPackage( String name, Element owner) {
        Package mdPackage = createPackage();
        mdPackage.setName(name);
        mdPackage.setOwner(owner);
        return mdPackage;
    }
    public Package createPackage() {
        return getMagicDrawFactory().createPackageInstance();
    }
    public Package createPackage(Element owner) {
        var mdPackage = getMagicDrawFactory().createPackageInstance();
        mdPackage.setOwner(owner);
        return mdPackage;
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


    public Class createConstraintBlock() {
        Class constraintBlock = getMagicDrawFactory().createClassInstance();
        Profile.getInstance().getSysml().constraintBlock().apply(constraintBlock);
        return constraintBlock;
    }

    public Class createConstraintBlock(Element owner) {
        Class constraintBlock = createConstraintBlock();
        constraintBlock.setOwner(owner);
        return constraintBlock;
    }

    public OpaqueExpression createOpaqueExpression() {
        return getMagicDrawFactory().createOpaqueExpressionInstance();
    }

    public OpaqueExpression createOpaqueExpression(Element owner) {
        OpaqueExpression opaqueExpression = createOpaqueExpression();
        opaqueExpression.setOwner(owner);
        return opaqueExpression;
    }

    public OpaqueExpression createOpaqueExpression(Element owner, String body) {
        OpaqueExpression opaqueExpression = createOpaqueExpression(owner);
        opaqueExpression.getBody().add(body);
        return opaqueExpression;
    }


    public Constraint createConstraint() {
        return getMagicDrawFactory().createConstraintInstance();
    }

    public Constraint createConstraint(Element owner) {
        Constraint constraint = createConstraint();
        constraint.setOwner(owner);
        return constraint;
    }

    public Constraint createConstraint(Element owner, String specification) {
        Constraint constraint = createConstraint(owner);
        var valueSpecification = createOpaqueExpression(constraint, specification);
        constraint.setSpecification(valueSpecification);
        constraint.getConstrainedElement().add(owner);
        return constraint;
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

    public Property createValueProperty() {
        Property valueProperty = createProperty();
        Profile._getSysmlAdditionalStereotypes().valueProperty().apply(valueProperty);
        return valueProperty;
    }

    public Property createValueProperty(Element owner) {
        Property valueProperty = createValueProperty();
        valueProperty.setOwner(owner);
        return valueProperty;
    }

    public Property createValueProperty(Element owner, Type type) {
        Property valueProperty = createValueProperty(owner);
        valueProperty.setType(type);
        return valueProperty;
    }

    public Property createConstraintProperty(Element owner) {
        Property constraintProperty = createConstraintProperty();
        constraintProperty.setOwner(owner);
        return constraintProperty;
    }
    public Property createConstraintProperty() {
        Property constraintProperty = createProperty();
        Profile._getSysmlAdditionalStereotypes().constraintProperty().apply(constraintProperty);
        return constraintProperty;
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

    public Port createConstraintParameter() {
        Port port = getMagicDrawFactory().createPortInstance();
        Profile._getSysmlAdditionalStereotypes().constraintParameter().apply(port);
        return port;
    }

    public Port createConstraintParameter(Element owner) {
        Port port = createConstraintParameter();
        port.setOwner(owner);
        return port;
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

    /* Literals */

    /**
     * Create a LiteralBoolean with the given value
     * @param b the value of the LiteralBoolean
     * @return the created LiteralBoolean
     */
    private LiteralBoolean createLiteralBoolean(boolean b) {
        LiteralBoolean literalBoolean = getMagicDrawFactory().createLiteralBooleanInstance();
        literalBoolean.setValue(b);
        return literalBoolean;
    }

    /**
     * Create a LiteralBoolean with the given value
     * @param owner the owner of the LiteralBoolean
     * @param defaultValue the value of the LiteralBoolean
     * @return the created LiteralBoolean
     */
    public LiteralBoolean createLiteralBoolean(Element owner, boolean defaultValue) {
        LiteralBoolean literalBoolean = createLiteralBoolean(defaultValue);
        literalBoolean.setOwner(owner);
        return literalBoolean;
    }

    /**
     * Create a LiteralInteger with the given value
     * @param i the value of the LiteralInteger
     * @return the created LiteralInteger
     */
    private LiteralInteger createLiteralInteger(int i) {
        LiteralInteger literalInteger = getMagicDrawFactory().createLiteralIntegerInstance();
        literalInteger.setValue(i);
        return literalInteger;
    }

    /**
     * Create a LiteralInteger with the given value
     * @param owner the owner of the LiteralInteger
     * @param i the value of the LiteralInteger
     * @return the created LiteralInteger
     */
    public LiteralInteger createLiteralInteger(Element owner, int i) {
        LiteralInteger literalInteger = createLiteralInteger(i);
        literalInteger.setOwner(owner);
        return literalInteger;
    }

    public LiteralReal createLiteralReal(double value) {
        LiteralReal literalReal = getMagicDrawFactory().createLiteralRealInstance();
        literalReal.setValue(value);
        return literalReal;
    }

    public LiteralReal createLiteralReal(Element owner, double value) {
        LiteralReal literalReal = createLiteralReal(value);
        literalReal.setOwner(owner);
        return literalReal;
    }

    /**
     * Create a LiteralString with the given value
     * @param stringValue the value of the LiteralString
     * @return the created LiteralString
     */
    private LiteralString createLiteralString(String stringValue) {
        LiteralString literalString = getMagicDrawFactory().createLiteralStringInstance();
        literalString.setValue(stringValue);
        return literalString;
    }

    /**
     * Create a LiteralString with the given value
     * @param owner the owner of the LiteralString
     * @param stringValue the value of the LiteralString
     * @return the created LiteralString
     */
    public LiteralString createLiteralString(Element owner, String stringValue) {
        LiteralString literalString = createLiteralString(stringValue);
        literalString.setOwner(owner);
        return literalString;
    }


    //Packages
    public Package getSysMLTypeLibraryPackage() {
        Model sysmlModel = Finder.byNameRecursively().find(OMFUtils.getProject(), Model.class, OMFConstants.SYSML_PACKAGE_NAME);
        return Finder.byNameRecursively().find(sysmlModel, Package.class, OMFConstants.SYSML_LIBRARY_PACKAGE_NAME);
    }

}



