/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test;

import com.nomagic.magicdraw.commandline.CommandLineActionManager;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFBrowserConfigurator;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.feature.registrables.options.option.AOptionListener;
import com.samares.omf.core.plugin.APlugin;
import com.samares.omf.core.listeners.IListenerManager;
import com.samares.omf.core.listeners.ListenerManager;
import com.samares.omf.core.listeners.listeners.ProjectListener;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFDiagramConfigurator;
import com.samares.omf.core.feature.registrables.actions.actions.configurators.OMFMainMenuConfigurator;
import com.samares.omf.core.ui.environmentoptions.OMFEnvironmentOptionsGroup;
import com.samares.omf.core.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares.omf.test.BatchLauncher;
import com.samares.omf.plugin.test.feature.FeatureCopyID;
import com.samares.omf.plugin.test.suites.batches.TWCBatch;
import com.samares.omf.test.templates.ATestBatch;
import com.samares.omf.plugin.test.ui.TestMainMenuConfigurator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TestPlugin extends APlugin {

    // List the test batches to be executed here
    public static final List<Class<? extends ATestBatch>> TEST_BATCHES = Arrays.asList(
            TWCBatch.class
    );

    @Override
    public void init() {
        super.init();

        // TODO find a way to get the path to set from gradle properties
        System.setProperty("tests.resources", "plugins/com.samares.omf.plugin.test/projects");

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
    public List<MDFeature> getFeaturesRegisteredOnPluginInit() {
        return List.of(
                new FeatureCopyID()
        );
    }

    @Override
    public OMFBrowserConfigurator getFeatureRegisteringBrowserConfigurator() {
        return new OMFBrowserConfigurator();
    }

    @Override
    public OMFDiagramConfigurator getFeatureRegisteringDiagramConfigurator() {
        return new OMFDiagramConfigurator();
    }

    @Override
    public OMFMainMenuConfigurator getFeatureRegisteringMainMenuConfigurator() {
        return new TestMainMenuConfigurator();
    }

    @Override
    public List<MDFeature> getFeaturesRegisteredOnProjectOpening() {
        return Collections.emptyList();
    }

    @Override
    public OMFEnvironmentOptionsGroup getFeatureRegisteringEnvironmentOptionGroup() {
        return null;
    }

    @Override
    public FeatureProjectOptionsConfigurator getFeatureRegisteringProjectOptionGroup() {
        return FeatureProjectOptionsConfigurator.getInstance();
    }

    @Override
    public ProjectListener getProjectListener() {
        return new ProjectListener();
    }

    @Override
    public IListenerManager getListenerManager() {
        return ListenerManager.getInstance();
    }

    @Override
    public boolean close() {
        return true;
    }

    @Override
    public boolean isSupported() {
        return true;
    }

    @Override
    public List<AOptionListener> getEnvironmentOptionsListener() {
        return Collections.emptyList();
    }
}

