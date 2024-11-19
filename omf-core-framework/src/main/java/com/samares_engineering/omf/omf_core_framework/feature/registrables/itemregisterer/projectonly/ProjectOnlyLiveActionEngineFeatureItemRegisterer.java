/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly;

import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.DevelopmentException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.ProjectOnlyFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType;
import com.samares_engineering.omf.omf_core_framework.listeners.IElementListener;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is used to register LiveActions (LiveActions) in the ListenerManager.
 * It will be used to register the LiveAction in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
 * see {@link IListenerManager}
 * see {@link IElementListener}
 * see {@link ALiveActionEngine}
 */
public class ProjectOnlyLiveActionEngineFeatureItemRegisterer implements ProjectOnlyFeatureItemRegisterer<LiveActionEngine> {
    /**
     * Use the IListenerManager to get the different listeners (Analyse, Creation, Update, Delete, AfterAutomation).
     */
    private  IListenerManager listenerManager;
    private FeatureRegisterer featureRegisterer;
    List<LiveActionEngine> registeredFeatureItems = new ArrayList<>();

    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        setFeatureRegisterer(featureRegisterer);
        this.listenerManager = featureRegisterer.getPlugin().getListenerManager();
    }

    /**
     * Register all LiveActions in the ListenerManager
     * @param LiveActions the LiveActions to register
     */
    public void registerFeatureItems(List<LiveActionEngine> LiveActions) {
        try {
            LiveActions.forEach(this::registerFeatureItem);
        }catch (Exception e){
            throw new FeatureRegisteringException("[Feature Registerer] Unable to register LiveActions", e);
        }
    }

    /**
     * Unregister all LiveActions in the ListenerManager
     * @param LiveActions the LiveActions to unregister
     */
    public void unregisterFeatureItems(List<LiveActionEngine> LiveActions) {
        try {
            LiveActions.forEach(this::unregisterFeatureItem);
        }catch (Exception e){
            throw new FeatureRegisteringException(
                    "[Feature Registerer] Unable to unregister liveActions",
                    e);
        }
    }

    /**
     * Allow LiveAction registration in the listener. Depending on the Category the LiveAction will be triggered and LiveActions will be evaluated.
     * -category: based on LiveActionUsage it will be used to register the LiveAction in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param liveAction: The LiveAction to register
     */
    @Override
    public void registerFeatureItem(LiveActionEngine liveAction) {
        String category = liveAction.getType();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<LiveActionEngine>> liveActionMap = listener.getLiveActionEngineMap();

        liveActionMap.computeIfAbsent(category, LiveActions ->  new ArrayList<>()); //If category absent -> create a new ArrayList

        liveActionMap.get(category).add(liveAction);
        registeredFeatureItems.add(liveAction);
    }

    /**
     * Remove a specific LiveAction if registered.
     * -category: based on LiveActionUsage it will be used to register the LiveAction in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param liveAction: The LiveAction to remove
     */
    @Override
    public void unregisterFeatureItem(LiveActionEngine liveAction) {
        String category = liveAction.getType();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<LiveActionEngine>> liveActionMap = listener.getLiveActionEngineMap();
        if (liveActionMap.containsKey(category))
            liveActionMap.get(category).remove(liveAction);
        registeredFeatureItems.remove(liveAction);
    }



    /**
     * Allow LiveAction registration in the listener with a specific Priority. Depending on the Category the LiveAction will be triggered and LiveActions will be evaluated.
     * -category: based on LiveActionUsage it will be used to register the LiveAction in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param liveAction: The LiveAction to register
     * @param featurePriority: will help to order the LiveAction execution by its priority.
     */
    private void addLiveAction(LiveActionEngine liveAction, int featurePriority){
        String category = liveAction.getType();
        IElementListener listener = getListenerFromCategory(category);
        HashMap<String, List<LiveActionEngine>> liveActionMap = listener.getLiveActionEngineMap();

        liveActionMap.computeIfAbsent(category, LiveActions ->  new ArrayList<>()); //If category absent -> create a new ArrayList

        liveActionMap.get(category).add(featurePriority, liveAction);
    }


    //TODO: Rethink priority management: does the priority is guaranteed ? Priority shall be linked to the RE/Feature
    /**
     * Move LiveAction registration in the listener with to specific, RE will be removed, then add again in the list decreasing the priority of all the other features.
     * - category: based on LiveActionUsage it will be used to register the LiveAction in the right place by default (Analyse, Create, Update, Delete, AfterAutomation).
     * @param liveAction: The LiveAction to register
     * @param featurePriority: The new pr.
     */
    private void moveLiveAction(LiveActionEngine liveAction, int featurePriority){
        unregisterFeatureItem(liveAction);
        addLiveAction(liveAction, featurePriority);
    }

    /**
     * Will return the listener instance
     * @param category the category of the LiveAction
     * @return the listener instance
     */
    private IElementListener getListenerFromCategory(String category) {
        switch (LiveActionType.valueOf(category)){
            case ANALYSE:
                return listenerManager.getAnalysisListener();
            case CREATE:
                return listenerManager.getCreationListener();
            case UPDATE:
                return listenerManager.getUpdateListener();
            case DELETE:
                return listenerManager.getDeletionListener();
            case HISTORY:
                return listenerManager.getHistoryListener();
            case AFTER_AUTOMATION:
                return listenerManager.getAfterAutomationListener();
            case ANALYSE_UNDO_REDO:
                return listenerManager.getUndoRedoAnalysisListener();
            case CREATE_UNDO_REDO:
                return listenerManager.getUndoRedoCreationListener();
            case UPDATE_UNDO_REDO:
                return listenerManager.getUndoRedoUpdateListener();
            case DELETE_UNDO_REDO:
                return listenerManager.getUndoRedoDeletionListener();
            case HISTORY_UNDO_REDO:
                return listenerManager.getUndoRedoHistoryListener();

            default:
                LegacyErrorHandler.handleException(new DevelopmentException("No Listener found for this category"));
                return null;
        }
    }

    @Override
    public void registerFeatureItems(OMFFeature feature) {
        registerFeatureItems(feature.getProjectOnlyLiveActionEngines());
    }

    @Override
    public void unregisterFeatureItems(OMFFeature feature) {
        unregisterFeatureItems(feature.getProjectOnlyLiveActionEngines());
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
