/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.builders.sysml;

import com.nomagic.uml2.ext.jmi.helpers.ModelHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectableElement;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares.omf.core.utils.builders.uml.metaclasses.NamedElementBuilder;
import com.samares.omf.core.utils.builders.BetaFactory;
import com.samares.omf.core.utils.builders.exceptions.BuilderException;
import com.samares.omf.core.utils.builders.generic.AGenericBuilder;
import com.samares.omf.core.utils.errorManagement.OMFErrorHandler;
import com.samares.omf.core.utils.factory.OMFHelper;
import com.samares.omf.core.utils.profile.Profile;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConnectorBuilder extends NamedElementBuilder {
    protected boolean createNewElement = false;
    protected Property srcPart;
    protected Property dstPart;
    protected ConnectableElement src;
    protected ConnectableElement dst;
    protected AGenericBuilder srcBuilder;
    protected AGenericBuilder dstBuilder;
    protected boolean autoEndSetting = false;
//    NamedElementBuilder defaultBuilder;

    protected List<Element> srcPropertyPath;
    protected List<Element> dstPropertyPath;
    protected Element defaultSrcBuilderOwner;
    protected Element defaultDstBuilderOwner;

    public ConnectorBuilder() {
        super();
    }

    public ConnectorBuilder(NamedElement elementToBuild) {
        super(elementToBuild);
    }

    public ConnectorBuilder(ConnectorBuilder connectorBuilder) {
        this(connectorBuilder, false);
    }

    public ConnectorBuilder(ConnectorBuilder connectorBuilder, boolean fromInheritedConstructor) {
        super(connectorBuilder, true);

        if (!fromInheritedConstructor)
            this.createNewElement = connectorBuilder.createNewElement;
        this.srcPart = connectorBuilder.srcPart;
        this.dstPart = connectorBuilder.dstPart;
        this.src = connectorBuilder.src;
        this.dst = connectorBuilder.dst;
        this.srcBuilder = connectorBuilder.srcBuilder;
        this.dstBuilder = connectorBuilder.dstBuilder;
        this.autoEndSetting = connectorBuilder.autoEndSetting;
        this.srcPropertyPath = connectorBuilder.srcPropertyPath;
        this.dstPropertyPath = connectorBuilder.dstPropertyPath;
        this.defaultSrcBuilderOwner = connectorBuilder.defaultSrcBuilderOwner;
        this.defaultDstBuilderOwner = connectorBuilder.defaultDstBuilderOwner;
    }


    @Override
    public ConnectorBuilder createNewElement() {
        this.createNewElement = true;
        return this;
    }

    @Override
    public ConnectorBuilder reuseBuilder(AGenericBuilder builderToReuse) {
        this.createNewElement = false;
        return (ConnectorBuilder) super.reuseBuilder(builderToReuse);
    }

    @Override
    public ConnectorBuilder cloneElement(Element elementToClone) {
        return null;
    }

    @Override
    public ConnectorBuilder withBase(Element element) {
        return (ConnectorBuilder) super.withBase(element);
    }

    @Override
    public ConnectorBuilder name(String name) {
        return (ConnectorBuilder) super.name(name);
    }

    @Override
    public ConnectorBuilder owner(Element owner) {
        return (ConnectorBuilder) super.owner(owner);
    }

    @Override
    public ConnectorBuilder stereotype(Stereotype stereotype) {
        return (ConnectorBuilder) super.stereotype(stereotype);
    }

    @Override
    public ConnectorBuilder stereotypes(Collection collection) {
        return (ConnectorBuilder) super.stereotypes(collection);
    }

    @Override
    public ConnectorBuilder ownedElement(AGenericBuilder builder) {
        return (ConnectorBuilder) super.ownedElement(builder);
    }

    @Override
    public ConnectorBuilder ownedElementBuilders(Collection collection) {
        return (ConnectorBuilder) super.ownedElementBuilders(collection);
    }

    @Override
    public ConnectorBuilder ownedElement(Element element) {
        return (ConnectorBuilder) super.ownedElement(element);
    }

    @Override
    public ConnectorBuilder ownedElements(Collection collection) {
        return (ConnectorBuilder) super.ownedElements(collection);
    }

    @Override
    public ConnectorBuilder defaultOwner(Element defaultOwner) {
        this.defaultOwner = defaultOwner;
        return this;
    }

    @Override
    public ConnectorBuilder defaultOwner(AGenericBuilder defaultOwner_Builder) {
        this.defaultOwner_Builder = defaultOwner_Builder;
        return this;
    }

    @Override
    public ConnectorBuilder clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("NotImplementedYet");
    }

    public ConnectorBuilder src(ConnectableElement src) {
        this.src = src;
        return this;
    }

    public ConnectorBuilder src(AGenericBuilder srcBuilder) {
        this.srcBuilder = srcBuilder;
        return this;
    }

    public ConnectorBuilder srcPart(Property srcPart) {
        this.srcPart = srcPart;
        return this;
    }

    public ConnectorBuilder dstPart(Property dstPart) {
        this.dstPart = dstPart;
        return this;
    }

    public ConnectorBuilder dst(ConnectableElement dst) {
        this.dst = dst;
        return this;
    }

    public ConnectorBuilder dst(AGenericBuilder dstBuilder) {
        this.dstBuilder = dstBuilder;
        return this;
    }


    public ConnectorBuilder removeSrcBuilder() {
        this.srcBuilder = null;
        return this;
    }

    public ConnectorBuilder removeDstBuilder() {
        this.dstBuilder = null;
        return this;
    }

    public ConnectorBuilder defaultSrcBuilderOwner(Element defaultSrcBuilderOwner) {
        this.defaultSrcBuilderOwner = defaultSrcBuilderOwner;
        return this;
    }

    public ConnectorBuilder defaultDstBuilderOwner(Element defaultDstBuilderOwner) {
        this.defaultDstBuilderOwner = defaultDstBuilderOwner;
        return this;
    }

    public ConnectorBuilder srcBuilderOwner(Element srcBuilderOwner) {
        if (srcBuilder != null)
            this.srcBuilder.owner(srcBuilderOwner);
        return this;
    }

    public ConnectorBuilder dstBuilderOwner(Element dstBuilderOwner) {
        if (dstBuilder != null)
            this.dstBuilder.owner(dstBuilderOwner);
        return this;
    }
