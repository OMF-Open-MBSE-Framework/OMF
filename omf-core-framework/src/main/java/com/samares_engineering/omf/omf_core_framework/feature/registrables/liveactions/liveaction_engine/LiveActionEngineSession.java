/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.events.SessionHistory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.exceptions.ErrorWhileEvaluationLiveActionException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.LiveAction;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiveActionEngineSession implements LiveActionEngine<SessionHistory> {
    private IListenerManager listenerManager = null;
    private List<LiveAction<SessionHistory, SessionHistory>> liveActions = new ArrayList<>();
    private String id = "";
    private int priority = -1;
    private String category = "HISTORY";
    private OMFFeature feature = null;
    private boolean activated = true;

    public LiveActionEngineSession() {
        this("HISTORY", -1);
    }

    public LiveActionEngineSession(String history){
        this(history, -1);
    }

    public LiveActionEngineSession(String history, int priority) {
        this.category = history;
        this.priority = priority;
    }

    @Override
    public void initRegistrableItem(OMFFeature feature) {
        this.feature = feature;
        listenerManager = feature.getPlugin().getListenerManager();
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

    @Override
    public Optional<LiveAction<SessionHistory, SessionHistory>> getMatchingLiveAction(SessionHistory sessionHistory) {
        if (skipLiveActions(sessionHistory)) {
            return Optional.empty();
        }
        return liveActions.stream()
            .filter(liveAction -> isLiveActionMatching(sessionHistory, liveAction))
        .findFirst();
    }

    @Override
    public List<LiveAction<SessionHistory, SessionHistory>> getAllMatchingLiveActions(SessionHistory sessionHistory) {
        if (skipLiveActions(sessionHistory)) return new ArrayList<>();

        List<LiveAction<SessionHistory, SessionHistory>> liveActionsToExecute = new ArrayList<>();

        for (LiveAction<SessionHistory, SessionHistory> liveAction : liveActions) {
        if (isLiveActionMatching(sessionHistory, liveAction)) {
            liveActionsToExecute.add(liveAction);
            if (liveAction.isBlocking()) break;
        }
    }
        return liveActionsToExecute;
    }

    private boolean isLiveActionMatching(SessionHistory sessionHistory, LiveAction<SessionHistory, SessionHistory> liveAction) {
        Boolean isMatching = OMFBarrierExecutor.executeWithinBarrier(() -> {
        try {
            return liveAction.isActivated() && liveAction.matches(sessionHistory);
        } catch (Exception e) {
            throw new ErrorWhileEvaluationLiveActionException(liveAction, e);
        }
    }, getFeature());
        return isMatching != null && isMatching;
    }

    @Override
    public boolean processAllMatchingLiveActions(SessionHistory sessionHistory) {
        List<LiveAction<SessionHistory, SessionHistory>> matchingLiveActions = getAllMatchingLiveActions(sessionHistory);
        if (matchingLiveActions.isEmpty()) return false;

        matchingLiveActions.forEach(liveAction -> OMFBarrierExecutor.executeInSessionWithinBarrier(
        () -> liveAction.process(sessionHistory), getFeature(), !liveAction.keepListenerActivated()
        ));

        OMFAutomationManager.getInstance().automationTriggered();
        return true;
    }

    /*
    Accessors
     */
    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String getType() {
        return category;
    }

    @Override
    public void setType(String category) {
        this.category = category;
    }

    @Override
    public OMFFeature getFeature() {
        return feature;
    }

    @Override
    public boolean skipLiveActions(SessionHistory sessionHistory) {
        return false;
    }

    @Override
    public void addLiveAction(LiveAction<SessionHistory, SessionHistory> liveAction) {
        liveAction.setLiveActionEngine(this);
        liveActions.add(liveAction);
    }

    @Override
    public void addAllLiveActions(List<LiveAction<SessionHistory, SessionHistory>> liveActions) {
        liveActions.forEach(this::addLiveAction);
    }

    @Override
    public void removeLiveAction(LiveAction<SessionHistory, SessionHistory> liveAction) {
        liveActions.remove(liveAction);
    }

    @Override
    public void removeLiveActions(List<LiveAction<SessionHistory, SessionHistory>> liveActions) {
        this.liveActions.removeAll(liveActions);
    }

    @Override
    public void removeAllLiveActions() {
        liveActions.clear();
    }

    @Override
    public List<LiveAction<SessionHistory, SessionHistory>> getLiveActions() {
        return liveActions;
    }

    public void setLiveActions(List<LiveAction<SessionHistory, SessionHistory>> liveActions) {
        this.liveActions = liveActions;
    }
}
