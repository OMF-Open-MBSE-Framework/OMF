/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.builders.sysml;


import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares.omf.core.builders.exceptions.BuilderException;
import com.samares.omf.core.builders.uml.metaclasses.TypeBuilder;
import com.samares.omf.core.builders.uml.PropertyBuilder;
import com.samares.omf.core.builders.generic.AGenericBuilder;
import com.samares.omf.core.factory.SysMLFactory;
import com.samares.omf.core.utils.profile.Profile;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InterfaceBlockBuilder extends TypeBuilder {
    protected boolean createNewElement = false;

    private List<FlowPropertyBuilder> l_flowPropertyBuilders;

    public InterfaceBlockBuilder() {
        super();
    }

    public InterfaceBlockBuilder(Type type) {
        super(type);
        stereotype(Profile.getSysml().interfaceBlock().getStereotype());
    }

    public InterfaceBlockBuilder(InterfaceBlockBuilder interfaceBlockBuilder) {
        this(interfaceBlockBuilder, false);
    }

    public InterfaceBlockBuilder(InterfaceBlockBuilder interfaceBlockBuilder, boolean fromInheritedConstructor) {
        super(interfaceBlockBuilder, true);
        if (!fromInheritedConstructor)
            this.createNewElement = interfaceBlockBuilder.createNewElement;
        this.l_flowPropertyBuilders = interfaceBlockBuilder.l_flowPropertyBuilders.stream().collect(Collectors.toList());
    }

    @Override
    public InterfaceBlockBuilder withBase(Element element) {
        return (InterfaceBlockBuilder) super.withBase(element);
    }

    @Override
    public InterfaceBlockBuilder createNewElement() {
        this.createNewElement = true;
        return this;
    }

    @Override
    public InterfaceBlockBuilder reuseBuilder(AGenericBuilder builderToReuse) {
        this.createNewElement = false;
        return (InterfaceBlockBuilder) super.reuseBuilder(builderToReuse);
    }

    @Override
    public InterfaceBlockBuilder cloneElement(Element elementToClone) {
        return (InterfaceBlockBuilder) super.cloneElement(elementToClone);
    }

    @Override
    public InterfaceBlockBuilder withProperty(Property property) {
        return (InterfaceBlockBuilder) super.withProperty(property);
    }

    @Override
    public InterfaceBlockBuilder withProperty(PropertyBuilder propertyBuilder) {
        return (InterfaceBlockBuilder) super.withProperty(propertyBuilder);
    }

    @Override
    public InterfaceBlockBuilder withProperty(List<PropertyBuilder> l_propertyBuilders) {
        return (InterfaceBlockBuilder) super.withProperty(l_propertyBuilders);
    }

    @Override
    public InterfaceBlockBuilder name(String name) {
        return (InterfaceBlockBuilder) super.name(name);
    }

    @Override
    public InterfaceBlockBuilder owner(Element owner) {
        return (InterfaceBlockBuilder) super.owner(owner);
    }

    @Override
    public InterfaceBlockBuilder stereotype(Stereotype stereotype) {
        return (InterfaceBlockBuilder) super.stereotype(stereotype);
    }

    @Override
    public InterfaceBlockBuilder stereotypes(Collection collection) {
        return (InterfaceBlockBuilder) super.stereotypes(collection);
    }

    @Override
    public InterfaceBlockBuilder ownedElement(Element element) {
        return (InterfaceBlockBuilder) super.ownedElement(element);
    }

    @Override
    public InterfaceBlockBuilder ownedElements(Collection collection) {
        return (InterfaceBlockBuilder) super.ownedElements(collection);
    }

    @Override
    public InterfaceBlockBuilder ownedElement(AGenericBuilder builder) {
        return (InterfaceBlockBuilder) super.ownedElement(builder);
    }

    @Override
    public InterfaceBlockBuilder ownedElementBuilders(Collection collection) {
        return (InterfaceBlockBuilder) super.ownedElementBuilders(collection);
    }

    @Override
    public InterfaceBlockBuilder defaultOwner(Element defaultOwner) {
        this.defaultOwner = defaultOwner;
        return this;
    }

    @Override
    public InterfaceBlockBuilder defaultOwner(AGenericBuilder defaultOwner_Builder) {
        this.defaultOwner_Builder = defaultOwner_Builder;
        return this;
    }

    @Override
    public InterfaceBlockBuilder clone() throws CloneNotSupportedException {
        return (InterfaceBlockBuilder) super.clone();
    }

    @Override
    protected void preBuild() throws BuilderException {
        //preBuild
        if (name == null)
            name = "";
        boolean elementToBuildHasNoOwner = defaultOwner == null && (builderToReuse != null && builderToReuse.getOwner() == null);
        if (elementToBuildHasNoOwner)
            throw new BuilderException("Type: " + name + " owner is null", this);
    }

    @Override
    public Type build() throws BuilderException {
        if (this.createNewElement)
            this.elementToBuild = SysMLFactory.getInstance().createInterfaceBlock();
        super.build();
        Type type = (Type) this.elementToBuild;

        return type;
    }

    @Override
    public Type rebuild() throws BuilderException {
        return super.rebuild();
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

    public List<FlowPropertyBuilder> getL_flowPropertyBuilders() {
        return l_flowPropertyBuilders;
    }

    public void setL_flowPropertyBuilders(List<FlowPropertyBuilder> l_flowPropertyBuilders) {
        this.l_flowPropertyBuilders = l_flowPropertyBuilders;
    }
}
