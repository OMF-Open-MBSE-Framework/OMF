/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction;

import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;

import java.beans.PropertyChangeEvent;

/**
 * Abstract class for a rule, contains the basic methods for a LiveAction rule.
 */
public abstract class ALiveAction implements LiveAction<PropertyChangeEvent, PropertyChangeEvent> {
    protected LiveActionEngine ruleEngine;
    public String id = "";
    public boolean isActivated = true;

    public ALiveAction(){id = getClass().getSimpleName();}

    public ALiveAction(String id){
        this.id = id;
    }

    /**
     * Check if the event matches the rule, and if the rule is activated.
     * This method is executed within a barrier that will catch any exception and rethrow it as an ErrorWhileEvaluationRuleException.
     * @param evt the event to match
     * @return true if the event matches the rule, false otherwise
     */
    @Override
    public final boolean matches(PropertyChangeEvent evt) {
        return isActivated && eventMatches(evt);
    }

    /**
     * Implement the check for the rule here.
     * This method checks if the event matches the rule, thus if the rule should be executed.
     * This method is executed within a barrier that will catch any exception and rethrow it as an ErrorWhileEvaluationRuleException.
     * @param evt the event to check
     * @return true if the event matches the rule, false otherwise
     */
    protected abstract boolean eventMatches(PropertyChangeEvent evt);

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
    public void setRuleEngine(LiveActionEngine ruleEngine) {
        this.ruleEngine = ruleEngine;
    }


    @Override
    public LiveActionEngine getRuleEngine() {
        return ruleEngine;
    }

    public OMFFeature getFeature() {
        return ruleEngine.getFeature();
    }
}
