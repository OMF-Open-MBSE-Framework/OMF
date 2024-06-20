/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AElementListener implements IElementListener {
    private boolean activated;
    private boolean isRegistered;

    private HashMap<String, List<ALiveActionEngine>> rulesEngines = new HashMap<>();

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
    public HashMap<String, List<ALiveActionEngine>> getRuleEngineMap() {
        return rulesEngines;
    }

    @Override
    public void setRuleEngineMap(HashMap<String, List<ALiveActionEngine>> rulesEngines) {
        this.rulesEngines = rulesEngines;
    }

    @Override
    public boolean manageAnalysis(PropertyChangeEvent event) {
        List<ALiveActionEngine> ruleEngines = getRuleEngineMap().get(LiveActionType.ANALYSE.toString());
        return processAllMatchingRules(ruleEngines, event);
    }

    @Override
    public boolean manageCreation(PropertyChangeEvent event) {
        List<ALiveActionEngine> ruleEngines = getRuleEngineMap().get(LiveActionType.CREATE.toString());
        return processAllMatchingRules(ruleEngines, event);
    }

    @Override
    public boolean manageUpdate(PropertyChangeEvent event) {
        List<ALiveActionEngine> ruleEngines = getRuleEngineMap().get(LiveActionType.UPDATE.toString());
        return processAllMatchingRules(ruleEngines, event);
    }

    @Override
    public boolean manageDeletion(PropertyChangeEvent event) {
        List<ALiveActionEngine> ruleEngines = getRuleEngineMap().get(LiveActionType.DELETE.toString());
        return processAllMatchingRules(ruleEngines, event);
    }

    @Override
    public boolean manageAfterAutomation(Collection<PropertyChangeEvent> l_events) {
        List<ALiveActionEngine> ruleEngines = getRuleEngineMap().get(LiveActionType.AFTER_AUTOMATION.toString());
        return l_events.stream().map(event -> processAllMatchingRules(ruleEngines, event)).anyMatch(b -> b);
    }

    /**
     * @return true if at least one rule matched
     */
    private boolean processAllMatchingRules(List<ALiveActionEngine> ruleEngines, PropertyChangeEvent event) {
        if(ruleEngines == null) return false;
        boolean hasRulesBeenTriggered = ruleEngines.stream()
                .map(ruleEngine -> ruleEngine.processAllMatchingRule(event))
                .collect(Collectors.toList())
                .contains(true);
        return hasRulesBeenTriggered;
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

    @Override
    public boolean isRegistered() {
        return isRegistered;
    }
    @Override
    public boolean isNotRegistered(){
        return !isRegistered;
    }

    @Override
    public void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }
}
