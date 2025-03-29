/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since 0.0.0
 ******************************************************************************/


package com.samares_engineering.omf.omf_public_features.testGeneration;

import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_public_features.apiserver.OMFApiServer;
import com.samares_engineering.omf.omf_public_features.apiserver.exception.APIServerException;
import com.samares_engineering.omf.omf_public_features.testGeneration.actions.GenerateCreationTest;
import com.samares_engineering.omf.omf_public_features.testGeneration.actions.GenerateSnapshotTest;
import com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration.CodeGenerationException;

import java.util.Arrays;
import java.util.List;

public class TestGenerationFeature extends SimpleFeature {

    private final String SERVER_FEATURE_NAME = "APIServer Feature";

    // USAGE : Enter here the groups to save ans restore with test and snapshots
    public List<String> ENV_OPTION_GROUPIDS =
            Arrays.asList(
//                "env.options.omf.plugin"
//                ,"TABLE_OPTIONS_GROUP"
            );


    public List<String> PROJECT_OPTION_GROUPIDS = Arrays.asList("");

    public TestGenerationFeature() {
        super("[TEST GENERATION]");
    }

    public String getServerAddress() {
        try {
            return OMFApiServer.getInstance().getURI().toString();
        } catch (APIServerException e) {
            LegacyErrorHandler.handleException(new CodeGenerationException("API Server is not started", e, GenericException.ECriticality.ALERT), false);
            return "SERVER_NOT_STARTED";
        }
    }

    /**
     * @return Feature's env option helper cast to the correct subtype
     */
    public TestGenerationEnvOptionsHelper getOptionsHelper() {
        return (TestGenerationEnvOptionsHelper) getEnvOptionsHelper();
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return new TestGenerationEnvOptionsHelper(this);
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new GenerateCreationTest(),
                new GenerateSnapshotTest()
        );
    }

    @Override
    public List<Option> initOptions() {
        // Test Generation Root Path
        StringProperty testGenRootPathProp = new StringProperty(
                TestGenerationEnvOptionsHelper.TEST_GENERATION_ROOTPATH_ID,
                TestGenerationEnvOptionsHelper.getTestGenerationRootPathDefaultValue()
        );
        Option testGenRootPath = createEnvOption(
                testGenRootPathProp,
                TestGenerationEnvOptionsHelper.TEST_GENERATION_ROOTPATH_GRP
        );


        // Test Generation Java Package
        StringProperty testGenJavaPackageProp = new StringProperty(
                TestGenerationEnvOptionsHelper.TEST_GENERATION_JAVAPACKAGE_ID,
                TestGenerationEnvOptionsHelper.getTestGenerationJavaPackageDefaultValue()
        );
        Option testGenJavaPackage = createEnvOption(
                testGenJavaPackageProp,
                TestGenerationEnvOptionsHelper.TEST_GENERATION_JAVAPACKAGE_GRP
        );

        return Arrays.asList(
                testGenRootPath,
                testGenJavaPackage
        );
    }


}
