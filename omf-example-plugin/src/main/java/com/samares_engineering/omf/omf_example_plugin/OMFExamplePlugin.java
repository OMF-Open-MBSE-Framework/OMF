/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin;

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
import com.samares_engineering.omf.omf_example_plugin.features.connection.ConnectionFeatureExample;
import com.samares_engineering.omf.omf_example_plugin.features.display.EnhancedDisplayFeature;
import com.samares_engineering.omf.omf_core_framework.genarchimodel.GeneratePluginModelArchi;
import com.samares_engineering.omf.omf_example_plugin.features.groupfeature.GroupElementFeature;
import com.samares_engineering.omf.omf_example_plugin.features.listeners.ListenersFeature;
import com.samares_engineering.omf.omf_example_plugin.features.sandbox.SandboxFeature;
import com.samares_engineering.omf.omf_example_plugin.features.stateactionfeature.StateActionExample;
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.SysMLBasicFeature;
import com.samares_engineering.omf.omf_public_features.activablefeatureoption.FeatureActivationFromOptionFeature;
import com.samares_engineering.omf.omf_public_features.apiserver.APIServerFeature;
import com.samares_engineering.omf.omf_public_features.clonefeature.CloneElementFeature;
import com.samares_engineering.omf.omf_public_features.featuredeactivation.FeaturesDeactivationFeature;
import com.samares_engineering.omf.omf_public_features.partblock_hyperttext.HyperLinkFeature;
import com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation.PatternCreationFeature;

import java.util.List;

public class OMFExamplePlugin extends AOMFPlugin {
    @Override
    public List<OMFFeature> initFeatures() {
        return List.of(
                new FeaturesDeactivationFeature(),
                //USEFUL
                new EnhancedDisplayFeature(),
                new HyperLinkFeature(),
//                new LockSafeFeature(),
                new APIServerFeature("http://localhost", 9850),
//
//                //USEFUL for Modeling
//                new StereotypesFeature(),
                new CloneElementFeature(),
                new GroupElementFeature(),
                new SysMLBasicFeature(),
                new ConnectionFeatureExample(),
                new PatternCreationFeature(),
                new StateActionExample(),
//
//                //Examples
                new SandboxFeature(),
                new GeneratePluginModelArchi(),
                //new ErrorManagementFeatureExample(),
//                new GeneratePluginModelArchi(),
//                new SysmlGptExploFeature(),
//                new TemplateFeature(),
//                new DiagramListenerFeature(),
//                new ElementSpecificationExample(),
//                new ExportDiagramImagesFeature(),

                new ListenersFeature(),
//                // Deactivation of features
                new FeatureActivationFromOptionFeature()
        );
    }

    @Override
    public void onPluginInit() {

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
}
