/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin;

import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFMainMenuConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.ProjectListener;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares_engineering.omf.omf_example_plugin.features.clonefeature.CloneElementFeature;
import com.samares_engineering.omf.omf_example_plugin.features.errorexample.ErrorManagementFeatureExample;
import com.samares_engineering.omf.omf_example_plugin.features.featureTemplate.TemplateFeature;
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.GeneratePluginModelArchi;
import com.samares_engineering.omf.omf_example_plugin.features.groupfeature.GroupElementFeature;
import com.samares_engineering.omf.omf_example_plugin.features.sysml_gpt_explo.SysmlGptExploFeature;
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.SysMLBasicFeature;
import com.samares_engineering.omf.omf_public_features.apiserver.APIServerFeature;
import com.samares_engineering.omf.omf_public_features.featuredeactivation.FeaturesDeactivationFeature;
import com.samares_engineering.omf.omf_public_features.lockmanager.LockSafeFeature;
import com.samares_engineering.omf.omf_public_features.partblock_hyperttext.HyperLinkFeature;
import com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation.PatternCreationFeature;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesFeature;

import java.util.Collections;
import java.util.List;

public class OMFExamplePlugin extends APlugin {
    @Override
    public List<MDFeature> initFeatures() {
        return List.of(
                new CloneElementFeature(),
                new GroupElementFeature(),
                new SysMLBasicFeature(),
                new FeaturesDeactivationFeature(),
                new ErrorManagementFeatureExample(),
                new HyperLinkFeature(),
                new LockSafeFeature(),
                new StereotypesFeature(),
                new APIServerFeature("http://localhost", 9850),
                new GeneratePluginModelArchi(),
                new SysmlGptExploFeature(),
                new PatternCreationFeature(),
                new TemplateFeature()
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
        return new OMFPropertyOptionsGroup("omf.example.plugin", "OMF Example Plugin");
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

    @Override
    public List<AOptionListener> initEnvironmentOptionsListener() {
       return Collections.emptyList();
    }
}
