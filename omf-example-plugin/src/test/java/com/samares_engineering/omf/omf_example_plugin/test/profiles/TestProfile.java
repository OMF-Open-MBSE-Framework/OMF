/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.profiles;

import com.nomagic.magicdraw.uml.BaseElement;
import com.nomagic.magicdraw.uml2.Profiles;
import com.nomagic.profiles.ProfileCache;
import com.nomagic.profiles.ProfileImplementation;
import com.nomagic.profiles.ProfilesBridge;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.nomagic.uml2.project.ElementProject;

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

    private final TestPackageStereotype testPackageStereotype;
    private final TestedElementStereotype testedElementStereotype;

    public static TestProfile getInstance(BaseElement baseElement)
    {
        TestProfile profile = ProfilesBridge.getProfile(TestProfile.class, baseElement);
        if (profile == null)
        {
            return ProfilesBridge.createProfile(TestProfile.class, baseElement, TestProfile::new, PROFILE_NAME, PROFILE_URI);
        }
        return profile;
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
        testPackageStereotype = new TestPackageStereotype(this);
        testedElementStereotype = new TestedElementStereotype(this);

    }
    public TestPackageStereotype testPackage()
    {
        return testPackageStereotype;
    }
    public TestedElementStereotype testedElement()
    {
        return testedElementStereotype;
    }


    public static class TestPackageStereotype extends StereotypeWrapper
    {


        //stereotype TestPackage and its tags
        public static final String STEREOTYPE_NAME =  "TestPackage";
        public static final String TESTDESCRIPTION =  "testDescription";
        public static final String TESTID =  "testID";
        public static final String TESTRESULTSDESCRIPTION =  "testResultsDescription";

        private final TestProfile _p;
        @CheckForNull
        private Property testDescription;
        @CheckForNull
        private Property testID;
        @CheckForNull
        private Property testResultsDescription;
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
        @CheckForNull
        public Property getTestDescriptionProperty()
        {
            if (testDescription == null)
            {
                testDescription = getTagByName(getStereotype(), TESTDESCRIPTION);
            }
            return testDescription;
        }

        @CheckForNull
        public Property getTestIDProperty()
        {
            if (testID == null)
            {
                testID = getTagByName(getStereotype(), TESTID);
            }
            return testID;
        }

        @CheckForNull
        public Property getTestResultsDescriptionProperty()
        {
            if (testResultsDescription == null)
            {
                testResultsDescription = getTagByName(getStereotype(), TESTRESULTSDESCRIPTION);
            }
            return testResultsDescription;
        }

        public void setTestDescription(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getTestDescriptionProperty(), value);
        }
        public void clearTestDescription(Element element)
        {
            Profiles.clearValue(element, getTestDescriptionProperty());
        }

        @CheckForNull
        public String getTestDescription(Element element)
        {
            return toString(Profiles.getFirstValue(element, getTestDescriptionProperty()));
        }

        public void setTestID(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getTestIDProperty(), value);
        }
        public void clearTestID(Element element)
        {
            Profiles.clearValue(element, getTestIDProperty());
        }

        @CheckForNull
        public String getTestID(Element element)
        {
            return toString(Profiles.getFirstValue(element, getTestIDProperty()));
        }

        public void setTestResultsDescription(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getTestResultsDescriptionProperty(), value);
        }
        public void clearTestResultsDescription(Element element)
        {
            Profiles.clearValue(element, getTestResultsDescriptionProperty());
        }

        @CheckForNull
        public String getTestResultsDescription(Element element)
        {
            return toString(Profiles.getFirstValue(element, getTestResultsDescriptionProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            testDescription = null;
            testID = null;
            testResultsDescription = null;
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
    public static class TestedElementStereotype extends StereotypeWrapper
    {


        //stereotype TestedElement and its tags
        public static final String STEREOTYPE_NAME =  "TestedElement";
        public static final String TESTID =  "testID";

        private final TestProfile _p;
        @CheckForNull
        private Property testID;
        protected  TestedElementStereotype(TestProfile profile)
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
        public Property getTestIDProperty()
        {
            if (testID == null)
            {
                testID = getTagByName(getStereotype(), TESTID);
            }
            return testID;
        }

        public void setTestID(Element element, @CheckForNull String value)
        {
            Profiles.setValue(element, getStereotype(), getTestIDProperty(), value);
        }
        public void clearTestID(Element element)
        {
            Profiles.clearValue(element, getTestIDProperty());
        }

        @CheckForNull
        public String getTestID(Element element)
        {
            return toString(Profiles.getFirstValue(element, getTestIDProperty()));
        }

        @Override
        protected void clear()
        {
            super.clear();
            testID = null;
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
                TestProfile instance = getInstance(element);
                return instance.isTypeOf(element, instance.testedElement().getStereotype());
            }
            return false;
        }

    }

    @Override
    protected Collection<ProfileElementWrapper> generatedGetAllElementWrappers()
    {
        Collection<ProfileElementWrapper> wrappers = new ArrayList<>();
        wrappers.add(testPackageStereotype);
        wrappers.add(testedElementStereotype);
        return wrappers;
    }


    @Override
    protected Collection<Stereotype> generatedGetAllStereotypes()
    {
        if (getProfile() != null)
        {
            final Collection<Stereotype> stereotypes = new HashSet<>();

            stereotypes.add(testPackageStereotype.getStereotype());
            stereotypes.add(testedElementStereotype.getStereotype());

            return stereotypes;
        }

        return Collections.emptyList();
    }


}
//MD5sum:2C0F4BFCC8A3979F244BAF4CB874F858