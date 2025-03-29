/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction;

import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.events.CharacterizedEvent;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;

/**
 * Abstract class for a liveAction, contains the basic methods for a LiveAction.
 */
public abstract class ALiveActionCharacterizedEvent implements LiveAction<CharacterizedEvent, CharacterizedEvent> {
    protected LiveActionEngine<CharacterizedEvent> liveActionEngine;
    public String id = "";
    public boolean isActivated = true;

    public ALiveActionCharacterizedEvent(){id = getClass().getSimpleName();}

    public ALiveActionCharacterizedEvent(String id){
        this.id = id;
    }

    /**
     * Check if the event matches the liveAction, and if the liveAction is activated.
     * This method is executed within a barrier that will catch any exception and rethrow it as an ErrorWhileEvaluationLiveActionException.
     * @param evt the event to match
     * @return true if the event matches the liveAction, false otherwise
     */
    @Override
    public final boolean matches(CharacterizedEvent evt) {
        return isActivated && eventMatches(evt);
    }

    /**
     * Implement the check for the liveAction here.
     * This method checks if the event matches the liveAction, thus if the liveAction should be executed.
     * This method is executed within a barrier that will catch any exception and rethrow it as an ErrorWhileEvaluationLiveActionException.
     * @param evt the event to check
     * @return true if the event matches the liveAction, false otherwise
     */
    protected abstract boolean eventMatches(CharacterizedEvent evt);

    @Deprecated
    public void debug(Object o){}

    @Override
    public boolean isActivated() {
        return isActivated;
    }

    @Override
    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setLiveActionEngine(LiveActionEngine<CharacterizedEvent> liveActionEngine) {
        this.liveActionEngine = liveActionEngine;
    }


    @Override
    public LiveActionEngine<CharacterizedEvent> getLiveActionEngine() {
        return liveActionEngine;
    }

    public OMFFeature getFeature() {
        return liveActionEngine.getFeature();
    }
}
