package com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature;

import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;

public class CriticalFeatureException extends OMFFeatureException {
    public CriticalFeatureException(String errorMsg, MDFeature feature, ECriticality criticality) {
        super(errorMsg, feature, criticality);
    }

    public CriticalFeatureException(String errorMsg, MDFeature feature, Exception exception, ECriticality criticality) {
        super(errorMsg, feature, exception, criticality);
    }

    public CriticalFeatureException(String debugMessage, String userMessage, MDFeature feature, Exception exception, ECriticality criticality) {
        super(debugMessage, userMessage, feature, exception, criticality);
    }
}
