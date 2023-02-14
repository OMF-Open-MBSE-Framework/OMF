/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature.registrables.rule_engines.rule_engine;

import com.samares.omf.core.plugin.APlugin;

public class FeatureRuleEngine extends RuleEngine implements IFeatureRuleEngine {
    private int priority = -1;
    private String category = "";

    public FeatureRuleEngine(APlugin plugin, RECategoryEnum category){
        this(plugin, category, -1);
    }

    public FeatureRuleEngine(APlugin plugin, RECategoryEnum category, int priority){
        this(plugin, category.toString(), priority);
    }

    public FeatureRuleEngine(APlugin plugin, String category){
        this(plugin, category, -1);
    }

    public FeatureRuleEngine(APlugin plugin, String category, int priority){
        super(plugin);
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
}
