package com.samares_engineering.omf.omf_public_features.stereotypes;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;

public class RequestFeatureDeactivationException extends OMFCriticalException {
    public RequestFeatureDeactivationException(String message) {
        this(message, null);
    }

    public RequestFeatureDeactivationException(String message, Throwable cause) {
        super(message, cause, OMFExceptionModifier.DEACTIVATE_FEATURE);
    }

    public RequestFeatureDeactivationException(String message, Throwable cause, OMFExceptionModifier... modifiers) {
        super(message, cause, modifiers);
    }
}
