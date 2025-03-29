/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.builders.uml;


import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.builders.exceptions.BuilderException;
import com.samares_engineering.omf.omf_core_framework.builders.generic.AGenericBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.uml.metaclasses.NamedElementBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.uml.metaclasses.TypeBuilder;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;

import java.util.Collection;
import java.util.stream.Collectors;

public class PropertyBuilder extends NamedElementBuilder {
    protected boolean createNewElement = false;
    protected Collection<ConnectorEnd> ends;

    protected Type type;
    protected TypeBuilder typeBuilder;

    protected SysMLProfile.FlowDirectionKindEnum direction;

    public PropertyBuilder() {
    }

    public PropertyBuilder(Property property) {
        super(property);
    }

    public PropertyBuilder(PropertyBuilder propertyBuilder) {
        this(propertyBuilder, false);
    }

    public PropertyBuilder(PropertyBuilder propertyBuilder, boolean fromInheritedConstructor) {
        super(propertyBuilder, true);
        if (!fromInheritedConstructor)
            this.createNewElement = propertyBuilder.createNewElement;

        this.ends = propertyBuilder.ends.stream().collect(Collectors.toList());
        this.type = propertyBuilder.type;
        this.typeBuilder = propertyBuilder.typeBuilder;
        this.direction = propertyBuilder.direction;

    }

    public PropertyBuilder type(Type type) {
        this.type = type;
        return this;
    }

    public PropertyBuilder type(TypeBuilder typeBuilder) {
        this.typeBuilder = typeBuilder;
        return this;
    }

    public PropertyBuilder withSharedType(Type sharedType) {
        return type(sharedType);
    }

    public PropertyBuilder withSharedType(TypeBuilder sharedType) {//TODO REFACTOR THIS
//        return type(new TypeBuilder(sharedType).reuseBuilder(sharedType));
        return type(new TypeBuilder().reuseBuilder(sharedType));
    }

    @Override
    public PropertyBuilder createNewElement() {
        this.createNewElement = true;
        return this;
    }

    @Override
    public PropertyBuilder reuseBuilder(AGenericBuilder builderToReuse) {
        this.createNewElement = false;
        return (PortBuilder) super.reuseBuilder(builderToReuse);
    }

    public PropertyBuilder cloneElement(Element elementToClone) {
        super.cloneElement(elementToClone);
        Property property = (Property) elementToClone;
        type(new TypeBuilder().cloneElement(property.getType()));

        return this;
    }

    @Override
    public PropertyBuilder withBase(Element element) {
        return (PropertyBuilder) super.withBase(element);
    }

    @Override
    public PropertyBuilder name(String name) {
        return (PropertyBuilder) super.name(name);
    }

    @Override
    public PropertyBuilder owner(Element owner) {
        return (PropertyBuilder) super.owner(owner);
    }

    @Override
    public PropertyBuilder stereotype(Stereotype stereotype) {
        return (PropertyBuilder) super.stereotype(stereotype);
    }

    @Override
    public PropertyBuilder stereotypes(Collection collection) {
        return (PropertyBuilder) super.stereotypes(collection);
    }

    @Override
    public PropertyBuilder ownedElement(AGenericBuilder builder) {
        return (PropertyBuilder) super.ownedElement(builder);
    }

    @Override
    public PropertyBuilder ownedElementBuilders(Collection collection) {
        return (PropertyBuilder) super.ownedElementBuilders(collection);
    }

    @Override
    public PropertyBuilder ownedElement(Element element) {
        return (PropertyBuilder) super.ownedElement(element);
    }

    @Override
    public PropertyBuilder ownedElements(Collection collection) {
        return (PropertyBuilder) super.ownedElements(collection);
    }

    @Override
    public PropertyBuilder defaultOwner(Element defaultOwner) {
        this.defaultOwner = defaultOwner;
        return this;
    }

    @Override
    public PropertyBuilder defaultOwner(AGenericBuilder defaultOwnerBuilder) {
        this.defaultOwnerBuilder = defaultOwnerBuilder;
        return this;
    }


    @Override
    protected void preBuild() {
        //preBuild
        if (name == null)
            name = "";
        boolean elementToBuildHasNoOwner = defaultOwner == null && (builderToReuse != null && builderToReuse.getOwner() == null);
        if (elementToBuildHasNoOwner)
            throw new BuilderException("port: " + name + " owner is null", this);
    }

    public Property build() {
        if(this.createNewElement)
            this.elementToBuild = SysMLFactory.getInstance().createFlowProperty();
        super.build();
        Property flowProperty = (Property) this.elementToBuild;

        return flowProperty;
    }

    @Override
    public Property rebuild() {
        return (Property) super.rebuild();
    }

    @Override
    public PropertyBuilder clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("NotImplementedYet");
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

    public Collection<ConnectorEnd> getEnds() {
        return ends;
    }

    public void setEnds(Collection<ConnectorEnd> ends) {
        this.ends = ends;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public TypeBuilder getTypeBuilder() {
        return typeBuilder;
    }

    public void setTypeBuilder(TypeBuilder typeBuilder) {
        this.typeBuilder = typeBuilder;
    }

    public SysMLProfile.FlowDirectionKindEnum getDirection() {
        return direction;
    }

    public void setDirection(SysMLProfile.FlowDirectionKindEnum direction) {
        this.direction = direction;
    }


}
