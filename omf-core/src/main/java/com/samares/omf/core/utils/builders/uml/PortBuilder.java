/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.builders.uml;


import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares.omf.core.utils.builders.BetaFactory;
import com.samares.omf.core.utils.builders.exceptions.BuilderException;
import com.samares.omf.core.utils.builders.generic.AGenericBuilder;
import com.samares.omf.core.utils.builders.uml.metaclasses.TypeBuilder;
import com.samares.omf.core.utils.errorManagement.OMFErrorHandler;

import java.util.Collection;

public class PortBuilder extends PropertyBuilder {
    protected boolean createNewElement = false;


    protected boolean isConjugated = false;
    protected boolean behavior = false;

    public PortBuilder() {

    }

    public PortBuilder(Port port) {
        super(port);
    }

    public PortBuilder(PortBuilder portBuilder) {
        this(portBuilder, false);
    }

    public PortBuilder(PortBuilder portBuilder, boolean fromInheritedConstructor) {
        super(portBuilder, true);
        if (!fromInheritedConstructor)
            this.createNewElement = portBuilder.createNewElement;
        this.isConjugated = portBuilder.isConjugated;
        this.behavior = portBuilder.behavior;

    }


    public PortBuilder ends(Collection<ConnectorEnd> ends) {
        this.ends = ends;
        return this;
    }

    public PortBuilder isConjugated(boolean isConjugated) {
        this.isConjugated = isConjugated;
        return this;
    }

    public PortBuilder behavior(boolean behavior) {
        this.behavior = behavior;
        return this;
    }

    public PortBuilder setTypeOwner(Element typeOwner) {
        if (this.type != null)
            type.setOwner(typeOwner);
        if (this.typeBuilder != null)
            typeBuilder.owner(typeOwner);
        return this;
    }


    @Override
    public PortBuilder reuseBuilder(AGenericBuilder builderToReuse) {
        this.createNewElement = false;
        return (PortBuilder) super.reuseBuilder(builderToReuse);
    }

    @Override
    public PortBuilder withBase(Element element) {
        return (PortBuilder) super.withBase(element);
    }

    @Override
    public PortBuilder createNewElement() {
        super.createNewElement();
        return this;
    }

    @Override
    public PortBuilder cloneElement(Element portToClone) {
        super.cloneElement(portToClone);
        createNewElement();
        Port port = (Port) portToClone;

        ends(port.getEnd()); //TODO: clone Connector??
        isConjugated(port.isConjugated());
        behavior(port.isBehavior());

        return this;
    }

    @Override
    public PortBuilder name(String name) {
        return (PortBuilder) super.name(name);
    }

    @Override
    public PortBuilder owner(Element owner) {
        return (PortBuilder) super.owner(owner);
    }

    @Override
    public PortBuilder stereotype(Stereotype stereotype) {
        return (PortBuilder) super.stereotype(stereotype);
    }

    @Override
    public PortBuilder stereotypes(Collection collection) {
        return (PortBuilder) super.stereotypes(collection);
    }

    @Override
    public PortBuilder ownedElement(Element element) {
        return (PortBuilder) super.ownedElement(element);
    }

    @Override
    public PortBuilder ownedElements(Collection collection) {
        return (PortBuilder) super.ownedElements(collection);
    }

    @Override
    public PortBuilder ownedElement(AGenericBuilder builder) {
        return (PortBuilder) super.ownedElement(builder);
    }

    @Override
    public PortBuilder ownedElementBuilders(Collection collection) {
        return (PortBuilder) super.ownedElementBuilders(collection);
    }

    @Override
    public PortBuilder type(Type type) {
        this.type = type;
        return this;
    }

    @Override
    public PortBuilder type(TypeBuilder typeBuilder) {
        this.typeBuilder = typeBuilder;
        return this;
    }

    @Override
    public PortBuilder defaultOwner(Element defaultOwner) {
        this.defaultOwner = defaultOwner;
        return this;
    }

    @Override
    public PortBuilder defaultOwner(AGenericBuilder defaultOwner_Builder) {
        this.defaultOwner_Builder = defaultOwner_Builder;
        return this;
    }

    @Override
    public PortBuilder clone() throws CloneNotSupportedException {
        return (PortBuilder) super.clone();
    }

    @Override
    protected void preBuild() throws BuilderException {
        //preBuild
        if (name == null)
            name = "";
        boolean elementToBuildHasNoOwner = defaultOwner == null && (builderToReuse != null && builderToReuse.getOwner() == null);
        if (elementToBuildHasNoOwner)
            throw new BuilderException("port: " + name + " owner is null", this);
        if (getTypeBuilder().getOwner() == null) {
            OMFErrorHandler.handleException(new BuilderException("TypeBuilder of port: " + name + " owner is null, default owner is set", this), false);
            getTypeBuilder().owner(owner);
        }

    }

    @Override
    public Port build() throws BuilderException {
        if (this.createNewElement)
            this.elementToBuild = BetaFactory.getInstance().magicDrawFactory.createPortInstance();
        super.build();      //Does it co to this preBuild?
        Port port = (Port) this.elementToBuild;

        if (null != ends)
            port.getEnd().addAll(ends);

        port.setConjugated(isConjugated);
        port.setBehavior(behavior);
        return port;
    }

    @Override
    public Port rebuild() throws BuilderException {
        return (Port) super.rebuild();
    }


    /**
     * GETTER/SETTERS
     **/
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

    public boolean isConjugated() {
        return isConjugated;
    }

    public void setConjugated(boolean conjugated) {
        isConjugated = conjugated;
    }

    public boolean isBehavior() {
        return behavior;
    }

    public void setBehavior(boolean behavior) {
        this.behavior = behavior;
    }


//  THIS COULD BE THE ULTIMATE METHOD TO DEEP CLONING or not..
    //    public static Map<String, Object> beanProperties(Object bean) {
//        try {
//            return Arrays.asList(
//                            Introspector.getBeanInfo(bean.getClass(), Object.class)
//                                    .getPropertyDescriptors()
//                    )
//                    .stream()
//                    // filter out properties with setters only
//                    .filter(pd -> Objects.nonNull(pd.getReadMethod()))
//                    .collect(Collectors.toMap(
//                            // bean property name
//                            PropertyDescriptor::getName,
//                            pd -> { // invoke method to get value
//                                try {
//                                    return pd.getReadMethod().invoke(bean);
//                                } catch (Exception e) {
//                                    // replace this with better error handling
//                                    return null;
//                                }
//                            }));
//        } catch (IntrospectionException e) {
//            // and this, too
//            return Collections.emptyMap();
//        }
//    }


}
