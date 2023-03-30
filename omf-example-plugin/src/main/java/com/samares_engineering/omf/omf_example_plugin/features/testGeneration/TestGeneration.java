/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/


package com.samares_engineering.omf.omf_example_plugin.features.testGeneration;

import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_example_plugin.features.testGeneration.actions.GenerateCreationTest;
import com.samares_engineering.omf.omf_example_plugin.features.testGeneration.actions.GenerateSnapshotTest;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TestGeneration extends AFeature {

    public static final String SERVER_ADRESS = "http://localhost:9850/refmodel/";
    public String GENERATION_PATH = getProjectPath("main");
    public String GENERATION_TEST_PATH = getProjectPath("test");

    // USAGE : Enter here the groups to save ans restore with test and snapshots
    public List<String> ENV_OPTION_GROUPIDS =
            Arrays.asList(
//                "env.options.omf.plugin"
//                ,"TABLE_OPTIONS_GROUP"
            );


    public List<String> PROJECT_OPTION_GROUPIDS = Arrays.asList("");

    public TestGeneration(){
        super("[TEST GENERATION]");
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Arrays.asList(
                new GenerateCreationTest(),
                new GenerateSnapshotTest()
        );
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
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
        return Collections.EMPTY_LIST;
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.EMPTY_LIST;
    }


    /**
     * Get the absolute path of the root module of this project, then add /src/[repo]/java
     * @param repo
     * @return
     */
    private String getProjectPath(String repo){
        String filePath = new File("").getAbsolutePath();
        String slash;
        // Linux format
        if (filePath.contains("/")) {
            slash = "/";
        }
        else {
            slash = "\\";
        }

        filePath = filePath.substring(0, filePath.lastIndexOf(slash));
        filePath = filePath.substring(0, filePath.lastIndexOf(slash));
        filePath += slash + "src" + slash + repo + slash + "java";

        return filePath;
    }

}

