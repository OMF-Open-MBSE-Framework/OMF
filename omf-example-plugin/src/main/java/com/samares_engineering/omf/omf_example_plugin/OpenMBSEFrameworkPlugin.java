/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.configurators.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.configurators.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.configurators.OMFMainMenuConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.ProjectListener;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares_engineering.omf.omf_example_plugin.features.example1.ExampleFeature1;
import com.samares_engineering.omf.omf_example_plugin.features.example3.ExampleFeature3;
import com.samares_engineering.omf.omf_public_features.testGeneration.TestGeneration;
import com.samares_engineering.omf.omf_example_plugin.options.OMFPluginEnvOptionsGroup;
import com.samares_engineering.omf.omf_public_features.apiserver.APIServerFeature;
import com.samares_engineering.omf.omf_public_features.dev.Dev;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesFeature;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

public class OpenMBSEFrameworkPlugin extends APlugin {
    @Override
    public List<MDFeature> initFeatures() {
        return List.of(
                new ExampleFeature1(),
                new ExampleFeature3(),
                new Dev(),
                new StereotypesFeature(),
                new APIServerFeature(),
                new TestGeneration()
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
        return new OMFPluginEnvOptionsGroup();
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
        var optionListener = new AOptionListener() {
            @Override
            public void updateByEnvironmentProperties(List<Property> list) {
                list.stream()
                        .filter(BooleanProperty.class::isInstance)
                        .map(BooleanProperty.class::cast)
                        .filter(opt -> opt.getName().equals(OMFPropertyOptionsGroup.ID_ACTIVATE_AUTOMATION))
                        .findFirst()
                        .ifPresent(opt -> {
                            if ((boolean) opt.getValue())
                                getFeatureRegister().registerFeatures(OpenMBSEFrameworkPlugin.this.getFeatures());
                            else
                                getFeatureRegister().unregisterFeatures(getFeatureRegister().getRegisteredFeatures());

                        });
            }

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

            }
        };
        return Collections.singletonList(optionListener);
    }
}
