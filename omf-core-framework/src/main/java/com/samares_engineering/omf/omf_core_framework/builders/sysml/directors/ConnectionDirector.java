/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.builders.sysml.directors;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.samares_engineering.omf.omf_core_framework.builders.exceptions.BuilderException;
import com.samares_engineering.omf.omf_core_framework.builders.generic.AGenericBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.generic.IGenericBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.sysml.ConnectorBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.sysml.InterfaceBlockBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.sysml.ProxyPortBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.uml.PortBuilder;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.DiagramUtils;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.InternalDiagramManagement;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectionDirector implements IGenericBuilder {
    ConnectableElement src;
    ConnectableElement dst;

    Connector connector;
    ConnectorBuilder connectorBuilder;

    AGenericBuilder srcBuilder;
    AGenericBuilder dstBuilder;

    List<Element> srcPropertyPath;
    List<Element> dstPropertyPath;


    ArrayList<Connector> createdConnectors;

    private boolean directConnection;
    private ConnectorBuilder defaultConnectorBuilder;
    private ConnectorBuilder srcConnectorBuilder;
    private ConnectorBuilder dstConnectorBuilder;
    private PortBuilder srcPortBuilderDelegation;
    private PortBuilder dstPortBuilderDelegation;
    private PortBuilder srcPortBuilder;
    private PortBuilder dstPortBuilder;
    private InterfaceBlockBuilder sharedInterfaceBuilder;
    private Type sharedInterface;

    //TODO Refactor constructors
    public ConnectionDirector() {
        super();
        this.createdConnectors = new ArrayList<>();
    }

    public ConnectionDirector src(AGenericBuilder srcBuilder) {
        this.srcBuilder = srcBuilder;
        return this;
    }

    public ConnectionDirector src(ConnectableElement src) {
        this.src = src;
        return this;
    }

    public ConnectionDirector srcPropertyPath(List<Element> srcPropertyPath) {
        this.srcPropertyPath = srcPropertyPath;
        return this;
    }

    public ConnectionDirector dst(AGenericBuilder dstBuilder) {
        this.dstBuilder = dstBuilder;
        return this;
    }

    public ConnectionDirector dst(ConnectableElement dst) {
        this.dst = dst;
        return this;
    }

    public ConnectionDirector dstPropertyPath(List<Element> dstPropertyPath) {
        this.dstPropertyPath = dstPropertyPath;
        return this;
    }

    public ConnectionDirector connector(Connector connector) {
        this.connector = connector;
        return this;
    }

    public ConnectionDirector connector(ConnectorBuilder connectorBuilder) {
        this.connectorBuilder = connectorBuilder;
        return this;
    }

    public ConnectionDirector directConnection() {
        this.directConnection = true;
        return this;
    }

    private void preBuild() {
        final boolean oneEndIsNull = (src == null && srcBuilder == null)
                || (dst == null && dstBuilder == null);
        if (oneEndIsNull)
            throw new BuilderException("One End is Null"
                    + "\nsrc: " + src
                    + "\ndst " + dst, this);

    }

    @Override
    public List<Connector> build() {
        preBuild();

        List<Connector> connectors = new ArrayList<>();
        if (srcBuilder != null)
            src = (ConnectableElement) srcBuilder.build();
        if (dstBuilder != null)
            dst = (ConnectableElement) dstBuilder.build();

//        if(directConnection)
//            connectors.add(createDirectConnection());

        return connectors;
    }

    public ConnectionDirector createDirectConnection() {
        if (connectorBuilder == null)
            connectorBuilder = new ConnectorBuilder();

        if (connector != null)
            connectorBuilder.withBase(connector);

        if (srcBuilder != null)
            src = (ConnectableElement) srcBuilder.build();

        if (dstBuilder != null)
            dst = (ConnectableElement) dstBuilder.build();

        connectorBuilder
                .src(src)
                .srcPropertyPath(srcPropertyPath)
                .dst(dst)
                .dstPropertyPath(srcPropertyPath);

        connectorBuilder.build();

        return this;
    }

    public ConnectionDirector createDirectConnectorPath(Property srcPart, ConnectableElement srcConnection, Property dstPart, Property dstConnection,
                                                        Element connectorOwner,
                                                        List<Element> srcPropertyPath, List<Element> dstPropertyPath) throws LegacyOMFException, BuilderException {
        ConnectorBuilder connectorBuilder = new ConnectorBuilder()
                .createNewElement()
                .srcPart(srcPart)
                .src(srcConnection)
                .dstPart(dstPart)
                .dst(dstConnection)
                .srcPropertyPath(srcPropertyPath)
                .dstPropertyPath(dstPropertyPath)
                .owner(connectorOwner);

        return createDirectConnectorPath(connectorBuilder);
    }

    public ConnectionDirector createDirectConnectorPath(ConnectorBuilder connectorBuilder) throws LegacyOMFException, BuilderException {
        Connector connector = connectorBuilder.build();
        createdConnectors.add(connector);
        return this;
    }

    public ConnectionDirector withSrcDelegation(PortBuilder srcPortBuilder) {
        this.srcPortBuilderDelegation = srcPortBuilder;
        return this;
    }

    public ConnectionDirector withDstDelegation(ProxyPortBuilder dstPortBuilder) {
        this.dstPortBuilderDelegation = dstPortBuilder;
        return this;
    }

    public ConnectionDirector withDelegation(ProxyPortBuilder proxyPortBuilder) {
        this.withSrcDelegation(proxyPortBuilder);
        this.withDstDelegation(proxyPortBuilder);
        return this;
    }

    public ArrayList<Connector> getConnectors() {
        return createdConnectors;
    }

    public ConnectionDirector withDefaultConnectionBuilder(ConnectorBuilder connectorBuilder) {
        this.defaultConnectorBuilder = connectorBuilder;
        return this;
    }

    public ConnectionDirector withSrcPort(PortBuilder srcPortBuilder) {
        this.srcPortBuilder = srcPortBuilder;
        return this;
    }

    public ConnectionDirector withDstPort(PortBuilder dstPortBuilder) {
        this.dstPortBuilder = dstPortBuilder;
        return this;
    }

    public ConnectionDirector withPortCreation(PortBuilder portBuilder) {
        withSrcPort(portBuilder);
        withDstPort(portBuilder);
        return this;
    }

    public ConnectionDirector withSharedInterface(InterfaceBlockBuilder interfaceBuilder) {
        this.sharedInterfaceBuilder = interfaceBuilder;
        return this;
    }

    public ConnectionDirector withSharedInterface(Type sharedInterface) {
        this.sharedInterface = sharedInterface;
        return this;
    }

    public ConnectionDirector withRefresh() {
        InternalDiagramManagement.refreshAllConnectors(createdConnectors, DiagramUtils.getOpenedDiagram());
        return this;
    }

    public ConnectionDirector connectConnectionFromSonToMother(Property src, ConnectorBuilder templateConnectorBuilder, Property dst, List<Element> sonPropertyPaths) {
        List<Element> partList = sonPropertyPaths.stream()
                .filter(Profile._getSysmlAdditionalStereotypes().partProperty()::is)
                .collect(Collectors.toList());
        partList.add(0, src);
        partList.add(dst);

        ConnectorBuilder connectorBuilder = new ConnectorBuilder(templateConnectorBuilder);
        for (int i = 0; i < partList.size() - 1; i++) {
            Property son = (Property) partList.get(i);
            Property mother = (Property) partList.get(i + 1);

            List<Element> motherPropertyPath = new ArrayList<>(partList);
            motherPropertyPath.remove(0);
            Connector connector = connectorBuilder
                    .srcPart(son)
                    .dstPart(mother)
                    .srcPropertyPath(partList)
                    .defaultSrcBuilderOwner(son.getType())
                    .defaultDstBuilderOwner(mother.getType())
//                    .srcBuilderOwner(son.getType())
//                    .dstBuilderOwner(mother.getType())
                    .owner(mother.getType())
                    .withAutoEndSetting()
                    .rebuild();

            connectorBuilder
                    .removeSrcBuilder()
                    .src((ConnectableElement) connectorBuilder.getDstBuilder()
                            .getElementToBuild());

            createdConnectors.add(connector);
        }

        return this;
    }

    public static class MethodReadyToInvoke {
        public Method methodToInvoke;
        public Object context;
        public Object[] parameters;

        public MethodReadyToInvoke(Method methodToInvoke, Object context, Object[] parameters) {
            this.methodToInvoke = methodToInvoke;
            this.context = context;
            this.parameters = parameters;
        }
    }

}
