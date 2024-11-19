/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.builders.sysml;


import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.builders.exceptions.BuilderException;
import com.samares_engineering.omf.omf_core_framework.builders.generic.AGenericBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.uml.PropertyBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.uml.metaclasses.TypeBuilder;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.Collection;

public class FlowPropertyBuilder extends PropertyBuilder {
    protected boolean createNewElement = false;

    public FlowPropertyBuilder() {
    }

    public FlowPropertyBuilder(Property property) {
        super(property);
        stereotype(Profile.getInstance().getSysml().flowProperty().getStereotype());
    }

    public FlowPropertyBuilder(FlowPropertyBuilder flowPropertyBuilder) {
        this(flowPropertyBuilder, false);
    }

    public FlowPropertyBuilder(FlowPropertyBuilder flowPropertyBuilder, boolean fromInheritedConstructor) {
        super(flowPropertyBuilder, true);
        if (!fromInheritedConstructor)
            this.createNewElement = flowPropertyBuilder.createNewElement;
    }

    public FlowPropertyBuilder direction(SysMLProfile.FlowDirectionKindEnum direction) {
        this.direction = direction;
        return this;
    }


    public FlowPropertyBuilder withFlowProperty(Property flowProperty) {
        this.elementToBuild = flowProperty;
        return this;
    }

    @Override
    public FlowPropertyBuilder createNewElement() {
        this.createNewElement = true;
        return this;
    }

    @Override
    public FlowPropertyBuilder reuseBuilder(AGenericBuilder builderToReuse) {
        this.createNewElement = false;
        return (FlowPropertyBuilder) super.reuseBuilder(builderToReuse);
    }

    @Override
    public FlowPropertyBuilder cloneElement(Element elementToClone) {
        return (FlowPropertyBuilder) super.cloneElement(elementToClone);
    }


    //OVERRIDE TO IMPLEMENT FLUENT BUILDER PATTERN
    @Override
    public FlowPropertyBuilder withBase(Element element) {
        return (FlowPropertyBuilder) super.withBase(element);
    }

    @Override
    public FlowPropertyBuilder name(String name) {
        return (FlowPropertyBuilder) super.name(name);
    }

    @Override
    public FlowPropertyBuilder owner(Element owner) {
        return (FlowPropertyBuilder) super.owner(owner);
    }

    @Override
    public FlowPropertyBuilder stereotype(Stereotype stereotype) {
        return (FlowPropertyBuilder) super.stereotype(stereotype);
    }

    @Override
    public FlowPropertyBuilder stereotypes(Collection collection) {
        return (FlowPropertyBuilder) super.stereotypes(collection);
    }

    @Override
    public FlowPropertyBuilder ownedElement(Element element) {
        return (FlowPropertyBuilder) super.ownedElement(element);
    }

    @Override
    public FlowPropertyBuilder ownedElements(Collection collection) {
        return (FlowPropertyBuilder) super.ownedElements(collection);
    }

    @Override
    public FlowPropertyBuilder ownedElement(AGenericBuilder builder) {
        return (FlowPropertyBuilder) super.ownedElement(builder);
    }

    @Override
    public FlowPropertyBuilder ownedElementBuilders(Collection collection) {
        return (FlowPropertyBuilder) super.ownedElementBuilders(collection);
    }

    @Override
    public FlowPropertyBuilder defaultOwner(Element defaultOwner) {
        this.defaultOwner = defaultOwner;
        return this;
    }

    @Override
    public FlowPropertyBuilder defaultOwner(AGenericBuilder defaultOwnerBuilder) {
        this.defaultOwnerBuilder = defaultOwnerBuilder;
        return this;
    }

    @Override
    public FlowPropertyBuilder type(Type type) {
        return (FlowPropertyBuilder) super.type(type);
    }

    @Override
    public FlowPropertyBuilder type(TypeBuilder typeBuilder) {
        return (FlowPropertyBuilder) super.type(typeBuilder);
    }

    @Override
    public FlowPropertyBuilder clone() throws CloneNotSupportedException {
        return (FlowPropertyBuilder) super.clone();
    }

    @Override
    protected void preBuild() {
        //preBuild
        if (name == null)
            name = "";
        boolean elementToBuildHasNoOwner = defaultOwner == null && (builderToReuse != null && builderToReuse.getOwner() == null);
        if (elementToBuildHasNoOwner)
            throw new BuilderException("port: " + name + " owner is null", null);
    }

    public Property build() {
        if (this.createNewElement)
            this.elementToBuild = SysMLFactory.getInstance().createFlowProperty();
        super.build();
        Property flowProperty = (Property) this.elementToBuild;

        return flowProperty;
    }

    @Override
    public Property rebuild() {
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

    public SysMLProfile.FlowDirectionKindEnum getDirection() {
        return direction;
    }

    public void setDirection(SysMLProfile.FlowDirectionKindEnum direction) {
        this.direction = direction;
    }
}
