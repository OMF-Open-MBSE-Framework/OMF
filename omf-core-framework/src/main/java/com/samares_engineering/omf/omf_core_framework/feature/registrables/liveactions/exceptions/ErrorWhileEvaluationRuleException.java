package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.LiveAction;

import java.beans.PropertyChangeEvent;

public class ErrorWhileEvaluationRuleException extends OMFCriticalException {
    public ErrorWhileEvaluationRuleException(LiveAction<PropertyChangeEvent, PropertyChangeEvent> rule, Exception exception) {
        super("Error while evaluating rule: " + rule.getId() + " for feature " + rule.getRuleEngine().getFeature().getName(), exception);
    }
}
