/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test;

import com.nomagic.magicdraw.commandline.CommandLineActionManager;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFMainMenuConfigurator;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.ProjectListener;
import com.samares_engineering.omf.omf_core_framework.plugin.AOMFPlugin;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares_engineering.omf.omf_example_plugin.test.feature.FeatureCopyID;
import com.samares_engineering.omf.omf_example_plugin.test.suites.batches.LocalAPIBatch;
import com.samares_engineering.omf.omf_example_plugin.test.suites.batches.LocalBatch;
import com.samares_engineering.omf.omf_example_plugin.test.suites.batches.TWCBatch;
import com.samares_engineering.omf.omf_public_features.testGeneration.TestGenerationFeature;
import com.samares_engineering.omf.omf_test_framework.BatchLauncher;
import com.samares_engineering.omf.omf_test_framework.plugin.AOMFTestPlugin;
import com.samares_engineering.omf.omf_test_framework.plugin.OMFTestPlugin;
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatch;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class OMFExampleTestPlugin extends AOMFTestPlugin {

    // List the test batches to be executed here
    public static final List<Class<? extends ATestBatch>> TEST_BATCHES = Arrays.asList(
            TWCBatch.class,
            LocalAPIBatch.class,
            LocalBatch.class
    );

    @Override
    public void onPluginInit() {
        // TODO find a way to get the path to set from gradle properties
        System.setProperty("tests.resources", "plugins/com.samares_engineering.omf.omf_example_plugin.test/projects");

        if (Objects.equals(System.getProperty("test"), "true")) {
            CommandLineActionManager.getInstance().addAction(new BatchLauncher(TEST_BATCHES));
        } else if (Objects.equals(System.getProperty("test"), "false")) {
            String serverAddress = System.getProperty("serverIp");
            String serverUser = System.getProperty("userName");
            String userPassword = System.getProperty("userPwd");
            String initProjectName = System.getProperty("projectInitName");
            String finalProjectName = System.getProperty("projectFinalName");
            String pathToSaveProjects = System.getProperty("saveLocation");

            CommandLineActionManager.getInstance().addAction(new ProjectRetriever(serverAddress, serverUser, userPassword, initProjectName, finalProjectName, pathToSaveProjects));
        }
    }

    @Override
    public List<OMFFeature> initFeatures() {
        return List.of(
                new FeatureCopyID(),
                new TestGenerationFeature()
        );
    }

    @Override
    public OMFBrowserConfigurator initFeatureRegisteringBrowserConfigurator() {
        return new OMFBrowserConfigurator();
    }

    @Override
    public OMFDiagramConfigurator initFeatureRegisteringDiagramConfigurator() {
        return new OMFDiagramConfigurator();
    }

    @Override
    public OMFMainMenuConfigurator initFeatureRegisteringMainMenuConfigurator() {
        return new OMFMainMenuConfigurator();
    }

    @Override
    public OMFPropertyOptionsGroup initFeatureRegisteringEnvironmentOptionGroup() {
        return new OMFPropertyOptionsGroup("omf.example.test.plugin", "OMF Example Test Plugin");
    }

    @Override
    public FeatureProjectOptionsConfigurator initFeatureRegisteringProjectOptionGroup() {
        return FeatureProjectOptionsConfigurator.getInstance();
    }

    @Override
    public ProjectListener initProjectListener() {
        return new ProjectListener(this);
    }

    @Override
    public IListenerManager initListenerManager() {
        return ListenerManager.getInstance();
    }

}

