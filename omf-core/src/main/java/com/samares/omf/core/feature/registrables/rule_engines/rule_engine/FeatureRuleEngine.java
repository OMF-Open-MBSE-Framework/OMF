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

    public FeatureRuleEngine(RECategoryEnum category){
        this(category, -1);
    }

    public FeatureRuleEngine(RECategoryEnum category, int priority){
        this(category.toString(), priority);
    }

    public FeatureRuleEngine(String category){
        this(category, -1);
    }

    public FeatureRuleEngine(String category, int priority){
        this.category = category;
        this.priority = priority;
    }

    @Override
    public void initRegisterableItem(MDFeature feature) {
        this.feature = feature;
        setListenerManager(feature.getPlugin().getListenerManager());
    }

    /*
    Accessors
     */

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
}
