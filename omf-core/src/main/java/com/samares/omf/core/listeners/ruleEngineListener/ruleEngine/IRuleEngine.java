package com.samares.omf.core.listeners.ruleEngineListener.ruleEngine;


import com.samares.omf.core.listeners.ruleEngineListener.rules.IRule;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Optional;

public interface IRuleEngine {
    Optional<IRule> getMatchingRule(PropertyChangeEvent evt);

    List<IRule> getAllMatchingRules(PropertyChangeEvent evt);

    boolean processFirstMatchingRule(PropertyChangeEvent evt);

    boolean processAllMatchingRule(PropertyChangeEvent evt);

    boolean skipRules(PropertyChangeEvent evt);

    void addRule(IRule rule);

    void addAllRules(List<IRule> lRules);

    void removeRule(IRule rule);

    void removeAllRules(List<IRule> lRules);

    void removeAllRules();
}
