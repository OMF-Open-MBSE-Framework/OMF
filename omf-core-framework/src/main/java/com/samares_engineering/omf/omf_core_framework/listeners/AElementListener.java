/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners;

import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;

import java.beans.PropertyChangeEvent;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AElementListener implements IElementListener {
    private boolean activated;
    private boolean isRegistered;

    private HashMap<String, List<IRuleEngine>> rulesEngines = new HashMap<>();

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
        return rulesEngines;
    }

    @Override
    public void setRuleEngineMap(HashMap<String, List<IRuleEngine>> rulesEngines) {
        this.rulesEngines = rulesEngines;
    }

    @Override
    public boolean manageAnalysis(PropertyChangeEvent event) {
        List<IRuleEngine> ruleEngines = getRuleEngineMap().get(RECategoryEnum.ANALYSE.toString());
        return processAllMatchingRules(ruleEngines, event);
    }

    @Override
    public boolean manageCreation(PropertyChangeEvent event) {
        List<IRuleEngine> ruleEngines = getRuleEngineMap().get(RECategoryEnum.CREATE.toString());
        return processAllMatchingRules(ruleEngines, event);
    }

    @Override
    public boolean manageUpdate(PropertyChangeEvent event) {
        List<IRuleEngine> ruleEngines = getRuleEngineMap().get(RECategoryEnum.UPDATE.toString());
        return processAllMatchingRules(ruleEngines, event);
    }

    @Override
    public boolean manageDeletion(PropertyChangeEvent event) {
        List<IRuleEngine> ruleEngines = getRuleEngineMap().get(RECategoryEnum.DELETE.toString());
        return processAllMatchingRules(ruleEngines, event);
    }

    /**
     * @return true if at least one rule matched
     */
    private boolean processAllMatchingRules(List<IRuleEngine> ruleEngines, PropertyChangeEvent event) {
        if(ruleEngines == null) return false;
        boolean hasRulesBeenTriggered = ruleEngines.stream()
                .map(ruleEngine -> ruleEngine.processAllMatchingRule(event))
                .collect(Collectors.toList())
                .contains(true);
        if(hasRulesBeenTriggered) OMFAutomationManager.getInstance().automationTriggered();
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
