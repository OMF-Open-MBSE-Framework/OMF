package com.samares.omf.core.feature.ruleengine;

import com.samares.omf.core.feature.IFeatureRegisterer;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.feature.errors.FeatureException;
import com.samares.omf.core.listeners.IElementListener;
import com.samares.omf.core.listeners.IListenerManager;
import com.samares.omf.core.listeners.ruleEngineListener.ruleEngine.IRuleEngine;
import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.exceptions.DevelopmentException;
import com.samares.omf.core.errors.exceptions.GenericException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RuleEngineRegisterer implements IFeatureRegisterer {

    private IListenerManager listenerManager;


    /**
     * Use the IListenerManager to get the different listeners (Analyse, Creation, Update, Delete, AfterAutomation).
     * @param listenerManager:
     */
    public RuleEngineRegisterer(IListenerManager listenerManager) {
        this.listenerManager = listenerManager;
    }

    /**
     * Will allow
     * @param mdFeature
     */
    @Override
    public void registerFeature(MDFeature mdFeature) {
        try {
            mdFeature.getLiveActions().forEach(this::addRuleEngine);
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException(
                    "[Feature Registerer] Unable to register LiveActions for feature: " + mdFeature.getName(),
                    e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    @Override
    public void unregisterFeature(MDFeature mdFeature) {
        try {
            mdFeature.getLiveActions().forEach(this::removeRuleEngine);
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException(
                    "[Feature Registerer] Unable to unregister liveActions for feature: " + mdFeature.getName(),
                    e, GenericException.ECriticality.CRITICAL), false);
        }
    }




    /**
     * Allow RuleEngine registration in the listener. Depending on the Category the RuleEngine will be triggered and Rules will be evaluated.
     * -category: based on RuleEngineUsage it will be used to register the RuleEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param ruleEngine: The RuleEngine to register
     */
    public void addRuleEngine(IFeatureRuleEngine ruleEngine){
        String category = ruleEngine.getCategory();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<IRuleEngine>> m_evt_ruleEngine = listener.getRuleEngineMap();

        m_evt_ruleEngine.computeIfAbsent(category, l_re ->  new ArrayList<>()); //If category absent -> create a new ArrayList

        m_evt_ruleEngine.get(category).add(ruleEngine);

    }

    /**
     * Allow RuleEngine registration in the listener with a specific Priority. Depending on the Category the RuleEngine will be triggered and Rules will be evaluated.
     * -category: based on RuleEngineUsage it will be used to register the RuleEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param ruleEngine: The RuleEngine to register
     * @param featurePriority: will help to order the RuleEngine execution by its priority.
     */
    public void addRuleEngine(IFeatureRuleEngine ruleEngine, int featurePriority){
        String category = ruleEngine.getCategory();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<IRuleEngine>> m_evt_ruleEngine = listener.getRuleEngineMap();

        m_evt_ruleEngine.computeIfAbsent(category, l_re ->  new ArrayList<>()); //If category absent -> create a new ArrayList

        m_evt_ruleEngine.get(category).add(featurePriority, ruleEngine);
    }

    /**
     * Remove a specific RuleEngine if registered.
     * -category: based on RuleEngineUsage it will be used to register the RuleEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param ruleEngine: The RuleEngine to remove
     */
    public void removeRuleEngine(IFeatureRuleEngine ruleEngine){
            String category = ruleEngine.getCategory();
            IElementListener listener = getListenerFromCategory(category);
            HashMap<String, List<IRuleEngine>> m_evt_ruleEngine = listener.getRuleEngineMap();
            if (m_evt_ruleEngine.containsKey(category))
                m_evt_ruleEngine.get(category).remove(ruleEngine);
    }

    //TODO: Rethink priority management: does the priority is guaranteed ? Priority shall be linked to the RE/Feature
    /**
     * Move RuleEngine registration in the listener with to specific, RE will be removed, then add again in the list decreasing the priority of all the other features.
     * - category: based on RuleEngineUsage it will be used to register the RuleEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param ruleEngine: The RuleEngine to register
     * @param featurePriority: The new pr.
     */
    public void moveRuleEngine(IFeatureRuleEngine ruleEngine, int featurePriority){
        removeRuleEngine(ruleEngine);
        addRuleEngine(ruleEngine, featurePriority);
    }

    /**
     * Will return the listener instance
     * @param category
     * @return
     */
    public IElementListener getListenerFromCategory(String category) {
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
