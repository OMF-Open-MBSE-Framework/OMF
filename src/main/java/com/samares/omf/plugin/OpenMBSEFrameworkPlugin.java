/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.plugin;


import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
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
import com.samares.omf.plugin.features.dev.Dev;
import com.samares.omf.plugin.features.example1.ExampleFeature1;
import com.samares.omf.plugin.features.example3.ExampleFeature3;
import com.samares.omf.plugin.features.stereotypes.StereotypesFeature;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;


public class OpenMBSEFrameworkPlugin extends APlugin {
    @Override
    public List<MDFeature> initFeatures() {
        return List.of(
                new ExampleFeature1(),
                new Dev(),
                new ExampleFeature3(),
                new StereotypesFeature()
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
    public OMFEnvironmentOptionsGroup initFeatureRegisteringEnvironmentOptionGroup() {
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
                        .filter(opt -> opt.getName().equals(OMFEnvironmentOptionsGroup.ID_ACTIVATE_AUTOMATION))
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
