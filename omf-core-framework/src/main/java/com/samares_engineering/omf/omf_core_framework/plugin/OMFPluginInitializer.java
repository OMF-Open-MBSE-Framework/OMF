package com.samares_engineering.omf.omf_core_framework.plugin;

import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFMainMenuConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.magicdraw.MagicDrawHookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.ProjectListener;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;

import java.util.List;

public interface OMFPluginInitializer {

    /**
     * Define all the features registered by default at Plugin initialization.
     * NOTE: Features can be registered later, by code or the project is opened (use instead getOnProjectOpeningFeatureToRegister())
     *
     * @return List of feature to register at plugin initialization
     */
    List<OMFFeature> initFeatures();

    /**
     * Define the BrowserConfigurator to register at plugin initialization.
     * This Configurator will be used for FeatureRegistering
     *
     * @return BrowserConfigurator to register
     */
    OMFBrowserConfigurator initFeatureRegisteringBrowserConfigurator();

    /**
     * Define the DiagramConfigurator to register at plugin initialization.
     * This Configurator will be used for FeatureRegistering
     *
     * @return DiagramConfigurator to register
     */
    OMFDiagramConfigurator initFeatureRegisteringDiagramConfigurator();

    /**
     * Define the MainMenuConfigurator to register at plugin initialization.
     * This Configurator will be used for FeatureRegistering
     *
     * @return MainMenuConfigurator to register
     */
    OMFMainMenuConfigurator initFeatureRegisteringMainMenuConfigurator();

    /**
     * Define the EnvironmentOptionsGroup to register at plugin Initialization
     * This Configurator will be used for FeatureRegistering
     *
     * @return EnvironmentOptionsGroup to register
     */
    OMFPropertyOptionsGroup initFeatureRegisteringEnvironmentOptionGroup();

    /**
     * Define the ProjectOptionsGroup to register at plugin Initialization
     * This Configurator will be used for FeatureRegistering
     *
     * @return ProjectOptionsGroup to register
     */
    FeatureProjectOptionsConfigurator initFeatureRegisteringProjectOptionGroup();

    /**
     * Define the ProjectListener to register at plugin Initialization
     * This Listener will be used for FeatureRegistering at projectOpening and registration of ProjectOptions
     *
     * @return ProjectOptionsGroup to register
     */
    ProjectListener initProjectListener();

    /**
     * Define the ListenerManager to register at plugin Initialization
     * This ListenerManager will be used for FeatureRegistering with liveActions and all registration of listeners
     *
     * @return ProjectOptionsGroup to register
     */
    IListenerManager initListenerManager();

    MagicDrawHookExecutor initMagicDrawHookExecutor();

    FeatureRegisterer initFeatureRegisterer();

    /**
     * Override this method to add behavior at plugin init
     */
    void onPluginInit();

    List<AOptionListener> initEnvironmentOptionsListener();
}
