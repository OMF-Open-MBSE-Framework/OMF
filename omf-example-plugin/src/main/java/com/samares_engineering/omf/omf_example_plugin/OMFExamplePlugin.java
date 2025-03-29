/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since 0.0.0
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
import com.samares_engineering.omf.omf_example_plugin.features.demo.demo_option.OptionsDemoFeature;

import java.util.ArrayList;
import java.util.List;

public class OMFExamplePlugin extends AOMFPlugin {
    @Override
    public List<OMFFeature> initFeatures() {
        List<OMFFeature> features = new ArrayList<>();
//        List<OMFFeature> featuresToDeactivateByDefaultOnStartUp = List.of(
//                new ConnectionFeatureExample(),
//                new PatternCreationFeature(),
//                new StateActionExample()
//        );
//        features.addAll(featuresToDeactivateByDefaultOnStartUp);
        features.addAll(List.of(
//                new FeaturesDeactivationFeature(),
//                //USEFUL
//                new EnhancedDisplayFeature(),
//                new HyperLinkFeature(),
////                new LockSafeFeature(),
//                new DerivedPropertyExample(),
//                new APIServerFeature("http://localhost", 9850),
////
////                //USEFUL for Modeling
////                new StereotypesFeature(),
//                new MiscErgonomicFeature(),
//                new CloneElementFeature(),
////                new ConnectionFeatureExample(),
////                new PatternCreationFeature(),
////                new StateActionExample(),
//
////
////                //Examples
//                new GroupElementFeature(),
//                new SysMLBasicFeature()
//
////                new SandboxFeature(),
//                new GeneratePluginModelArchi(),
////                new ErrorManagementFeatureExample(),
////                new GeneratePluginModelArchi(),
//                new SysmlGptExploFeature(),
//                new TemplateFeature(),
//                new DiagramListenerFeature(),
//                new ElementSpecificationExample(),
//                new ExportDiagramImagesFeature(),
//                new FeatureExample()
//                new OpenProjectOnStartFeature("C:\\Workspace\\MBSE\\Untitled1.mdzip"),
//
//                new ListenersFeature(),
//                // Deactivation of features
//                new FeatureActivationFromOptionFeature()
//                        .onStartupDeactivate(featuresToDeactivateByDefaultOnStartUp)
                new OptionsDemoFeature()
        ));
        return features;
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
