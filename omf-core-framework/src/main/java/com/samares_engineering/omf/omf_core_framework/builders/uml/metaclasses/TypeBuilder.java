/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.builders.uml.metaclasses;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*;
import com.nomagic.uml2.ext.magicdraw.commonbehaviors.mdcommunications.Signal;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.builders.BetaFactory;
import com.samares_engineering.omf.omf_core_framework.builders.exceptions.BuilderException;
import com.samares_engineering.omf.omf_core_framework.builders.generic.AGenericBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.uml.PropertyBuilder;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TypeBuilder extends NamedElementBuilder {
    protected boolean createNewElement = false;

    private List<PropertyBuilder> propertyBuilders;

    public TypeBuilder() {
        super();
    }

    public TypeBuilder(Type type) {
        super(type);
        stereotype(Profile.getInstance().getSysml().interfaceBlock().getStereotype());
        propertyBuilders = new ArrayList<>();
    }

    public TypeBuilder(TypeBuilder typeBuilder) {
        this(typeBuilder, false);
    }

    public TypeBuilder(TypeBuilder typeBuilder, boolean fromInheritedConstructor) {
        super(typeBuilder, true);
        if (!fromInheritedConstructor)
            this.createNewElement = typeBuilder.createNewElement;

        this.propertyBuilders = typeBuilder.propertyBuilders;

    }

    @Override
    public TypeBuilder createNewElement() {
        super.createNewElement();
        return this;
    }

    @Override
    public TypeBuilder reuseBuilder(AGenericBuilder builderToReuse) {
        this.createNewElement = false;
        return (TypeBuilder) super.reuseBuilder(builderToReuse);
    }

    @Override
    public TypeBuilder cloneElement(Element elementToClone) {
        super.cloneElement(elementToClone);
        createNewElement();

        if (elementToClone instanceof Class)
            this.elementToBuild = BetaFactory.getInstance().magicDrawFactory.createClassInstance();
        if (elementToClone instanceof DataType)
            this.elementToBuild = BetaFactory.getInstance().magicDrawFactory.createDataTypeInstance();
        if (elementToClone instanceof Signal)
            this.elementToBuild = BetaFactory.getInstance().magicDrawFactory.createSignalInstance();

        Type type = (Type) elementToClone;
        return this;
    }

    public TypeBuilder withProperty(Property property) {
        return ownedElement(property);
    }

    public TypeBuilder withProperty(PropertyBuilder propertyBuilder) {
        propertyBuilders.add(propertyBuilder);
        return this;
    }

    public TypeBuilder withProperty(List<PropertyBuilder> propertyBuilders) {
        this.propertyBuilders.addAll(propertyBuilders);
        return this;
    }

    //REDEFINED TO IMPLEMENT FLUENT PATTERN
    @Override
    public TypeBuilder withBase(Element element) {
        return (TypeBuilder) super.name(name);

    }

    @Override
    public TypeBuilder name(String name) {
        return (TypeBuilder) super.name(name);
    }

    @Override
    public TypeBuilder owner(Element owner) {
        return (TypeBuilder) super.owner(owner);
    }

    @Override
    public TypeBuilder stereotype(Stereotype stereotype) {
        return (TypeBuilder) super.stereotype(stereotype);
    }

    @Override
    public TypeBuilder stereotypes(Collection collection) {
        return (TypeBuilder) super.stereotypes(collection);
    }

    @Override
    public TypeBuilder ownedElement(Element element) {
        return (TypeBuilder) super.ownedElement(element);
    }

    @Override
    public TypeBuilder ownedElements(Collection collection) {
        return (TypeBuilder) super.ownedElements(collection);
    }

    @Override
    public TypeBuilder ownedElement(AGenericBuilder builder) {
        return (TypeBuilder) super.ownedElement(builder);
    }

    @Override
    public TypeBuilder ownedElementBuilders(Collection collection) {
        return (TypeBuilder) super.ownedElementBuilders(collection);
    }

    @Override
    public TypeBuilder defaultOwner(Element defaultOwner) {
        this.defaultOwner = defaultOwner;
        return this;
    }

    @Override
    public TypeBuilder defaultOwner(AGenericBuilder defaultOwnerBuilder) {
        this.defaultOwnerBuilder = defaultOwnerBuilder;
        return this;
    }

    @Override
    public TypeBuilder clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("NOT IMPLEMENTED YET");
    }

    @Override
    protected void preBuild() {
        //preBuild
//        if (this.elementToBuild == null)
//            this.elementToBuild = SysMLFactory.getInstance().createBlock();
        if (name == null)
            name = "";
        boolean elementToBuildHasNoOwner = defaultOwner == null && (builderToReuse != null && builderToReuse.getOwner() == null);
        if (elementToBuildHasNoOwner)
            throw new BuilderException("Type: " + name + " owner is null", this);
    }

    @Override
    public Type build() {
//        preBuild();
        super.build();
        Type type = (Type) this.elementToBuild;

//        applyStereotypes();
//        setOwnedElements();

        return type;
    }

    @Override
    public Type rebuild() {
        return (Type) super.rebuild();
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

    public List<PropertyBuilder> getPropertyBuilders() {
        return propertyBuilders;
    }

    public void setPropertyBuilders(List<PropertyBuilder> propertyBuilders) {
        this.propertyBuilders = propertyBuilders;
    }
}
