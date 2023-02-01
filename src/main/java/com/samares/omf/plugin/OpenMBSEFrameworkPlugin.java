/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares.omf.plugin;


import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.samares.omf.core.actions.v2.configurators.OMFBrowserConfigurator;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.feature.options.AOptionListener;
import com.samares.omf.core.plugin.APlugin;
import com.samares.omf.core.listeners.IListenerManager;
import com.samares.omf.core.listeners.OMFListenerManager;
import com.samares.omf.core.listeners.listeners.OMFProjectListener;
import com.samares.omf.core.ui.OMFDiagramConfigurator;
import com.samares.omf.core.ui.OMFMainMenuConfigurator;
import com.samares.omf.core.ui.environmentoptions.OMFEnvironmentOptionsGroup;
import com.samares.omf.core.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares.omf.plugin.features.dev.Dev;
import com.samares.omf.plugin.features.example1.ExampleFeature1;
import com.samares.omf.plugin.features.example3.ExampleFeature3;
import com.samares.omf.plugin.features.stereotypes.Stereotypes;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class OpenMBSEFrameworkPlugin extends APlugin {
    @Override
    public List<MDFeature> getDefaultFeatureRegistered() {
        return Arrays.asList(
                new ExampleFeature1(),
                new ExampleFeature3(),
                new Dev()
        );
    }

    @Override
    public List<MDFeature> getOnProjectOpeningFeatureToRegister() {
        return List.of(
                new Stereotypes()
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
        return new OMFMainMenuConfigurator();
    }

    @Override
    public OMFEnvironmentOptionsGroup getFeatureRegisteringEnvironmentOptionGroup() {
        return new OMFPluginEnvOptionsGroup();
    }

    @Override
    public FeatureProjectOptionsConfigurator getFeatureRegisteringProjectOptionGroup() {
        return FeatureProjectOptionsConfigurator.getInstance();
    }

    @Override
    public OMFProjectListener getProjectListener() {
        return new OMFProjectListener(getFeatureRegister());
    }

    @Override
    public IListenerManager getListenerManager() {
        return OMFListenerManager.getInstance();
    }

    @Override
    public List<AOptionListener> getEnvironmentOptionsListener() {
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
                                getFeatureRegister().registerAllFeatures(getDefaultFeatureRegistered());
                            else
                                getFeatureRegister().unregisterAllFeatures(getFeatureRegister().getRegisteredFeatures());

                        });
            }

            @Override
            public void propertyChange(PropertyChangeEvent evt) {

            }
        };
        return Collections.singletonList(optionListener);
    }
}
