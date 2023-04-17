/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/


package com.samares_engineering.omf.omf_public_features.testGeneration;

import com.nomagic.magicdraw.properties.StringProperty;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFException;
import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_public_features.apiserver.APIEnvOptionsHelper;
import com.samares_engineering.omf.omf_public_features.testGeneration.actions.GenerateCreationTest;
import com.samares_engineering.omf.omf_public_features.testGeneration.actions.GenerateSnapshotTest;
import com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration.CodeGenerationException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestGenerationFeature extends AFeature {

    private final String SERVER_FEATURE_NAME = "APIServer Feature";

    // USAGE : Enter here the groups to save ans restore with test and snapshots
    public List<String> ENV_OPTION_GROUPIDS =
            Arrays.asList(
//                "env.options.omf.plugin"
//                ,"TABLE_OPTIONS_GROUP"
            );


    public List<String> PROJECT_OPTION_GROUPIDS = Arrays.asList("");

    public TestGenerationFeature(){
        super("[TEST GENERATION]");
    }

    public String getServerAdress() {
        try {
            MDFeature server_feature = getPlugin().getFeatureByName(SERVER_FEATURE_NAME);
            APIEnvOptionsHelper serverEnvOptions = APIEnvOptionsHelper.getInstance(server_feature);
            return serverEnvOptions.getServerURL() + ":" + serverEnvOptions.getServerPort();
        }
        catch (OMFException e) {
            OMFErrorHandler.handleException(new CodeGenerationException("The provided Server API Feature Name is incorrect.", e, GenericException.ECriticality.ALERT), false );
        }
        return "SERVER_NOT_FOUND";
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
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new GenerateCreationTest(),
                new GenerateSnapshotTest()
        );
    }

    @Override
    protected List<IUIAction> initProjectOnlyFeatureActions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<IOption> initOptions() {
        // Test Generation Root Path
        StringProperty testGenRootPathProp = new StringProperty(TestGenerationEnvOptionsHelper.TEST_GENERATION_ROOTPATH_ID,
                                                                TestGenerationEnvOptionsHelper.getTestGenerationRootPathDefaultValue());
        IOption testGenRootPath = createEnvOption(testGenRootPathProp, TestGenerationEnvOptionsHelper.TEST_GENERATION_ROOTPATH_GRP);


        // Test Generation Java Package
        StringProperty testGenJavaPackageProp = new StringProperty(TestGenerationEnvOptionsHelper.TEST_GENERATION_JAVAPACKAGE_ID,
                TestGenerationEnvOptionsHelper.getTestGenerationJavaPackageDefaultValue());
        IOption testGenJavaPackage = createEnvOption(testGenJavaPackageProp, TestGenerationEnvOptionsHelper.TEST_GENERATION_JAVAPACKAGE_GRP);

        return Arrays.asList(
                testGenRootPath,
                testGenJavaPackage
        );
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.EMPTY_LIST;
    }

}
