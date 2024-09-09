/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly;

import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.DevelopmentException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType;
import com.samares_engineering.omf.omf_core_framework.listeners.IElementListener;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LiveActionEngineFeatureItemRegisterer implements FeatureItemRegisterer<LiveActionEngine> {
    /**
     * Use the IListenerManager to get the different listeners (Analyse, Creation, Update, Delete, AfterAutomation).
     */
    private IListenerManager listenerManager;
    private FeatureRegisterer featureRegisterer;
    List<LiveActionEngine> registeredFeatureItems = new ArrayList<>();

    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
        this.listenerManager = featureRegisterer.getPlugin().getListenerManager();
    }

    /**
     * Will allow to register a list of LiveActionEngine in the listener.
     * @param liveActionEngines List of LiveActionEngine to register
     */
    public void registerFeatureItems(List<LiveActionEngine> liveActionEngines) {
        try {
            liveActionEngines.forEach(this::registerFeatureItem);
        }catch (Exception e){
            throw new FeatureRegisteringException("Unable to register LiveActions", e);
        }
    }

    public void unregisterFeatureItems(List<LiveActionEngine> liveActionEngines){
        try {
            liveActionEngines.forEach(this::unregisterFeatureItem);
        }catch (Exception e){
            throw new FeatureRegisteringException(" Unable to unregister liveActions", e);
        }
    }

    /**
     * Allow LiveActionEnginet registration in the listener. Depending on the Category the LiveActionEngine will be triggered and LiveActions will be evaluated.
     * -category: based on LiveActionEngineUsage it will be used to register the LiveActionEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param liveActionEngine: The LiveActionEngine to register
     */
    @Override
    public void registerFeatureItem(LiveActionEngine liveActionEngine) {
        String category = liveActionEngine.getType();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<LiveActionEngine>> liveActionEngineMap = listener.getLiveActionEngineMap();

        liveActionEngineMap.computeIfAbsent(category, LiveActionEngines ->  new ArrayList<>()); //If category absent -> create a new ArrayList

        liveActionEngineMap.get(category).add(liveActionEngine);
        registeredFeatureItems.add(liveActionEngine);
    }

    /**
     * Remove a specific LiveActionEngine if registered.
     * -category: based on LiveActionEngineUsage it will be used to register the LiveActionEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param LiveActionEngine: The LiveActionEngine to remove
     */
    @Override
    public void unregisterFeatureItem(LiveActionEngine LiveActionEngine) {
        String category = LiveActionEngine.getType();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<LiveActionEngine>> LiveActionEngineMap = listener.getLiveActionEngineMap();
        if (LiveActionEngineMap.containsKey(category))
            LiveActionEngineMap.get(category).remove(LiveActionEngine);
        registeredFeatureItems.remove(LiveActionEngine);
    }
    
    /**
     * Allow LiveActionEngine registration in the listener with a specific Priority. Depending on the Category the LiveActionEngine will be triggered and LiveActions will be evaluated.
     * -category: based on LiveActionEngineUsage it will be used to register the LiveActionEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param LiveActionEngine: The LiveActionEngine to register
     * @param featurePriority: will help to order the LiveActionEngine execution by its priority.
     */
    private void addLiveActionEngine(LiveActionEngine LiveActionEngine, int featurePriority){
        String category = LiveActionEngine.getType();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<LiveActionEngine>> LiveActionEngineMap = listener.getLiveActionEngineMap();

        LiveActionEngineMap.computeIfAbsent(category, LiveActionEngines ->  new ArrayList<>()); //If category absent -> create a new ArrayList

        LiveActionEngineMap.get(category).add(featurePriority, LiveActionEngine);
    }

    //TODO: Rethink priority management: does the priority is guaranteed ? Priority shall be linked to the RE/Feature
    /**
     * Move LiveActionEngine registration in the listener with to specific, RE will be removed, then add again in the list decreasing the priority of all the other features.
     * - category: based on LiveActionEngineUsage it will be used to register the LiveActionEngine in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param LiveActionEngine: The LiveActionEngine to register
     * @param featurePriority: The new pr.
     */
    private void moveLiveActionEngine(LiveActionEngine LiveActionEngine, int featurePriority){
        unregisterFeatureItem(LiveActionEngine);
        addLiveActionEngine(LiveActionEngine, featurePriority);
    }

    /**
     * Will return the listener instance
     * @param category
     * @return
     */
    private IElementListener getListenerFromCategory(String category) {
        LiveActionType liveActionType = LiveActionType.valueOf(category);

        switch (liveActionType){
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
                LegacyErrorHandler.handleException(new DevelopmentException("No Listener found for this category"));
                return null;
        }
    }

    @Override
    public void registerFeatureItems(OMFFeature feature) {
        registerFeatureItems(feature.getLiveActionEngines());
    }

    @Override
    public void unregisterFeatureItems(OMFFeature feature) {
        unregisterFeatureItems(feature.getLiveActionEngines());
    }

    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegisterer;
    }

    @Override
    public void setFeatureRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }
    @Override
    public List<LiveActionEngine> getRegisteredFeatureItems() {
        return registeredFeatureItems;
    }
}
