/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature;

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
     * @param mdFeature
     * @return
     */
    public void registerFeature(MDFeature mdFeature){
        if(isAlreadyRegistered(mdFeature)) {
            return;
        }

        mdFeature.setPlugin(plugin);
        registeredFeatures.add(mdFeature);
        mdFeature.setRegistered(true);
        mdFeature.onRegistering();
        optionRegisterer.register(mdFeature);
        uiActionRegisterer.register(mdFeature);
        ruleEngineRegisterer.register(mdFeature);
    }

    public void registerFeatures(List<MDFeature> features){
        features.forEach(feature -> {
            feature.setPlugin(plugin);
            this.registerFeature(feature);
        });
    }

    public void unregisterFeature(MDFeature mdFeature){
        boolean isNotRegistered = !registeredFeatures.contains(mdFeature);
        registeredFeatures.remove(mdFeature);

        mdFeature.setRegistered(false);
        mdFeature.onUnregistering();
        uiActionRegisterer.unregister(mdFeature);
        ruleEngineRegisterer.unregister(mdFeature);
        optionRegisterer.unregister(mdFeature);
    }

    public void unregisterFeatures(List<MDFeature> features){
        features.forEach(this::unregisterFeature);
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
