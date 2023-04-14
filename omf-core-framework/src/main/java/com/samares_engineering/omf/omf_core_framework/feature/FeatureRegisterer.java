/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.feature.errors.FeatureException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.MDActionRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.OptionRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.RuleEngineRegisterer;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

import java.util.ArrayList;
import java.util.List;

public class FeatureRegisterer {
    private MDActionRegisterer uiActionRegisterer;
    private RuleEngineRegisterer ruleEngineRegisterer;
    private OptionRegisterer optionRegisterer;

    private List<MDFeature> registeredFeatures = new ArrayList<>();
    private final APlugin plugin;

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
     */
    public void registerFeature(MDFeature feature){
        if(isAlreadyRegistered(feature)) {
            OMFErrorHandler.handleException(new FeatureException("Trying to register feature " + feature.getName() +
                    " which is already registered.", GenericException.ECriticality.ALERT), false);
        }



        try {
            feature.initFeature(plugin);
            feature.setIsRegistered(true);

            registeredFeatures.add(feature);

            optionRegisterer.registerFeatureItems(feature.getOptions());
            uiActionRegisterer.registerFeatureItems(feature.getUIActions());
            ruleEngineRegisterer.registerFeatureItems(feature.getRuleEngines());
        } catch (FeatureException e) { //TODO: Act if feature need to be unregistered
            OMFErrorHandler.handleException(new FeatureException("Error while registering feature " + feature.getName(),
                    e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    public void registerFeatures(List<MDFeature> features){
        features.forEach(this::registerFeature);
    }

    /**
     * Registers feature items that have declared as "project only" until project is opened. This method is then called
     * every time the project opens.
     * On the first registration, the items are also initialised. We wait until the project to be opened to initialise
     * the items in order to avoid instances where the items need the project to be opened to function, for example if
     * you need to set a default value from the Sysml profile in an Option
     * @param feature
     */
    private void registerProjectOnlyFeatureItems(MDFeature feature) {
        feature.initProjectOnlyFeatureItems();

        try {
            optionRegisterer.registerFeatureItems(feature.getProjectOnlyOptions());
            uiActionRegisterer.registerFeatureItems(feature.getProjectOnlyUIActions());
            ruleEngineRegisterer.registerFeatureItems(feature.getProjectOnlyRuleEngines());
        } catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while registering project only items for feature " +
                    feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    public void registerProjectOnlyItemsOfFeatures(List<MDFeature> features) {
        features.forEach(this::registerProjectOnlyFeatureItems);
    }

    public void unregisterFeature(MDFeature feature){
        if (!isAlreadyRegistered(feature)) {
            OMFErrorHandler.handleException(new FeatureException("Trying to unregister feature " + feature.getName() +
                    " which is not registered.", GenericException.ECriticality.ALERT), false);
        }

        registeredFeatures.remove(feature);

        feature.setIsRegistered(false);

        try {
            uiActionRegisterer.unregisterFeatureItems(feature.getUIActions());
            uiActionRegisterer.unregisterFeatureItems(feature.getProjectOnlyUIActions());
            ruleEngineRegisterer.unregisterFeatureItems(feature.getRuleEngines());
            ruleEngineRegisterer.unregisterFeatureItems(feature.getProjectOnlyRuleEngines());
            optionRegisterer.unregisterFeatureItems(feature.getOptions());
            optionRegisterer.unregisterFeatureItems(feature.getProjectOnlyOptions());
        } catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while unregistering items for feature " +
                    feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    public void unregisterFeatures(List<MDFeature> features){
        features.forEach(this::unregisterFeature);
    }

    public void unregisterDelayedItemsOfFeatures(List<MDFeature> features){
        features.forEach(this::unregisterDelayedItemsOfFeature);
    }

    public void unregisterDelayedItemsOfFeature(MDFeature feature){
        try {
            uiActionRegisterer.unregisterFeatureItems(feature.getProjectOnlyUIActions());
            ruleEngineRegisterer.unregisterFeatureItems(feature.getProjectOnlyRuleEngines());
            optionRegisterer.unregisterFeatureItems(feature.getProjectOnlyOptions());
        } catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while unregistering project only items for feature " +
                    feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
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
