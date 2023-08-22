package com.samares_engineering.omf.omf_example_plugin.privatefeaturelibrary.patterncreation.profile;

import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.profiles.ProfileCache;
import com.nomagic.profiles.ProfileImplementation;
import com.nomagic.profiles.ProfilesBridge;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.project.ElementProject;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

@SuppressWarnings("WeakerAccess, unused")
public class PatternCreatorProfile extends ProfileImplementation
{
    public static final String PROFILE_URI =  "";

    public static final String PROFILE_NAME =  "PatternCreatorProfile";

    private final OnCreationStereotype onCreationStereotype;
    private final PatternInstanceStereotype patternInstanceStereotype;
    private final PatternTemplateStereotype patternTemplateStereotype;
    private final PossiblePatternCreationOwnerStereotype possiblePatternCreationOwnerStereotype;

    public static PatternCreatorProfile getInstance(BaseElement baseElement)
    {
        PatternCreatorProfile profile = ProfilesBridge.getProfile(PatternCreatorProfile.class, baseElement);
        if (profile == null)
        {
            return ProfilesBridge.createProfile(PatternCreatorProfile.class, baseElement, PatternCreatorProfile::new, PROFILE_NAME, PROFILE_URI);
        }
        return profile;
    }
    public static PatternCreatorProfile getInstanceByProject(ElementProject project)
    {
        PatternCreatorProfile profile = ProfilesBridge.getProfile(PatternCreatorProfile.class, project);
        if (profile == null)
        {
            return ProfilesBridge.createProfile(PatternCreatorProfile.class, project, PatternCreatorProfile::new, PROFILE_NAME, PROFILE_URI);
        }
        return profile;
    }
    public  PatternCreatorProfile(ProfileCache cache)
    {
        super(cache);
        onCreationStereotype = new OnCreationStereotype(this);
        patternInstanceStereotype = new PatternInstanceStereotype(this);
        patternTemplateStereotype = new PatternTemplateStereotype(this);
        possiblePatternCreationOwnerStereotype = new PossiblePatternCreationOwnerStereotype(this);

    }

    public static PatternCreatorProfile getInstance() {
        return getInstance(OMFUtils.currentProject);
    }

    public OnCreationStereotype onCreation()
    {
        return onCreationStereotype;
    }
    public PatternInstanceStereotype patternInstance()
    {
        return patternInstanceStereotype;
    }
    public PatternTemplateStereotype patternTemplate()
    {
        return patternTemplateStereotype;
    }
    public PossiblePatternCreationOwnerStereotype possiblePatternCreationOwner()
    {
        return possiblePatternCreationOwnerStereotype;
    }


    public static class OnCreationStereotype extends StereotypeWrapper
    {


        //stereotype OnCreation and its tags
        public static final String STEREOTYPE_NAME =  "OnCreation";

        private final PatternCreatorProfile _p;
        protected  OnCreationStereotype(PatternCreatorProfile profile)
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
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency)
            {
                PatternCreatorProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.onCreation().getStereotype());
            }
            return false;
        }

    }
    public static class PatternInstanceStereotype extends StereotypeWrapper
    {


        //stereotype Pattern Instance and its tags
        public static final String STEREOTYPE_NAME =  "Pattern Instance";

        private final PatternCreatorProfile _p;
        protected  PatternInstanceStereotype(PatternCreatorProfile profile)
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
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype)
            {
                PatternCreatorProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.patternInstance().getStereotype());
            }
            return false;
        }

    }
    public static class PatternTemplateStereotype extends StereotypeWrapper
    {


        //stereotype Pattern Template and its tags
        public static final String STEREOTYPE_NAME =  "Pattern Template";

        private final PatternCreatorProfile _p;
        protected  PatternTemplateStereotype(PatternCreatorProfile profile)
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
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element != null &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element != null)
            {
                PatternCreatorProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.patternTemplate().getStereotype());
            }
            return false;
        }

    }
    public static class PossiblePatternCreationOwnerStereotype extends StereotypeWrapper
    {


        //stereotype Possible Pattern Creation Owner and its tags
        public static final String STEREOTYPE_NAME =  "Possible Pattern Creation Owner";

        private final PatternCreatorProfile _p;
        protected  PossiblePatternCreationOwnerStereotype(PatternCreatorProfile profile)
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
        @Override
        public boolean is(@CheckForNull Element element)
        {
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency)
            {
                PatternCreatorProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.possiblePatternCreationOwner().getStereotype());
            }
            return false;
        }

    }

    @Override
    protected Collection<ProfileElementWrapper> generatedGetAllElementWrappers()
    {
        Collection<ProfileElementWrapper> wrappers = new ArrayList<>();
        wrappers.add(onCreationStereotype);
        wrappers.add(patternInstanceStereotype);
        wrappers.add(patternTemplateStereotype);
        wrappers.add(possiblePatternCreationOwnerStereotype);
        return wrappers;
    }


    @Override
    protected Collection<Stereotype> generatedGetAllStereotypes()
    {
        if (getProfile() != null)
        {
            final Collection<Stereotype> stereotypes = new HashSet<>();

            stereotypes.add(onCreationStereotype.getStereotype());
            stereotypes.add(patternInstanceStereotype.getStereotype());
            stereotypes.add(patternTemplateStereotype.getStereotype());
            stereotypes.add(possiblePatternCreationOwnerStereotype.getStereotype());

            return stereotypes;
        }

        return Collections.emptyList();
    }


}
//MD5sum:AF316732A7A103CCE92087581B3BD039