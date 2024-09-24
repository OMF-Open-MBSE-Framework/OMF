/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.UIActionConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFMainMenuConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UIActionFeatureItemRegisterer implements FeatureItemRegisterer<UIAction> {
    private final List<UIActionConfigurator> configurators = new ArrayList<>();
    private FeatureRegisterer featureRegisterer;
    final  List<UIAction> registeredFeatureItems = new ArrayList<>();

    public UIActionFeatureItemRegisterer(OMFPlugin plugin) {
        configurators.add(plugin.getBrowserConfigurator());
        configurators.add(plugin.getDiagramConfigurator());
        configurators.add(plugin.getMenuConfigurator());
    }

    /**
     * Initialize the registerer with the feature registerer.
     * This method is called by the {@link FeatureRegisterer} class.
     * It requires Configurators to be registered in the plugin:
     * a {@link OMFBrowserConfigurator},
     * a {@link OMFDiagramConfigurator}
     * and a {@link OMFMainMenuConfigurator}.
     */
    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }

    /**
     * Register a list of UIActions and refresh the configurators.
     */
    @Override
    public void registerFeatureItems(List<UIAction> actions) {
        if (actions == null) {
            throw new FeatureRegisteringException("Trying to register actions but passed action list is null");
        }
        try {
            actions.forEach(this::registerFeatureItem);
        } catch (Exception e) {
            throw new FeatureRegisteringException("Unable to register MDActions", e);
        }
    }

    /**
     * Unregister a list of UIActions and refresh the configurators.
     */
    @Override
    public void unregisterFeatureItems(List<UIAction> actions) {
        if (actions == null) {
            throw new FeatureRegisteringException("Trying to unregister actions but passed action list is null");
        }
        try {
            actions.forEach(this::unregisterFeatureItem);
        } catch (Exception e) {
            throw new FeatureRegisteringException("Unable to unregister MDActions", e);
        }
    }

    /**
     * Register a UIAction in the configurators.
     */
    @Override
    public void registerFeatureItem(UIAction action) {
        configurators.stream().filter(Objects::nonNull).forEach(c -> c.addRegisteredAction(action));
        registeredFeatureItems.add(action);
    }

    /**
     * Unregister a UIAction in the configurators.
     */
    @Override
    public void unregisterFeatureItem(UIAction action) {
        configurators.stream().filter(Objects::nonNull).forEach(c -> c.removeRegisteredAction(action));
        registeredFeatureItems.remove(action);
    }

    @Override
    public void registerFeatureItems(OMFFeature feature) {
        registerFeatureItems(feature.getUIActions());
    }

    @Override
    public void unregisterFeatureItems(OMFFeature feature) {
        unregisterFeatureItems(feature.getUIActions());
    }


    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegisterer;
    }

    @Override
    public void setFeatureRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }

    @Override
    public List<UIAction> getRegisteredFeatureItems() {
        return registeredFeatureItems;
    }

    public List<UIActionConfigurator> getConfigurators() {
        return configurators;
    }
}
