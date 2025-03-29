/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.builders.uml.metaclasses;


import com.nomagic.uml2.ext.jmi.helpers.StereotypesHelper;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.samares_engineering.omf.omf_core_framework.builders.exceptions.BuilderException;
import com.samares_engineering.omf.omf_core_framework.builders.generic.AGenericBuilder;

public abstract class NamedElementBuilder extends AGenericBuilder {

    public NamedElementBuilder() {
        super();
    }

    public NamedElementBuilder(NamedElement element) {
        super(element);
    }

    public NamedElementBuilder(NamedElementBuilder namedElementBuilder) {
        this(namedElementBuilder, false);
    }

    public NamedElementBuilder(NamedElementBuilder namedElementBuilder, boolean fromInheritedConstructor) {
        super(namedElementBuilder, true);
        if (!fromInheritedConstructor)
            this.createNewElement = namedElementBuilder.createNewElement;
    }

    @Override
    public NamedElementBuilder createNewElement() {
        this.createNewElement = true;
        return this;
    }

    @Override
    public NamedElementBuilder reuseBuilder(AGenericBuilder builderToReuse) {
        this.createNewElement = false;
        return (NamedElementBuilder) super.reuseBuilder(builderToReuse);
    }

    public NamedElementBuilder cloneElement(Element elementToClone) {
        NamedElement namedElement = (NamedElement) elementToClone;
        this.elementToBuild = namedElement;

        name(namedElement.getName());
        owner(namedElement.getOwner());
        stereotypes(StereotypesHelper.getStereotypes(namedElement));
        ownedElements(namedElement.getOwnedElement());
        return this;
    }

    @Override
    public abstract NamedElementBuilder clone() throws CloneNotSupportedException;

    @Override
    protected void preBuild() {
        //preBuild
        if (this.elementToBuild == null)
            throw new BuilderException("Element has not been initialized", this);
        if (name == null)
            name = "";
        boolean elementToBuildHasNoOwner = defaultOwnerBuilder == null && owner == null && (builderToReuse != null && builderToReuse.getOwner() == null);
        if (elementToBuildHasNoOwner)
            throw new BuilderException("Element: " + name + " owner is null", this);
    }

    @Override
    public NamedElement build() {
        if (this.builderToReuse != null)
            elementToBuild = builderToReuse.getElementToBuild();

        preBuild();
        applyStereotypes();
        setOwnedElements();

        NamedElement element = (NamedElement) elementToBuild;

        if (name != null)
            element.setName(name);

        boolean setOwnerBasedOnDefaultBuilderOwner = defaultOwnerBuilder != null && defaultOwnerBuilder.getOwner() != null && owner == null;
        if (setOwnerBasedOnDefaultBuilderOwner)
            element.setOwner(defaultOwnerBuilder.getOwner());

        if (defaultOwner != null && owner == null)
            element.setOwner(defaultOwner);

        if (owner != null)
            element.setOwner(owner);

        return element;
    }

    @Override
    public NamedElement rebuild() {
        elementToBuild = null;
        return null;
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

}
