/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.listeners;

import com.samares.omf.core.feature.ruleengine.RECategoryEnum;
import com.samares.omf.core.listeners.ruleEngineListener.ruleEngine.IRuleEngine;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AElementListener implements IElementListener {
    private boolean activated;

    private HashMap<String, List<IRuleEngine>> m_event_ruleEngine = new HashMap<>();

    private int priority = 0;

    @Override
    public boolean isActivated() {
        return activated;
    }

    @Override
    public void activate() {
        this.activated = true;
    }

    @Override
    public void deactivate() {
        this.activated = false;
    }

    @Override
    public HashMap<String, List<IRuleEngine>> getRuleEngineMap() {
        return m_event_ruleEngine;
    }

    @Override
    public void setRuleEngineMap(HashMap<String, List<IRuleEngine>> m_event_ruleEngine) {
        this.m_event_ruleEngine = m_event_ruleEngine;
    }

    @Override
    public boolean manageAnalysis(PropertyChangeEvent event) {
        List<IRuleEngine> l_re = getRuleEngineMap().get(RECategoryEnum.ANALYSE.toString());
        return processAllMatchingRules(l_re, event);
    }

    @Override
    public boolean manageCreation(PropertyChangeEvent event) {
        List<IRuleEngine> l_re = getRuleEngineMap().get(RECategoryEnum.CREATE.toString());
        return processAllMatchingRules(l_re, event);
    }

    @Override
    public boolean manageUpdate(PropertyChangeEvent event) {
        List<IRuleEngine> l_re = getRuleEngineMap().get(RECategoryEnum.UPDATE.toString());
        return processAllMatchingRules(l_re, event);
    }

    @Override
    public boolean manageDeletion(PropertyChangeEvent event) {
        List<IRuleEngine> l_re = getRuleEngineMap().get(RECategoryEnum.DELETE.toString());
        return processAllMatchingRules(l_re, event);
    }

    /**
     * @return true if at least one rule matched
     */
    private boolean processAllMatchingRules(List<IRuleEngine> l_re, PropertyChangeEvent event) {
        if(l_re == null)
            return false;
        return l_re.stream()
                .map(ruleEngine -> ruleEngine.processAllMatchingRule(event))
                .collect(Collectors.toList())
                .contains(true);
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
