package com.samares_engineering.omf.omf_public_features.testGeneration;

import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

public class TestGenerationEnvOptionsHelper extends EnvOptionsHelper {
    public TestGenerationEnvOptionsHelper(TestGenerationFeature testGeneration) {
        super(testGeneration);
    }

    public static TestGenerationEnvOptionsHelper getInstance(OMFFeature feature) {
        return ((TestGenerationFeature) feature).getOptionsHelper();
    }

    /*
        Test Generation Root Path
     */
    public static final String TEST_GENERATION_ROOTPATH_GRP = "Test Generation";
    public static final String TEST_GENERATION_ROOTPATH_ID = "Test generation root path";

    public static String getTestGenerationRootPathDefaultValue() {
        return OMFUtils.getUserDir() + "/testCodeGenerated";
    }

    public String getTestGenerationRootPath() {
        StringProperty p = (StringProperty) getPropertyByName(TEST_GENERATION_ROOTPATH_ID);
        return p.getString();
    }

    public void setTestGenerationRootPath(String path) {
        getPropertyByName(TEST_GENERATION_ROOTPATH_ID).setValue(path);
    }

    /*
        Test Generation Java Package
    */
    public static final String TEST_GENERATION_JAVAPACKAGE_GRP = "Test Generation";
    public static final String TEST_GENERATION_JAVAPACKAGE_ID = "Test generation java package";

    public static String getTestGenerationJavaPackageDefaultValue() {
        return "generatedTestCode";
    }

    public String getTestGenerationJavaPackage() {
        StringProperty p = (StringProperty) getPropertyByName(TEST_GENERATION_JAVAPACKAGE_ID);
        return p.getString();
    }

    public void setTestGenerationJavaPackage(String path) {
        getPropertyByName(TEST_GENERATION_JAVAPACKAGE_ID).setValue(path);
    }
}
