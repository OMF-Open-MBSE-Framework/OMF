package com.samares_engineering.omf.omf_core_framework.genarchimodel;

import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml2.Profiles;
import com.nomagic.profiles.ProfileCache;
import com.nomagic.profiles.ProfileImplementation;
import com.nomagic.profiles.ProfilesBridge;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.project.ElementProject;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@SuppressWarnings("WeakerAccess, unused")
public class PluginArchitectureProfile extends ProfileImplementation
{
    public static final String PROFILE_URI =  "";

    public static final String PROFILE_NAME =  "PluginArchitecture";

    private final MethodsStereotype methodsStereotype;

    public static PluginArchitectureProfile getInstance(){
        return getInstance(OMFUtils.getProject());
    }

    public static PluginArchitectureProfile getInstance(BaseElement baseElement)
    {
        PluginArchitectureProfile profile = ProfilesBridge.getProfile(PluginArchitectureProfile.class, baseElement);
        if (profile == null)
        {
            return ProfilesBridge.createProfile(PluginArchitectureProfile.class, baseElement, PluginArchitectureProfile::new, PROFILE_NAME, PROFILE_URI);
        }
        return profile;
    }
    public static PluginArchitectureProfile getInstanceByProject(ElementProject project)
    {
        PluginArchitectureProfile profile = ProfilesBridge.getProfile(PluginArchitectureProfile.class, project);
        if (profile == null)
        {
            return ProfilesBridge.createProfile(PluginArchitectureProfile.class, project, PluginArchitectureProfile::new, PROFILE_NAME, PROFILE_URI);
        }
        return profile;
    }
    public PluginArchitectureProfile(ProfileCache cache)
    {
        super(cache);
        methodsStereotype = new MethodsStereotype(this);

    }
    public MethodsStereotype methods()
    {
        return methodsStereotype;
    }


    public static class MethodsStereotype extends StereotypeWrapper
    {


        //stereotype methods and its tags
        public static final String STEREOTYPE_NAME =  "methods";
        public static final String OPERATION =  "operation";
        public static final String PARAMETERS =  "parameters";
        public static final String RETURN =  "return";

        private final PluginArchitectureProfile _p;
        @CheckForNull
        private Property operation;
        @CheckForNull
        private Property parameters;
        @CheckForNull
        private Property _return;
        protected  MethodsStereotype(PluginArchitectureProfile profile)
        {
            super(profile);
            _p = profile;
        }
        @Override
        @SuppressWarnings("ConstantConditions")
        public Stereotype getStereotype()
        {
            return getElementByName(STEREOTYPE_NAME);
        }
        @CheckForNull
        public Property getOperationProperty()
        {
            if (operation == null)
            {
                operation = getTagByName(getStereotype(), OPERATION);
            }
            return operation;
        }

        @CheckForNull
        public Property getParametersProperty()
        {
            if (parameters == null)
            {
                parameters = getTagByName(getStereotype(), PARAMETERS);
            }
            return parameters;
        }

        @CheckForNull
        public Property getReturnProperty()
        {
            if (_return == null)
            {
                _return = getTagByName(getStereotype(), RETURN);
            }
            return _return;
        }

        public void setOperation(Element element, @CheckForNull Element value)
        {
            Profiles.setValue(element, getStereotype(), getOperationProperty(), value);
        }
        public void clearOperation(Element element)
        {
            Profiles.clearValue(element, getOperationProperty());
        }

        @CheckForNull
        public Element getOperation(Element element)
        {
            return (Element)Profiles.getFirstValue(element, getOperationProperty());
        }

        public void setParameters(Element element, @CheckForNull java.util.Collection<? extends Element> value)
        {
            Profiles.setValue(element, getStereotype(), getParametersProperty(), value);
        }
        public void clearParameters(Element element)
        {
            Profiles.clearValue(element, getParametersProperty());
        }
        public void addParameters(Element element, Element value)
        {
            Profiles.addValue(element, getStereotype(), getParametersProperty(), value);
        }
        public void removeParameters(Element element, Element value)
        {
            java.util.List<Element> values = getParameters(element);
            if (values.remove(value)) {
                setParameters(element, values);
            }
        }
        @SuppressWarnings("unchecked")
        public java.util.List<Element> getParameters(Element element)
        {
            return (java.util.List<Element>)Profiles.getValue(element, getParametersProperty());
        }

        public void setReturn(Element element, @CheckForNull Element value)
        {
            Profiles.setValue(element, getStereotype(), getReturnProperty(), value);
        }
        public void clearReturn(Element element)
        {
            Profiles.clearValue(element, getReturnProperty());
        }

        @CheckForNull
        public Element getReturn(Element element)
        {
            return (Element)Profiles.getFirstValue(element, getReturnProperty());
        }

        @Override
        protected void clear()
        {
            super.clear();
            operation = null;
            parameters = null;
            _return = null;
        }
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property)
            {
                PluginArchitectureProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.methods().getStereotype());
            }
            return false;
        }

    }

    @Override
    protected Collection<ProfileElementWrapper> generatedGetAllElementWrappers()
    {
        Collection<ProfileElementWrapper> wrappers = new ArrayList<>();
        wrappers.add(methodsStereotype);
        return wrappers;
    }


    @Override
    protected Collection<Stereotype> generatedGetAllStereotypes()
    {
        if (getProfile() != null)
        {
            final Collection<Stereotype> stereotypes = new HashSet<>();

            stereotypes.add(methodsStereotype.getStereotype());

            return stereotypes;
        }

        return Collections.emptyList();
    }


}
//MD5sum:75D58C3A9168F7B753F492EAC62D298C