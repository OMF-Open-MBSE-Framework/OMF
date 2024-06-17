package com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule.IRule;

import java.beans.PropertyChangeEvent;

public class ErrorWhileEvaluationRuleException extends OMFCriticalException {
    public ErrorWhileEvaluationRuleException(IRule<PropertyChangeEvent, PropertyChangeEvent> rule, Exception exception) {
        super("Error while evaluating rule: " + rule.getId() + " for feature " + rule.getRuleEngine().getFeature().getName(), exception);
    }
}
