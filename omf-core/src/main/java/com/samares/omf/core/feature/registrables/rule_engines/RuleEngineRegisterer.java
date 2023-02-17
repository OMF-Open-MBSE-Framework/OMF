/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature.registrables.rule_engines;

import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.exceptions.DevelopmentException;
import com.samares.omf.core.errors.exceptions.GenericException;
import com.samares.omf.core.feature.FeatureItemRegisterer;
import com.samares.omf.core.feature.FeatureRegisterer;
import com.samares.omf.core.feature.errors.FeatureException;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IFeatureRuleEngine;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.RECategoryEnum;
import com.samares.omf.core.listeners.IElementListener;
import com.samares.omf.core.listeners.IListenerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RuleEngineRegisterer extends FeatureItemRegisterer<IFeatureRuleEngine> {
    /**
     * Use the IListenerManager to get the different listeners (Analyse, Creation, Update, Delete, AfterAutomation).
     */
    private final IListenerManager listenerManager;

    public RuleEngineRegisterer(FeatureRegisterer featureRegisterer) {
        super(featureRegisterer);
        this.listenerManager = featureRegisterer.getPlugin().getListenerManager();
    }

    /**
     * Will allow
     * @param ruleEngines
     */
    public void registerFeatureItems(List<IFeatureRuleEngine> ruleEngines) throws FeatureException {
        try {
            ruleEngines.forEach(this::registerFeatureItem);
        }catch (Exception e){
            throw new FeatureException(
                    "[Feature Registerer] Unable to register LiveActions",
                    e, GenericException.ECriticality.CRITICAL);
        }
    }

    public void unregisterFeatureItems(List<IFeatureRuleEngine> ruleEngines) throws FeatureException {
        try {
            ruleEngines.forEach(this::unregisterFeatureItem);
        }catch (Exception e){
            throw new FeatureException(
                    "[Feature Registerer] Unable to unregister liveActions",
                    e, GenericException.ECriticality.CRITICAL);
        }
    }

    /**
     * Allow RuleEngine registration in the listener. Depending on the Category the RuleEngine will be triggered and Rules will be evaluated.
     * -category: based on RuleEngineUsage it will be used to register the RuleEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param ruleEngine: The RuleEngine to register
     */
    @Override
    protected void registerFeatureItem(IFeatureRuleEngine ruleEngine) {
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
    protected void unregisterFeatureItem(IFeatureRuleEngine ruleEngine) {
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
    private void addRuleEngine(IFeatureRuleEngine ruleEngine, int featurePriority){
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
    private void moveRuleEngine(IFeatureRuleEngine ruleEngine, int featurePriority){
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
}
