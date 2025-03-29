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
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.events.CharacterizedEvent;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.exceptions.ErrorWhileEvaluationLiveActionException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.LiveAction;
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LiveActionEngineCharacterized implements LiveActionEngine<CharacterizedEvent> {
    private IListenerManager listenerManager = null;
    private List<LiveAction<CharacterizedEvent, CharacterizedEvent>> liveActions = new ArrayList<>();
    private String id = "";
    private int priority = -1;
    private String category = "";
    private OMFFeature feature = null;
    private boolean activated = true;

    public LiveActionEngineCharacterized(LiveActionType category) {
        this(category, -1);
    }

    public LiveActionEngineCharacterized(LiveActionType category, int priority) {
        this.category = category.toString();
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
    public Optional<LiveAction<CharacterizedEvent, CharacterizedEvent>> getMatchingLiveAction(CharacterizedEvent characterizedEvent) {
        if (skipLiveActions(characterizedEvent)) {
            return Optional.empty();
        }
        return liveActions.stream()
            .filter(liveAction -> isLiveActionMatching(characterizedEvent, liveAction))
        .findFirst();
    }

    @Override
    public List<LiveAction<CharacterizedEvent, CharacterizedEvent>> getAllMatchingLiveActions(CharacterizedEvent characterizedEvent) {
        if (skipLiveActions(characterizedEvent)) return new ArrayList<>();

        List<LiveAction<CharacterizedEvent, CharacterizedEvent>> liveActionsToExecute = new ArrayList<>();

        for (LiveAction<CharacterizedEvent, CharacterizedEvent> liveAction : liveActions) {
        if (isLiveActionMatching(characterizedEvent, liveAction)) {
            liveActionsToExecute.add(liveAction);
            if (liveAction.isBlocking()) break;
        }
    }
        return liveActionsToExecute;
    }

    private boolean isLiveActionMatching(CharacterizedEvent characterizedEvent, LiveAction<CharacterizedEvent, CharacterizedEvent> liveAction) {
        Boolean isMatching = OMFBarrierExecutor.executeWithinBarrier(() -> {
        try {
            return liveAction.isActivated() && liveAction.matches(characterizedEvent);
        } catch (Exception e) {
            throw new ErrorWhileEvaluationLiveActionException(liveAction, e);
        }
    }, getFeature());
        return isMatching != null && isMatching;
    }

    @Override
    public boolean processAllMatchingLiveActions(CharacterizedEvent characterizedEvent) {
        List<LiveAction<CharacterizedEvent, CharacterizedEvent>> matchingLiveActions = getAllMatchingLiveActions(characterizedEvent);
        if (matchingLiveActions.isEmpty()) return false;

        matchingLiveActions.forEach(liveAction -> OMFBarrierExecutor.executeInSessionWithinBarrier(
        () -> liveAction.process(characterizedEvent), getFeature(), !liveAction.keepListenerActivated()
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
    public boolean skipLiveActions(CharacterizedEvent characterizedEvent) {
        return false;
    }

    @Override
    public void addLiveAction(LiveAction<CharacterizedEvent, CharacterizedEvent> liveAction) {
        liveAction.setLiveActionEngine(this);
        liveActions.add(liveAction);
    }

    @Override
    public void addAllLiveActions(List<LiveAction<CharacterizedEvent, CharacterizedEvent>> liveActions) {
        liveActions.forEach(this::addLiveAction);
    }

    @Override
    public void removeLiveAction(LiveAction<CharacterizedEvent, CharacterizedEvent> liveAction) {
        liveActions.remove(liveAction);
    }

    @Override
    public void removeLiveActions(List<LiveAction<CharacterizedEvent, CharacterizedEvent>> liveActions) {
        this.liveActions.removeAll(liveActions);
    }

    @Override
    public void removeAllLiveActions() {
        liveActions.clear();
    }

    @Override
    public List<LiveAction<CharacterizedEvent, CharacterizedEvent>> getLiveActions() {
        return liveActions;
    }

    public void setLiveActions(List<LiveAction<CharacterizedEvent, CharacterizedEvent>> liveActions) {
        this.liveActions = liveActions;
    }
}
