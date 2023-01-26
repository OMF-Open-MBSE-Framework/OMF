package com.samares.omf.core.feature.ruleengine;

import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares.omf.core.listeners.ruleEngineListener.ruleEngine.IRuleEngine;

public interface IFeatureRuleEngine extends IRuleEngine, PriorityProvider {

    void setPriority(int priority);

    String getCategory();
    void setCategory(String category);
}
