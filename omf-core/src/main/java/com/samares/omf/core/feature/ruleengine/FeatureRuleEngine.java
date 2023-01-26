package com.samares.omf.core.feature.ruleengine;

import com.samares.omf.core.listeners.ruleEngineListener.ruleEngine.RuleEngine;

public class FeatureRuleEngine extends RuleEngine implements IFeatureRuleEngine {
    private int priority = -1;
    private String category = "";

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
        super();
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
