/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.exceptions.ErrorWhileEvaluationLiveActionException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.LiveAction;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ALiveActionEngine implements LiveActionEngine<PropertyChangeEvent> {
    private IListenerManager listenerManager;
    private List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> liveActions = new ArrayList<>();
    private String id = "";
    private int priority = -1;
    private String category = "";
    private OMFFeature feature;
    private boolean activated = true;


    public ALiveActionEngine(LiveActionType category) {
        this(category, -1);
    }

    public ALiveActionEngine(LiveActionType category, int priority) {
        this(category.toString(), priority);
    }

    public ALiveActionEngine(String category) {
        this(category, -1);
    }

    public ALiveActionEngine(String category, int priority) {
        this.category = category;
        this.priority = priority;
    }

    @Override
    public void initRegistrableItem(OMFFeature feature) {
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
     * Find the highest priority liveAction (if it exists) matching the provided event
     *
     * @param PropertyChangeEvent event to process
     * @return the liveAction found
     */
    @Override
    public Optional<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> getMatchingLiveAction(PropertyChangeEvent PropertyChangeEvent) {
        if (skipLiveActions(PropertyChangeEvent)) {
            return Optional.empty();
        }
        return liveActions.stream()
                .filter(liveAction -> isLiveActionMatching(PropertyChangeEvent, liveAction))
                .findFirst();
    }

    @Override
    public List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> getAllMatchingLiveActions(PropertyChangeEvent PropertyChangeEvent) {
        if (skipLiveActions(PropertyChangeEvent))
            return new ArrayList<>();

        List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> liveActionsToExecute = new ArrayList<>();

        for (LiveAction<PropertyChangeEvent, PropertyChangeEvent> liveAction : liveActions) {  //return all matching liveActions until the first Blocking liveAction is found
            if (isLiveActionMatching(PropertyChangeEvent, liveAction)) {
                liveActionsToExecute.add(liveAction);
                OMFLogger.statusToSystemConsole("Triggered live action: " + liveAction.getClass().getSimpleName() + " for event: " + PropertyChangeEvent.getPropertyName()
                        + " on element: " + ((Element) PropertyChangeEvent.getSource()).getHumanName());
                if (liveAction.isBlocking()) {
                    OMFLogger.statusToSystemConsole("Live action is blocking: stopping liveAction matching for this event");
                    break;
                }
            }
        }
        return liveActionsToExecute;

    }

    private boolean isLiveActionMatching(PropertyChangeEvent PropertyChangeEvent, LiveAction<PropertyChangeEvent, PropertyChangeEvent> liveAction) {
        Boolean isMatching = OMFBarrierExecutor.executeWithinBarrier(() -> {
            try {
                return liveAction.isActivated() && liveAction.matches(PropertyChangeEvent);
            } catch (Exception e) {
                throw new ErrorWhileEvaluationLiveActionException(liveAction, e);
            }
        }, getFeature());
        return isMatching != null && isMatching;
    }

    /**
     * Finds and processes the highest priority liveAction (if it exists) matching the provided event
     * In case of a blocking liveAction, the processing stops after the first blocking liveAction has been processed
     * In case of error, the error is handled by the ErrorHandler2, which may throw a RollbackException
     *
     * @param PropertyChangeEvent event to process
     * @return true if a matching liveAction has been found and processed, false otherwise
     */
    @Override
    public boolean processAllMatchingLiveActions(PropertyChangeEvent PropertyChangeEvent) {
        List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> matchingLiveActions = getAllMatchingLiveActions(PropertyChangeEvent);
        if (matchingLiveActions.isEmpty())
            return false;

//        listenerManager.deactivateAllListeners();
        matchingLiveActions.forEach(liveAction -> {
            OMFBarrierExecutor.executeInSessionWithinBarrier(() -> liveAction.process(PropertyChangeEvent), getFeature(), !liveAction.keepListenerActivated());
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

    public String getType() {
        return category;
    }

    public void setType(String category) {
        this.category = category;
    }

    public OMFFeature getFeature() {
        return feature;
    }

    public boolean skipLiveActions(PropertyChangeEvent PropertyChangeEvent) {
        return false;
    }

    @Override
    public void addLiveAction(LiveAction<PropertyChangeEvent, PropertyChangeEvent> liveAction) {
        liveAction.setLiveActionEngine(this);
        this.liveActions.add(liveAction);
    }

    @Override
    public void addAllLiveActions(List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> liveActions) {
        liveActions.forEach(this::addLiveAction);
    }

    @Override
    public void removeLiveAction(LiveAction<PropertyChangeEvent, PropertyChangeEvent> liveAction) {
        this.liveActions.remove(liveAction);
    }

    @Override
    public void removeLiveActions(List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> liveActions) {
        this.liveActions.removeAll(liveActions);
    }

    @Override
    public void removeAllLiveActions() {
        this.liveActions.clear();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> getLiveActions() {
        return liveActions;
    }

    public void setLiveActions(List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> liveActions) {
        this.liveActions = liveActions;
    }

    public void setListenerManager(IListenerManager listenerManager) {
        this.listenerManager = listenerManager;
    }

    public IListenerManager getListenerManager() {
        return listenerManager;
    }
}
