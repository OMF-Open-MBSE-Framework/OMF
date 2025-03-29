package com.samares_engineering.omf.omf_core_framework.plugin;

import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFMainMenuConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.magicdraw.MagicDrawHookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.LiveActionEngineFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.OptionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.UIActionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks.FeatureLifeCycleHookFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks.MagicDrawLifeCycleHookFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks.ProjectLifeCycleHookFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyLiveActionEngineFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyOptionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.ProjectOnlyUIActionFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.ProjectListener;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;

import java.util.List;
import java.util.Optional;

public interface OMFPlugin {

    void registerAllFeatures();

    void unregisterAllFeatures();

    //------------------------------------ GETTER SETTER ----------------------------------------------------//
    LiveActionEngineFeatureItemRegisterer getLiveActionEngineRegisterer();

    UIActionFeatureItemRegisterer getUiActionFeatureItemRegisterer();

    OptionFeatureItemRegisterer getOptionRegisterer();

    List<OMFFeature> getFeatures();

    Optional<OMFFeature> getFeatureByName(String name);

    FeatureRegisterer getFeatureRegisterer();

    boolean isInitialized();

    List<AOptionListener> getEnvironmentOptionsListener();

    FeatureProjectOptionsConfigurator getProjectOptionConfigurator();

    Optional<OMFPropertyOptionsGroup> getEnvironmentOptionsGroup();

    IListenerManager getListenerManager();

    ProjectListener getProjectListener();

    OMFBrowserConfigurator getBrowserConfigurator();

    OMFDiagramConfigurator getDiagramConfigurator();

    OMFMainMenuConfigurator getMenuConfigurator();

    String getName();

    ProjectOnlyUIActionFeatureItemRegisterer getProjectOnlyUiActionRegisterer();

    ProjectOnlyLiveActionEngineFeatureItemRegisterer getProjectOnlyLiveActionEngineFeatureItemRegisterer();

    ProjectOnlyOptionFeatureItemRegisterer getProjectOnlyOptionFeatureItemRegisterer();

    LiveActionEngineFeatureItemRegisterer getLiveActionEngineFeatureItemRegisterer();

    OptionFeatureItemRegisterer getOptionFeatureItemRegisterer();

    MagicDrawLifeCycleHookFeatureItemRegisterer getMagicDrawLifeCycleHookFeatureItemRegisterer();

    ProjectLifeCycleHookFeatureItemRegisterer getProjectLifeCycleHookFeatureItemRegisterer();

    FeatureLifeCycleHookFeatureItemRegisterer getFeatureLifeCycleHookFeatureItemRegisterer();

    MagicDrawHookExecutor getMagicDrawHookExecutor();
}
