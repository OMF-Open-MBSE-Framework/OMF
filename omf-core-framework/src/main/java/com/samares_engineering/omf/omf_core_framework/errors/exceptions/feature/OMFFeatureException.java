package com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;

/**
 * Basic exception for exceptions thrown in omf user features
 */
public class OMFFeatureException extends OMFException {
    private final MDFeature feature;

    public OMFFeatureException(String errorMsg, MDFeature feature, ECriticality criticality) {
        this(errorMsg, feature, null, criticality);
    }

    public OMFFeatureException(String errorMsg, MDFeature feature, Exception exception, ECriticality criticality) {
        this(errorMsg, errorMsg, feature, exception, criticality);
    }
    public OMFFeatureException(String debugMessage, String userMessage, MDFeature feature, Exception exception, ECriticality criticality) {
        super(debugMessage, generateErrorMessageWithPrefix(userMessage, feature), exception, criticality);
        this.feature = feature;
    }

    private static String generateErrorMessageWithPrefix(String errorMsg, MDFeature feature) {
        return "[" + feature.getPlugin().getName() + "::" + feature.getName() + "] " + errorMsg;
    }

    public MDFeature getFeature() {
        return feature;
    }
}
