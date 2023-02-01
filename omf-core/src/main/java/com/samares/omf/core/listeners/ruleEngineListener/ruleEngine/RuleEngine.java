/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.listeners.ruleEngineListener.ruleEngine;

import com.samares.omf.core.plugin.APlugin;
import com.samares.omf.core.listeners.ruleEngineListener.rules.IRule;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RuleEngine implements IRuleEngine {
    private List<IRule> rules;
    private String      id = "";

    public RuleEngine(){ rules = new ArrayList<>();};

    /**
     * Find the highest priority rule (if it exists) matching the provided event
     * @param evt event to process
     * @return the rule found
     */
    @Override
    public Optional<IRule> getMatchingRule(PropertyChangeEvent evt){
        if (skipRules(evt)) {
            return Optional.empty();
        }
        return rules.stream()
                .filter(rule -> rule.isActivated() && rule.matches(evt))
                .findFirst();
    }
    @Override
    public List<IRule> getAllMatchingRules(PropertyChangeEvent evt){
        if (skipRules(evt))
            return new ArrayList<>();

        List<IRule> rulesToExecute = new ArrayList<>();

        for (IRule rule : rules) {  //return all matching rules until the first Blocking rule is found
            if(!rule.isActivated())
                continue;
            if (rule.matches(evt)) {
                rulesToExecute.add(rule);
                if (rule.isBlocking())
                    break;
            }

        }
        return rulesToExecute;

    }

    /**
     * Finds and processes the highest priority rule (if it exists) matching the provided event
     * @param evt event to process
     * @return true if a matching rule has been found and processed, false otherwise
     */
    @Override
    public boolean processFirstMatchingRule(PropertyChangeEvent evt) {
        Optional<IRule> matchingRule = getMatchingRule(evt);
        matchingRule.ifPresent(rule ->  {
            APlugin.getInstance().getListenerManager().deactivateAllListeners();
            rule.process(evt);
        });
        return matchingRule.isPresent();
    }
    /**
     * Finds and processes the highest priority rule (if it exists) matching the provided event
     * @param evt event to process
     * @return true if a matching rule has been found and processed, false otherwise
     */
    @Override
    public boolean processAllMatchingRule(PropertyChangeEvent evt) {
        List<IRule> matchingRules = getAllMatchingRules(evt);
        if(matchingRules.isEmpty())
            return false;

        APlugin.getInstance().getListenerManager().deactivateAllListeners();
        matchingRules.stream()
                .forEach(rule -> rule.process(evt));

        return true;
    }

    @Override
    public boolean skipRules(PropertyChangeEvent evt) {
        return false;
    }

    @Override
    public void addRule(IRule rule){
        this.rules.add(rule);
    }
    @Override
    public void addAllRules(List<IRule> lRules){
        this.rules.addAll(lRules);
    }
    @Override
    public void removeRule(IRule rule){
        this.rules.remove(rule);
    }
    @Override
    public void removeAllRules(List<IRule> lRules){
        this.rules.removeAll(lRules);
    }

    @Override
    public void removeAllRules(){
        this.rules.clear();
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public List<IRule> getRules() {
        return rules;
    }
    public void setRules(List<IRule> rules) {
        this.rules = rules;
    }
}
