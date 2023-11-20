package com.samares_engineering.omf.omf_public_features.testGeneration.profile;

import com.nomagic.magicdraw.sysml.util.SysMLProfile;
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
public class TestProfile extends ProfileImplementation
{
    public static final String PROFILE_URI =  "";

    public static final String PROFILE_NAME =  "TestProfile";

    private final DataFlowPropertyStereotype dataFlowPropertyStereotype;
    private final DataPortStereotype dataPortStereotype;
    private final GenericPortStereotype genericPortStereotype;
    private final TestPackageStereotype testPackageStereotype;

    public static TestProfile getInstance(BaseElement baseElement)
    {
        TestProfile profile = ProfilesBridge.getProfile(TestProfile.class, baseElement);
        if (profile == null)
        {
            return ProfilesBridge.createProfile(TestProfile.class, baseElement, TestProfile::new, PROFILE_NAME, PROFILE_URI);
        }
        return profile;
    }
    public static TestProfile getInstanceByProject(){
        return getInstanceByProject(OMFUtils.getProject());
    }
    public static TestProfile getInstanceByProject(ElementProject project)
    {
        TestProfile profile = ProfilesBridge.getProfile(TestProfile.class, project);
        if (profile == null)
        {
            return ProfilesBridge.createProfile(TestProfile.class, project, TestProfile::new, PROFILE_NAME, PROFILE_URI);
        }
        return profile;
    }
    public  TestProfile(ProfileCache cache)
    {
        super(cache);
        dataFlowPropertyStereotype = new DataFlowPropertyStereotype(this);
        dataPortStereotype = new DataPortStereotype(this);
        genericPortStereotype = new GenericPortStereotype(this);
        testPackageStereotype = new TestPackageStereotype(this);

    }
    public DataFlowPropertyStereotype dataFlowProperty()
    {
        return dataFlowPropertyStereotype;
    }
    public DataPortStereotype dataPort()
    {
        return dataPortStereotype;
    }
    public GenericPortStereotype genericPort()
    {
        return genericPortStereotype;
    }
    public TestPackageStereotype testPackage()
    {
        return testPackageStereotype;
    }


    public static class DataFlowPropertyStereotype extends StereotypeWrapper
    {


        //stereotype DataFlowProperty and its tags
        public static final String STEREOTYPE_NAME =  "DataFlowProperty";

        /**
         * Specifies if the property value is received from an external block (direction= in ), transmitted to an external Block (direction= out ) or both (direction= inout ).
         */public static final String DIRECTION =  "direction";

        private final TestProfile _p;
        @CheckForNull
        private Property direction;
        protected  DataFlowPropertyStereotype(TestProfile profile)
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
        public Property getDirectionProperty()
        {
            if (direction == null)
            {
                direction = getTagByName(getStereotype(), DIRECTION);
            }
            return direction;
        }

        public void setDirection(Element element, @CheckForNull SysMLProfile.FlowDirectionKindEnum value)
        {
            Profiles.setValue(element, getStereotype(), getDirectionProperty(), value != null ? value.getText() : null);
        }
        public void clearDirection(Element element)
        {
            Profiles.clearValue(element, getDirectionProperty());
        }

        @CheckForNull
        public SysMLProfile.FlowDirectionKindEnum getDirection(Element element)
        {
            return SysMLProfile.FlowDirectionKindEnum.from(Profiles.getFirstValue(element, getDirectionProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            direction = null;
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
                TestProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.dataFlowProperty().getStereotype());
            }
            return false;
        }

    }
    public static class DataPortStereotype extends StereotypeWrapper
    {


        //stereotype DataPort and its tags
        public static final String STEREOTYPE_NAME =  "DataPort";

        private final TestProfile _p;
        protected  DataPortStereotype(TestProfile profile)
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
            return element instanceof com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port)
            {
                TestProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.dataPort().getStereotype());
            }
            return false;
        }

    }
    public static class GenericPortStereotype extends StereotypeWrapper
    {


        //stereotype GenericPort and its tags
        public static final String STEREOTYPE_NAME =  "GenericPort";

        private final TestProfile _p;
        protected  GenericPortStereotype(TestProfile profile)
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
            return element instanceof com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port)
            {
                TestProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.genericPort().getStereotype());
            }
            return false;
        }

    }
    public static class TestPackageStereotype extends StereotypeWrapper
    {


        //stereotype TestPackage and its tags
        public static final String STEREOTYPE_NAME =  "TestPackage";

        private final TestProfile _p;
        protected  TestPackageStereotype(TestProfile profile)
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
            return element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package &&
                    _p.isTypeOf(element, getStereotype());
        }

        public static boolean isInstance(@CheckForNull Element element)
        {
            if(element instanceof com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package)
            {
                TestProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.testPackage().getStereotype());
            }
            return false;
        }

    }

    @Override
    protected Collection<ProfileElementWrapper> generatedGetAllElementWrappers()
    {
        Collection<ProfileElementWrapper> wrappers = new ArrayList<>();
        wrappers.add(dataFlowPropertyStereotype);
        wrappers.add(dataPortStereotype);
        wrappers.add(genericPortStereotype);
        wrappers.add(testPackageStereotype);
        return wrappers;
    }


    @Override
    protected Collection<Stereotype> generatedGetAllStereotypes()
    {
        if (getProfile() != null)
        {
            final Collection<Stereotype> stereotypes = new HashSet<>();

            stereotypes.add(dataFlowPropertyStereotype.getStereotype());
            stereotypes.add(dataPortStereotype.getStereotype());
            stereotypes.add(genericPortStereotype.getStereotype());
            stereotypes.add(testPackageStereotype.getStereotype());

            return stereotypes;
        }

        return Collections.emptyList();
    }


}
//MD5sum:7FCF9C663073187D4CF34C3FF4C11128
