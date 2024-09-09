package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.LiveAction;

public class ErrorWhileEvaluationLiveActionException extends OMFCriticalException {
    public ErrorWhileEvaluationLiveActionException(LiveAction<?,?> liveAction, Exception exception) {
        super("Error while evaluating liveAction: " + liveAction.getId() + " for feature " + liveAction.getLiveActionEngine().getFeature().getName(), exception);
    }
}
