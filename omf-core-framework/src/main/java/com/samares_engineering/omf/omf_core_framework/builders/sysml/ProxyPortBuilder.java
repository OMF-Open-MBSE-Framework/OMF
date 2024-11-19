/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.builders.sysml;


import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.ConnectorEnd;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.builders.exceptions.BuilderException;
import com.samares_engineering.omf.omf_core_framework.builders.generic.AGenericBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.uml.PortBuilder;
import com.samares_engineering.omf.omf_core_framework.builders.uml.metaclasses.TypeBuilder;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.Collection;

public class ProxyPortBuilder extends PortBuilder {
    protected boolean createNewElement = false;

    public ProxyPortBuilder() {
    }

    public ProxyPortBuilder(Port port) {
        super(port);
        stereotype(Profile.getInstance().getSysml().proxyPort().getStereotype());
    }

    public ProxyPortBuilder(ProxyPortBuilder proxyPortBuilder) {
        this(proxyPortBuilder, false);
    }

    public ProxyPortBuilder(ProxyPortBuilder proxyPortBuilder, boolean fromInheritedConstructor) {
        super(proxyPortBuilder, true);
        if (!fromInheritedConstructor)
            this.createNewElement = proxyPortBuilder.createNewElement;
    }


    public ProxyPortBuilder isConjugated(boolean isConjugated) {
        this.isConjugated = isConjugated;
        return this;
    }

    public ProxyPortBuilder behavior(boolean behavior) {
        this.behavior = behavior;
        return this;
    }

    @Override
    public PortBuilder withBase(Element element) {
        return super.withBase(element);
    }

    @Override
    public ProxyPortBuilder createNewElement() {
        this.createNewElement = true;
        return this;
    }

    @Override
    public ProxyPortBuilder reuseBuilder(AGenericBuilder builderToReuse) {
        this.createNewElement = false;
        return (ProxyPortBuilder) super.reuseBuilder(builderToReuse);
    }

    @Override
    public ProxyPortBuilder cloneElement(Element portToClone) {
        super.cloneElement(portToClone);
        createNewElement();
        Port port = (Port) portToClone;

        ends(port.getEnd()); //TODO: clone Connector??
        isConjugated(port.isConjugated());
        behavior(port.isBehavior());

        return this;
    }

    @Override
    public ProxyPortBuilder name(String name) {
        return (ProxyPortBuilder) super.name(name);
    }

    @Override
    public ProxyPortBuilder owner(Element owner) {
        return (ProxyPortBuilder) super.owner(owner);
    }

    @Override
    public ProxyPortBuilder stereotype(Stereotype stereotype) {
        return (ProxyPortBuilder) super.stereotype(stereotype);
    }

    @Override
    public ProxyPortBuilder stereotypes(Collection collection) {
        return (ProxyPortBuilder) super.stereotypes(collection);
    }

    @Override
    public ProxyPortBuilder ownedElement(Element element) {
        return (ProxyPortBuilder) super.ownedElement(element);
    }

    @Override
    public ProxyPortBuilder ownedElements(Collection collection) {
        return (ProxyPortBuilder) super.ownedElements(collection);
    }

    @Override
    public ProxyPortBuilder ownedElement(AGenericBuilder builder) {
        return (ProxyPortBuilder) super.ownedElement(builder);
    }

    @Override
    public ProxyPortBuilder ownedElementBuilders(Collection collection) {
        return (ProxyPortBuilder) super.ownedElementBuilders(collection);
    }

    @Override
    public ProxyPortBuilder setTypeOwner(Element typeOwner) {
        return (ProxyPortBuilder) super.setTypeOwner(typeOwner);
    }

    @Override
    public ProxyPortBuilder type(Type type) {
        this.type = type;
        return this;
    }

    @Override
    public ProxyPortBuilder type(TypeBuilder typeBuilder) {
        this.typeBuilder = typeBuilder;
        return this;
    }

    @Override
    public ProxyPortBuilder withSharedType(Type sharedType) {
        return (ProxyPortBuilder) super.withSharedType(sharedType);
    }

    @Override
    public ProxyPortBuilder withSharedType(TypeBuilder sharedType) {
        return (ProxyPortBuilder) super.withSharedType(sharedType);
    }

    public ProxyPortBuilder withSharedInterface(Type sharedInterface) {
        return withSharedType(sharedInterface);
    }

    public ProxyPortBuilder withSharedInterface(TypeBuilder sharedInterface) {
        return withSharedType(sharedInterface);
    }

    @Override
    public ProxyPortBuilder ends(Collection<ConnectorEnd> ends) {
        this.ends = ends;
        return this;
    }

    @Override
    public ProxyPortBuilder clone() throws CloneNotSupportedException {
        return (ProxyPortBuilder) super.clone();
    }


    @Override
    public ProxyPortBuilder defaultOwner(Element defaultOwner) {
        this.defaultOwner = defaultOwner;
        return this;
    }

    @Override
    public ProxyPortBuilder defaultOwner(AGenericBuilder defaultOwnerBuilder) {
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

    @Override
    public Port build() {
        if (this.createNewElement)
            this.elementToBuild = SysMLFactory.getInstance().createProxyPort();
        super.build();      //Does it co to this preBuild?
        Port port = (Port) this.elementToBuild;

        return port;
    }

    @Override
    public Port rebuild() {
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
