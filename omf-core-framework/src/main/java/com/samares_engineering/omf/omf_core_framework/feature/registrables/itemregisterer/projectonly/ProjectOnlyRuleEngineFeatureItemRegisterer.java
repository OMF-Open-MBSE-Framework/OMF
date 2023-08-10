/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly;

import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.DevelopmentException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares_engineering.omf.omf_core_framework.listeners.IElementListener;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to register RuleEngines (LiveActions) in the ListenerManager.
 * It will be used to register the RuleEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
 * see {@link IListenerManager}
 * see {@link IElementListener}
 * see {@link IRuleEngine}
 */
public class ProjectOnlyRuleEngineFeatureItemRegisterer implements ProjectOnlyFeatureItemRegisterer<IRuleEngine> {
    /**
     * Use the IListenerManager to get the different listeners (Analyse, Creation, Update, Delete, AfterAutomation).
     */
    private  IListenerManager listenerManager;
    private FeatureRegisterer featureRegisterer;

    public void init(FeatureRegisterer featureRegisterer) {
        setFeatureRegisterer(featureRegisterer);
        this.listenerManager = featureRegisterer.getPlugin().getListenerManager();
    }

    /**
     * Register all RuleEngines in the ListenerManager
     * see  {@link #registerFeatureItem(IRuleEngine)}
     * @param ruleEngines
     */
    public void registerFeatureItems(List<IRuleEngine> ruleEngines) {
        try {
            ruleEngines.forEach(this::registerFeatureItem);
        }catch (Exception e){
            throw new OMFFeatureRegisteringException("[Feature Registerer] Unable to register LiveActions", e);
        }
    }

    /**
     * Unregister all RuleEngines in the ListenerManager
     * see {@link #unregisterFeatureItem(IRuleEngine)}
     * @param ruleEngines
     */
    public void unregisterFeatureItems(List<IRuleEngine> ruleEngines) {
        try {
            ruleEngines.forEach(this::unregisterFeatureItem);
        }catch (Exception e){
            throw new OMFFeatureRegisteringException(
                    "[Feature Registerer] Unable to unregister liveActions",
                    e);
        }
    }

    /**
     * Allow RuleEngine registration in the listener. Depending on the Category the RuleEngine will be triggered and Rules will be evaluated.
     * -category: based on RuleEngineUsage it will be used to register the RuleEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param ruleEngine: The RuleEngine to register
     */
    @Override
    public void registerFeatureItem(IRuleEngine ruleEngine) {
        String category = ruleEngine.getCategory();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<IRuleEngine>> ruleEngineMap = listener.getRuleEngineMap();

        ruleEngineMap.computeIfAbsent(category, ruleEngines ->  new ArrayList<>()); //If category absent -> create a new ArrayList

        ruleEngineMap.get(category).add(ruleEngine);
    }

    /**
     * Remove a specific RuleEngine if registered.
     * -category: based on RuleEngineUsage it will be used to register the RuleEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param ruleEngine: The RuleEngine to remove
     */
    @Override
    public void unregisterFeatureItem(IRuleEngine ruleEngine) {
        String category = ruleEngine.getCategory();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<IRuleEngine>> ruleEngineMap = listener.getRuleEngineMap();
        if (ruleEngineMap.containsKey(category))
            ruleEngineMap.get(category).remove(ruleEngine);
    }



    /**
     * Allow RuleEngine registration in the listener with a specific Priority. Depending on the Category the RuleEngine will be triggered and Rules will be evaluated.
     * -category: based on RuleEngineUsage it will be used to register the RuleEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param ruleEngine: The RuleEngine to register
     * @param featurePriority: will help to order the RuleEngine execution by its priority.
     */
    private void addRuleEngine(IRuleEngine ruleEngine, int featurePriority){
        String category = ruleEngine.getCategory();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<IRuleEngine>> ruleEngineMap = listener.getRuleEngineMap();

        ruleEngineMap.computeIfAbsent(category, ruleEngines ->  new ArrayList<>()); //If category absent -> create a new ArrayList

        ruleEngineMap.get(category).add(featurePriority, ruleEngine);
    }


    //TODO: Rethink priority management: does the priority is guaranteed ? Priority shall be linked to the RE/Feature
    /**
     * Move RuleEngine registration in the listener with to specific, RE will be removed, then add again in the list decreasing the priority of all the other features.
     * - category: based on RuleEngineUsage it will be used to register the RuleEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param ruleEngine: The RuleEngine to register
     * @param featurePriority: The new pr.
     */
    private void moveRuleEngine(IRuleEngine ruleEngine, int featurePriority){
        unregisterFeatureItem(ruleEngine);
        addRuleEngine(ruleEngine, featurePriority);
    }

    /**
     * Will return the listener instance
     * @param category
     * @return
     */
    private IElementListener getListenerFromCategory(String category) {
        switch (RECategoryEnum.valueOf(category)){
            case ANALYSE:
                return listenerManager.getAnalysisListener();
            case CREATE:
                return listenerManager.getCreationListener();
            case UPDATE:
                return listenerManager.getUpdateListener();
            case DELETE:
                return listenerManager.getDeletionListener();
            case AFTER_AUTOMATION:
                return listenerManager.getAfterAutomationListener();
            default:
                OMFErrorHandler.handleException(new DevelopmentException("No Listener found for this category"));
                return null;
        }
    }

    @Override
    public void registerFeatureItems(MDFeature feature) {
        registerFeatureItems(feature.getProjectOnlyRuleEngines());
    }

    @Override
    public void unregisterFeatureItems(MDFeature feature) {
        unregisterFeatureItems(feature.getProjectOnlyRuleEngines());
    }

    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegisterer;
    }

    @Override
    public void setFeatureRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }
}