//    public ConnectorBuilder withDefaultBuilder(NamedElementBuilder defaultBuilder) {
//        this.defaultBuilder = defaultBuilder;
//        return this;
//    }

    public ConnectorBuilder srcPropertyPath(List<Element> srcPropertyPath) {
        this.srcPropertyPath = srcPropertyPath;
        return this;
    }

    public ConnectorBuilder dstPropertyPath(List<Element> dstPropertyPath) {
        this.dstPropertyPath = dstPropertyPath;
        return this;
    }

    public ConnectorBuilder withAutoEndSetting() {
        this.autoEndSetting = true;
        return this;
    }


    @Override
    protected void applyStereotypes() {
        super.applyStereotypes();
    }

    @Override
    protected void setOwnedElements() throws BuilderException {
        super.setOwnedElements();
    }


    @Override
    protected void preBuild() throws BuilderException {
        super.preBuild();
        if (src == null && srcPart != null)
            src = srcPart;
        if (dst == null && dstPart != null)
            dst = dstPart;
    }

    @Override
    public Connector build() throws BuilderException {
        if (this.createNewElement)
            this.elementToBuild = BetaFactory.getInstance().magicDrawFactory.createConnectorInstance();
        super.build();
        Connector connectorBuilt = (Connector) this.elementToBuild;
        ConnectorEnd srcCE = Objects.requireNonNull(ModelHelper.getFirstEnd(connectorBuilt), "Connector first end is null");
        ConnectorEnd dstCE = Objects.requireNonNull(ModelHelper.getSecondEnd(connectorBuilt), "Connector second end is null");

        final boolean setSrcBuilderDefaultOwner = defaultSrcBuilderOwner != null && srcBuilder != null;
        if (setSrcBuilderDefaultOwner)
            srcBuilder.defaultOwner(defaultSrcBuilderOwner);
        final boolean setDstBuilderDefaultOwner = defaultDstBuilderOwner != null && dstBuilder != null;
        if (setDstBuilderDefaultOwner)
            dstBuilder.defaultOwner(defaultDstBuilderOwner);

        if (srcBuilder != null)
            src = (ConnectableElement) srcBuilder.build();
        if (dstBuilder != null)
            dst = (ConnectableElement) dstBuilder.build();

        if (src != null)
            Objects.requireNonNull(ModelHelper.getFirstEnd(connectorBuilt), "Connector first end is null").setRole(src);
        if (dst != null)
            Objects.requireNonNull(ModelHelper.getSecondEnd(connectorBuilt), "Connector second end is null").setRole(dst);

        if (srcPropertyPath != null)
            Profile.getSysml().elementPropertyPath().setPropertyPath(srcCE, srcPropertyPath);
        if (dstPropertyPath != null)
            Profile.getSysml().elementPropertyPath().setPropertyPath(dstCE, dstPropertyPath);

        if (autoEndSetting) {
            try {
                OMFHelper.getInstance().setConnectorEnd(srcCE, srcPart, (Port) src,
                        srcPropertyPath.stream()
                                .filter(Profile.getSysmlAdditionalStereotypes().partProperty()::is)
                                .map(Property.class::cast)
                                .collect(Collectors.toList()));

                OMFHelper.getInstance().setConnectorEnd(dstCE, dstPart, (Port) dst,
                        dstPropertyPath.stream()
                                .filter(Profile.getSysmlAdditionalStereotypes().partProperty()::is)
                                .map(Property.class::cast)
                                .collect(Collectors.toList()));
            } catch (Exception e) {
                OMFErrorHandler.handleException(new BuilderException("Error during AutomaticEndSetting", this), false);
            }
        }


        return connectorBuilt;
    }

    @Override
    public Connector rebuild() throws BuilderException {
        super.rebuild();
        createNewElement = true;
        return build();
    }

    /**
     * GETTER/SETTER
     **/
    @Override
    public boolean isCreateNewElement() {
        return createNewElement;
    }

    @Override
    public void setCreateNewElement(boolean createNewElement) {
        this.createNewElement = createNewElement;
    }

    public Property getSrcPart() {
        return srcPart;
    }

    public void setSrcPart(Property srcPart) {
        this.srcPart = srcPart;
    }

    public Property getDstPart() {
        return dstPart;
    }

    public void setDstPart(Property dstPart) {
        this.dstPart = dstPart;
    }

    public ConnectableElement getSrc() {
        return src;
    }

    public void setSrc(ConnectableElement src) {
        this.src = src;
    }

    public ConnectableElement getDst() {
        return dst;
    }

    public void setDst(ConnectableElement dst) {
        this.dst = dst;
    }

    public AGenericBuilder getSrcBuilder() {
        return srcBuilder;
    }

    public void setSrcBuilder(NamedElementBuilder srcBuilder) {
        this.srcBuilder = srcBuilder;
    }

    public AGenericBuilder getDstBuilder() {
        return dstBuilder;
    }

    public void setDstBuilder(NamedElementBuilder dstBuilder) {
        this.dstBuilder = dstBuilder;
    }

    public List<Element> getSrcPropertyPath() {
        return srcPropertyPath;
    }

    public void setSrcPropertyPath(List<Element> srcPropertyPath) {
        this.srcPropertyPath = srcPropertyPath;
    }

    public List<Element> getDstPropertyPath() {
        return dstPropertyPath;
    }

    public void setDstPropertyPath(List<Element> dstPropertyPath) {
        this.dstPropertyPath = dstPropertyPath;
    }


}
