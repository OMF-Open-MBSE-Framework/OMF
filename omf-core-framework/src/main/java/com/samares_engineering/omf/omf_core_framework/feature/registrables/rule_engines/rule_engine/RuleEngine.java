/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine;

import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule.IRule;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RuleEngine implements IRuleEngine {
    private IListenerManager listenerManager;
    private List<IRule> rules = new ArrayList<>();
    private String id = "";
    private int priority = -1;
    private String category = "";
    private MDFeature feature;

    public RuleEngine(RECategoryEnum category){
        this(category, -1);
    }

    public RuleEngine(RECategoryEnum category, int priority){
        this(category.toString(), priority);
    }

    public RuleEngine(String category){
        this(category, -1);
    }

    public RuleEngine(String category, int priority){
        this.category = category;
        this.priority = priority;
    }

    @Override
    public void initRegistrableItem(MDFeature feature) {
        this.feature = feature;
        setListenerManager(feature.getPlugin().getListenerManager());
    }

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
            listenerManager.deactivateAllListeners();
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

        listenerManager.deactivateAllListeners();
        matchingRules.stream()
                .forEach(rule -> rule.process(evt));

        return true;
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

    public MDFeature getFeature() {
        return feature;
    }

    public boolean skipRules(PropertyChangeEvent evt) {
        return false;
    }

    public void addRule(IRule rule) {
        rule.setRuleEngine(this);
        this.rules.add(rule);
    }
    public void addAllRules(List<IRule> lRules){
        lRules.forEach(this::addRule);
    }
    public void removeRule(IRule rule){
        this.rules.remove(rule);
    }
    public void removeRules(List<IRule> lRules){
        this.rules.removeAll(lRules);
    }
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

    public void setListenerManager(IListenerManager listenerManager) {
        this.listenerManager = listenerManager;
    }
    public IListenerManager getListenerManager() {
        return listenerManager;
    }
}
