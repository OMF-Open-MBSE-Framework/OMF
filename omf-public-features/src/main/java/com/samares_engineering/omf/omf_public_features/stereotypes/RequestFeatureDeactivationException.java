package com.samares_engineering.omf.omf_public_features.stereotypes;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFExceptionModifier2;

public class RequestFeatureDeactivationException extends OMFCriticalException2 {
    public RequestFeatureDeactivationException(String message) {
        this(message, null);
    }

    public RequestFeatureDeactivationException(String message, Throwable cause) {
        super(message, cause, OMFExceptionModifier2.DEACTIVATE_FEATURE);
    }

    public RequestFeatureDeactivationException(String message, Throwable cause, OMFExceptionModifier2... modifiers) {
        super(message, cause, modifiers);
    }
}
