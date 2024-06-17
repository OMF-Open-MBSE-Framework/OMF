/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFDevException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.ColorPrinter;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.exceptions.ErrorWhileEvaluationRuleException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule.IRule;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiveAction implements ILiveAction {
    private IListenerManager listenerManager;
    private List<IRule<PropertyChangeEvent, PropertyChangeEvent>> rules = new ArrayList<>();
    private String id = "";
    private int priority = -1;
    private String category = "";
    private MDFeature feature;
    private boolean activated = true;

    public LiveAction(RECategoryEnum category){
        this(category, -1);
    }

    public LiveAction(RECategoryEnum category, int priority){
        this(category.toString(), priority);
    }

    public LiveAction(String category){
        this(category, -1);
    }

    public LiveAction(String category, int priority){
        this.category = category;
        this.priority = priority;
    }

    @Override
    public void initRegistrableItem(MDFeature feature) {
        this.feature = feature;
        setListenerManager(feature.getPlugin().getListenerManager());
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
    public boolean isActivated() {
        return activated;
    }

    /**
     * Find the highest priority rule (if it exists) matching the provided event
     *
     * @param evt event to process
     * @return the rule found
     */
    @Override
    public Optional<IRule<PropertyChangeEvent, PropertyChangeEvent>> getMatchingRule(PropertyChangeEvent evt){
        if (skipRules(evt)) {
            return Optional.empty();
        }
        return rules.stream()
                .filter(rule -> isRuleMatching(evt, rule))
                .findFirst();
    }

    @Override
    public List<IRule<PropertyChangeEvent, PropertyChangeEvent>> getAllMatchingRules(PropertyChangeEvent evt){
        if (skipRules(evt))
            return new ArrayList<>();

        List<IRule<PropertyChangeEvent, PropertyChangeEvent>> rulesToExecute = new ArrayList<>();

        for (IRule<PropertyChangeEvent, PropertyChangeEvent> rule : rules) {  //return all matching rules until the first Blocking rule is found
            if(isRuleMatching(evt, rule)){
                rulesToExecute.add(rule);
                ColorPrinter.status("Triggered rule: " + rule.getClass().getSimpleName() + " for event: " + evt.getPropertyName()
                        + " on element: " + ((Element) evt.getSource()).getHumanName());
                if (rule.isBlocking()) {
                    ColorPrinter.status("Rule is blocking: stopping rule matching for this event");
                    break;
                }
            }
        }
        return rulesToExecute;

    }

    private boolean isRuleMatching(PropertyChangeEvent evt, IRule<PropertyChangeEvent, PropertyChangeEvent> rule) {
        Boolean isMatching = OMFBarrierExecutor.executeWithinBarrier(() -> {
            try {
                return rule.isActivated() && rule.matches(evt);
            } catch (Exception e) {
                throw new ErrorWhileEvaluationRuleException(rule, e);
            }
        }, getFeature());
        return isMatching != null && isMatching;
    }

    /**
     * Finds and processes the highest priority rule (if it exists) matching the provided event
     * In case of a blocking rule, the processing stops after the first blocking rule has been processed
     * In case of error, the error is handled by the ErrorHandler2, which may throw a RollbackException
     * @param evt event to process
     * @return true if a matching rule has been found and processed, false otherwise
     */
    @Override
    public boolean processAllMatchingRule(PropertyChangeEvent evt) {
        List<IRule<PropertyChangeEvent, PropertyChangeEvent>> matchingRules = getAllMatchingRules(evt);
        if(matchingRules.isEmpty())
            return false;

        listenerManager.deactivateAllListeners();
        matchingRules.forEach(rule -> {
            try {
                rule.process(evt);
            } catch (OMFDevException e) {
                ErrorHandler.getInstance().handleException(e, getFeature()); //Could throw a RollbackException
            } catch (RuntimeException e) {
                ErrorHandler.getInstance().handleException(e, getFeature());//Throw a RollbackException
            }
        });

        OMFAutomationManager.getInstance().automationTriggered();
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

    @Override
    public void addRule(IRule<PropertyChangeEvent, PropertyChangeEvent> rule) {
        rule.setRuleEngine(this);
        this.rules.add(rule);
    }

    @Override
    public void addAllRules(List<IRule<PropertyChangeEvent, PropertyChangeEvent>> lRules){
        lRules.forEach(this::addRule);
    }
    @Override
    public void removeRule(IRule<PropertyChangeEvent, PropertyChangeEvent> rule){
        this.rules.remove(rule);
    }
    @Override
    public void removeRules(List<IRule<PropertyChangeEvent, PropertyChangeEvent>> lRules){
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

    public List<IRule<PropertyChangeEvent, PropertyChangeEvent>> getRules() {
        return rules;
    }
    public void setRules(List<IRule<PropertyChangeEvent,PropertyChangeEvent>> rules) {
        this.rules = rules;
    }

    public void setListenerManager(IListenerManager listenerManager) {
        this.listenerManager = listenerManager;
    }
    public IListenerManager getListenerManager() {
        return listenerManager;
    }
}
