/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.builders.generic;

import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.builders.exceptions.BuilderException;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


// TODO Finish or remove this WIP (same for the other builders)
public abstract class AGenericBuilder<ConcreteBuiltElement extends Element> implements IGenericBuilder, Cloneable, Serializable {
    protected ConcreteBuiltElement elementToBuild;
    protected String name;
    protected Element owner;

    protected boolean createNewElement = false;

    protected Set<Stereotype> stereotypes;

    protected Set<Element> ownedElements;
    protected Set<AGenericBuilder> ownedElementBuilder;
    protected AGenericBuilder builderToReuse;
    protected Element defaultOwner;
    protected AGenericBuilder defaultOwnerBuilder;

    protected AGenericBuilder() {
        this(null);
    }

    public AGenericBuilder(ConcreteBuiltElement elementToBuild) {
        this.elementToBuild = elementToBuild;
        stereotypes = new HashSet<>();
        ownedElements = new HashSet<>();
        ownedElementBuilder = new HashSet<>();
    }

    public AGenericBuilder(AGenericBuilder aGenericBuilder, boolean fromInheritedConstructor) {
        this.elementToBuild = (ConcreteBuiltElement) aGenericBuilder.elementToBuild;
        this.name = aGenericBuilder.name;
        this.owner = aGenericBuilder.owner;
        if (!fromInheritedConstructor)
            this.createNewElement = aGenericBuilder.createNewElement;
        this.builderToReuse = aGenericBuilder.builderToReuse;
        this.stereotypes = (Set<Stereotype>) aGenericBuilder.stereotypes.stream().collect(Collectors.toSet());
        this.ownedElements = (Set<Element>) aGenericBuilder.ownedElements.stream().collect(Collectors.toSet());
        this.ownedElementBuilder = (Set<AGenericBuilder>) aGenericBuilder.ownedElementBuilder.stream().collect(Collectors.toSet());
    }

    public AGenericBuilder reuseBuilder(AGenericBuilder builderToReuse) {
        this.createNewElement = false;
        this.builderToReuse = builderToReuse;
        return this;
    }

    public abstract AGenericBuilder createNewElement();

    public abstract AGenericBuilder cloneElement(ConcreteBuiltElement elementToClone);

    public AGenericBuilder withBase(ConcreteBuiltElement elementToBuild) {
        this.elementToBuild = elementToBuild;
        return this;
    }

    public AGenericBuilder name(String name) {
        this.name = name;
        return this;
    }

    public AGenericBuilder owner(Element owner) {
        this.owner = owner;
        return this;
    }

    public AGenericBuilder stereotype(Stereotype stereotype) {
        this.stereotypes.add(stereotype);
        return this;
    }

    public AGenericBuilder stereotypes(Collection<Stereotype> stereotypes) {
        this.stereotypes.addAll(stereotypes);
        return this;
    }

    public AGenericBuilder ownedElement(AGenericBuilder builder) {
        this.ownedElementBuilder.add(builder);
        return this;
    }

    public AGenericBuilder ownedElementBuilders(Collection<AGenericBuilder> builders) {
        this.ownedElementBuilder.addAll(builders);
        return this;
    }

    public AGenericBuilder ownedElement(Element element) {
        this.ownedElements.add(element);
        return this;
    }

    public AGenericBuilder ownedElements(Collection<Element> elements) {
        this.ownedElements.addAll(elements);
        return this;
    }

    public AGenericBuilder defaultOwner(Element defaultOwner) {
        this.defaultOwner = defaultOwner;
        return this;
    }

    public AGenericBuilder defaultOwner(AGenericBuilder defaultOwnerBuilder) {
        this.defaultOwnerBuilder = defaultOwnerBuilder;
        return this;

    }

    protected abstract void preBuild() throws BuilderException;

    public abstract ConcreteBuiltElement build() throws BuilderException;

    public abstract ConcreteBuiltElement rebuild() throws BuilderException;

    protected void applyStereotypes() {
        stereotypes.forEach(str -> StereotypesHelper.addStereotype(elementToBuild, str));
    }

    protected void setOwnedElements() {
        AtomicReference<BuilderException> exception = new AtomicReference<>();
        ownedElements.forEach(ownedElement -> ownedElement.setOwner(elementToBuild));
        ownedElementBuilder.stream()
                .map(builder -> builder.owner(elementToBuild))
                .forEach(aGenericBuilder -> {
                    try {
                        aGenericBuilder.build();
                    } catch (BuilderException e) {
                        exception.set(e);
                    }
                });
        if (exception.get() != null) throw exception.get();
    }

    public abstract AGenericBuilder clone() throws CloneNotSupportedException;

//        return (AGenericBuilder) SerializationUtils.clone(this);
//        return (AGenericBuilder) super.clone();



    /*
     * -----------------------------------------
     *  GETTERS AND SETTERS
     * -----------------------------------------
     */

    public ConcreteBuiltElement getElementToBuild() {
        return elementToBuild;
    }

    public void setElementToBuild(ConcreteBuiltElement elementToBuild) {
        this.elementToBuild = elementToBuild;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Element getOwner() {
        return owner;
    }

    public void setOwner(Element owner) {
        this.owner = owner;
    }

    public Set<Stereotype> getStereotypes() {
        return stereotypes;
    }

    public void setStereotypes(Set<Stereotype> stereotypes) {
        this.stereotypes = stereotypes;
    }

    public Set<Element> getOwnedElements() {
        return ownedElements;
    }

    public void setOwnedElements(Set<Element> ownedElements) {
        this.ownedElements = ownedElements;
    }

    public Set<AGenericBuilder> getOwnedElementBuilder() {
        return ownedElementBuilder;
    }

    public void setOwnedElementBuilder(Set<AGenericBuilder> ownedElementBuilder) {
        this.ownedElementBuilder = ownedElementBuilder;
    }

    public boolean isCreateNewElement() {
        return createNewElement;
    }

    public void setCreateNewElement(boolean createNewElement) {
        this.createNewElement = createNewElement;
    }

    public AGenericBuilder getBuilderToReuse() {
        return builderToReuse;
    }

    public void setBuilderToReuse(AGenericBuilder builderToReuse) {
        this.builderToReuse = builderToReuse;
    }

    public Element getDefaultOwner() {
        return defaultOwner;
    }

    public void setDefaultOwner(Element defaultOwner) {
        this.defaultOwner = defaultOwner;
    }

    public AGenericBuilder getDefaultOwnerBuilder() {
        return defaultOwnerBuilder;
    }

    public void setDefaultOwnerBuilder(AGenericBuilder defaultOwnerBuilder) {
        this.defaultOwnerBuilder = defaultOwnerBuilder;
    }
}
