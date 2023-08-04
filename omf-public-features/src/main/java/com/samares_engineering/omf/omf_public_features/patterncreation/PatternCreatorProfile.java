package com.samares_engineering.omf.omf_public_features.patterncreation;

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
    private final PatternStereotype patternStereotype;
    private final SourceDefaultCreationPatternStereotype sourceDefaultCreationPatternStereotype;

    public static PatternCreatorProfile getInstance(){
        return getInstance(OMFUtils.currentProject);
    }

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
        patternStereotype = new PatternStereotype(this);
        sourceDefaultCreationPatternStereotype = new SourceDefaultCreationPatternStereotype(this);

    }
    public OnCreationStereotype onCreation()
    {
        return onCreationStereotype;
    }
    public PatternStereotype pattern()
    {
        return patternStereotype;
    }
    public SourceDefaultCreationPatternStereotype sourceDefaultCreationPattern()
    {
        return sourceDefaultCreationPatternStereotype;
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
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DirectedRelationship &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DirectedRelationship)
            {
                PatternCreatorProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.onCreation().getStereotype());
            }
            return false;
        }

    }
    public static class PatternStereotype extends StereotypeWrapper
    {


        //stereotype Pattern and its tags
        public static final String STEREOTYPE_NAME =  "Pattern";

        private final PatternCreatorProfile _p;
        protected  PatternStereotype(PatternCreatorProfile profile)
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
                return instance.isTypeOf(element, instance.pattern().getStereotype());
            }
            return false;
        }

    }
    public static class SourceDefaultCreationPatternStereotype extends StereotypeWrapper
    {


        //stereotype SourceDefaultCreationPattern and its tags
        public static final String STEREOTYPE_NAME =  "SourceDefaultCreationPattern";

        private final PatternCreatorProfile _p;
        protected  SourceDefaultCreationPatternStereotype(PatternCreatorProfile profile)
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
                return instance.isTypeOf(element, instance.sourceDefaultCreationPattern().getStereotype());
            }
            return false;
        }

    }

    @Override
    protected Collection<ProfileElementWrapper> generatedGetAllElementWrappers()
    {
        Collection<ProfileElementWrapper> wrappers = new ArrayList<>();
        wrappers.add(onCreationStereotype);
        wrappers.add(patternStereotype);
        wrappers.add(sourceDefaultCreationPatternStereotype);
        return wrappers;
    }


    @Override
    protected Collection<Stereotype> generatedGetAllStereotypes()
    {
        if (getProfile() != null)
        {
            final Collection<Stereotype> stereotypes = new HashSet<>();

            stereotypes.add(onCreationStereotype.getStereotype());
            stereotypes.add(patternStereotype.getStereotype());
            stereotypes.add(sourceDefaultCreationPatternStereotype.getStereotype());

            return stereotypes;
        }

        return Collections.emptyList();
    }


}
//MD5sum:3F5AB42AB466F8974FFCEE3F5B6F9A1B