/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly;

import com.nomagic.actions.AMConfigurator;
import com.nomagic.magicdraw.actions.ActionsProvider;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.UIActionConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFBrowserConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFDiagramConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.configurators.implementations.OMFMainMenuConfigurator;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UIActionFeatureItemRegisterer implements FeatureItemRegisterer<UIAction> {
    private final List<UIActionConfigurator> configurators = new ArrayList<>();
    private FeatureRegisterer featureRegisterer;

    public UIActionFeatureItemRegisterer(APlugin plugin) {
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
     * @param featureRegisterer
     */
    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }

    /**
     * Register a list of UIActions and refresh the configurators.
     * @param actions
     */
    @Override
    public void registerFeatureItems(List<UIAction> actions) {
        if (actions == null) {
            throw new OMFFeatureRegisteringException("Trying to register actions but passed action list is null");
        }
        try {
            actions.forEach(this::registerFeatureItem);
            registerActionsIntoMD();
        } catch (Exception e) {
            throw new OMFFeatureRegisteringException("Unable to register MDActions", e);
        }
    }

    /**
     * Unregister a list of UIActions and refresh the configurators.
     * @param actions
     */
    @Override
    public void unregisterFeatureItems(List<UIAction> actions) {
        if (actions == null) {
            throw new OMFFeatureRegisteringException("Trying to unregister actions but passed action list is null");
        }
        try {
            unregisterActionsFromMD();
            actions.forEach(this::unregisterFeatureItem);
            registerActionsIntoMD();
        } catch (Exception e) {
            throw new OMFFeatureRegisteringException("Unable to unregister MDActions", e);
        }
    }

    /*
    Only need to reset the menu configurator because the browser and diagram configurators are reset by magicdraw when
    opening the context menu
     */
    private void unregisterActionsFromMD() {
        configurators.stream().filter(AMConfigurator.class::isInstance)
                .forEach(c -> c.unregisterActionsFromMD(ActionsProvider.getInstance().getMainMenuActions()));
    }

    /*
    Only need to reset the menu configurator because the browser and diagram configurators are reset by magicdraw when
    opening the context menu
     */
    private void registerActionsIntoMD() {
        configurators.stream().filter(AMConfigurator.class::isInstance)
                .map(AMConfigurator.class::cast)
                .forEach(c -> c.configure(ActionsProvider.getInstance().getMainMenuActions()));
    }

    /**
     * Register a UIAction in the configurators.
     * @param action
     */
    @Override
    public void registerFeatureItem(UIAction action) {
        configurators.stream().filter(Objects::nonNull).forEach(c -> c.addAction(action));
    }

    /**
     * Unregister a UIAction in the configurators.
     * @param action
     */
    @Override
    public void unregisterFeatureItem(UIAction action) {
        configurators.stream().filter(Objects::nonNull).forEach(c -> c.removeAction(action));
    }

    @Override
    public void registerFeatureItems(MDFeature feature) {
        registerFeatureItems(feature.getUIActions());
    }

    @Override
    public void unregisterFeatureItems(MDFeature feature) {
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
}
