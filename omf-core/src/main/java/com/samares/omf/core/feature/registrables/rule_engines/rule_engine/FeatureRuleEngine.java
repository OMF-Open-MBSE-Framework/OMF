/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature.registrables.rule_engines.rule_engine;

import com.samares.omf.core.feature.MDFeature;

public class FeatureRuleEngine extends RuleEngine implements IFeatureRuleEngine {
    private int priority = -1;
    private String category = "";
    private MDFeature feature;

    public FeatureRuleEngine(MDFeature feature, RECategoryEnum category){
        this(feature, category, -1);
    }

    public FeatureRuleEngine(MDFeature feature, RECategoryEnum category, int priority){
        this(feature, category.toString(), priority);
    }

    public FeatureRuleEngine(MDFeature feature, String category){
        this(feature, category, -1);
    }

    public FeatureRuleEngine(MDFeature feature, String category, int priority){
        this.feature = feature;
        this.category = category;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public MDFeature getFeature() {
        return feature;
    }

    @Override
    public void setFeature(MDFeature feature) {
        this.feature = feature;
        setListenerManager(feature.getPlugin().getListenerManager());
        getRules().forEach(rule -> rule.setRuleEngine(this));
    }
}
