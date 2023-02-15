/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature;

import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.exceptions.GenericException;
import com.samares.omf.core.feature.errors.FeatureException;
import com.samares.omf.core.feature.registrables.actions.MDActionRegisterer;
import com.samares.omf.core.feature.registrables.options.OptionRegisterer;
import com.samares.omf.core.feature.registrables.rule_engines.RuleEngineRegisterer;
import com.samares.omf.core.plugin.APlugin;

import java.util.ArrayList;
import java.util.List;

public class FeatureRegisterer {
    private MDActionRegisterer uiActionRegisterer;
    private RuleEngineRegisterer ruleEngineRegisterer;
    private OptionRegisterer optionRegisterer;

    private List<MDFeature> registeredFeatures = new ArrayList<>();
    private APlugin plugin;

    //TODO: Create a class regrouping all Configurators
    public FeatureRegisterer(APlugin plugin){
        this.plugin = plugin;
        this.uiActionRegisterer = new MDActionRegisterer(this);
        this.ruleEngineRegisterer = new RuleEngineRegisterer(this);
        this.optionRegisterer = new OptionRegisterer(this);
    }

    /**
     * Register a feature using delegation to register MDActions and RuleEngines. Return true if the feature is already registered;
     * @param feature
     * @return
     */
    public void registerFeature(MDFeature feature){
        if(isAlreadyRegistered(feature)) {
            OMFErrorHandler.handleException(new FeatureException("Trying to register feature " + feature.getName() +
                    " which is already registered.", GenericException.ECriticality.ALERT), false);
        }

        feature.initFeature(plugin);
        feature.setRegistered(true);

        registeredFeatures.add(feature);

        try {
            optionRegisterer.registerFeatureItems(feature.getOptions());
            uiActionRegisterer.registerFeatureItems(feature.getUIActions());
            ruleEngineRegisterer.registerFeatureItems(feature.getRuleEngines());
        } catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while registering feature " + feature.getName(),
                    e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    public void registerFeatures(List<MDFeature> features){
        features.forEach(this::registerFeature);
    }

    public void registerDelayedItemsOfFeatures(List<MDFeature> features) {
        features.forEach(this::registerDelayedFeatureItems);
    }

    private void registerDelayedFeatureItems(MDFeature feature) {
        feature.initDelayedFeatureItems();

        try {
            optionRegisterer.registerFeatureItems(feature.getDelayedOptions());
            uiActionRegisterer.registerFeatureItems(feature.getDelayedUIActions());
            ruleEngineRegisterer.registerFeatureItems(feature.getDelayedRuleEngines());
        } catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while registering delayed items for feature " +
                    feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    public void unregisterFeature(MDFeature feature){
        if (!isAlreadyRegistered(feature)) {
            OMFErrorHandler.handleException(new FeatureException("Trying to unregister feature " + feature.getName() +
                    " which is not registered.", GenericException.ECriticality.ALERT), false);
        }

        registeredFeatures.remove(feature);

        feature.setRegistered(false);

        try {
            uiActionRegisterer.unregisterFeatureItems(feature.getUIActions());
            uiActionRegisterer.unregisterFeatureItems(feature.getDelayedUIActions());
            ruleEngineRegisterer.unregisterFeatureItems(feature.getRuleEngines());
            ruleEngineRegisterer.unregisterFeatureItems(feature.getDelayedRuleEngines());
            optionRegisterer.unregisterFeatureItems(feature.getOptions());
            optionRegisterer.unregisterFeatureItems(feature.getDelayedOptions());
        } catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while unregistering items for feature " +
                    feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    public void unregisterFeatures(List<MDFeature> features){
        features.forEach(this::unregisterFeature);
    }

    public void unregisterDelayedItemsOfFeature(MDFeature feature){
        try {
            uiActionRegisterer.unregisterFeatureItems(feature.getDelayedUIActions());
            ruleEngineRegisterer.unregisterFeatureItems(feature.getDelayedRuleEngines());
            optionRegisterer.unregisterFeatureItems(feature.getDelayedOptions());
        } catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while unregistering delayed items for feature " +
                    feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    public void unregisterDelayedItemsOfFeatures(List<MDFeature> features){
        features.forEach(this::unregisterDelayedItemsOfFeature);
    }

    private boolean isAlreadyRegistered(MDFeature mdFeature) {
        return registeredFeatures.stream().anyMatch(mdFeature.getClass()::isInstance);
    }

    //-------------------------------- GETTER / SETTER --------------------------------------------
    public RuleEngineRegisterer getRuleEngineRegisterer() {
        return ruleEngineRegisterer;
    }
    public void setRuleEngineRegisterer(RuleEngineRegisterer ruleEngineRegisterer) {
        this.ruleEngineRegisterer = ruleEngineRegisterer;
    }
    public MDActionRegisterer getUiActionRegisterer() {
        return uiActionRegisterer;
    }
    public void setUiActionRegisterer(MDActionRegisterer uiActionRegisterer) {
        this.uiActionRegisterer = uiActionRegisterer;
    }
    public OptionRegisterer getOptionRegisterer() {
        return optionRegisterer;
    }
    public void setOptionRegisterer(OptionRegisterer optionRegisterer) {
        this.optionRegisterer = optionRegisterer;
    }
    public List<MDFeature> getRegisteredFeatures() {
        return registeredFeatures;
    }
    public void setRegisteredFeatures(List<MDFeature> registeredFeatures) {
        this.registeredFeatures = registeredFeatures;
    }

    public APlugin getPlugin() {
        return plugin;
    }
}
