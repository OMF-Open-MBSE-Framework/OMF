package com.samares_engineering.omf.omf_core_framework.errors.exceptions;

import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;

/**
 * Basic exception for exceptions thrown in omf user features
 */
public class OMFFeatureException extends OMFException {
    public OMFFeatureException(String errorMsg, MDFeature feature, ECriticality criticality) {
        super(generateErrorMessageWithPrefix(errorMsg, feature), criticality);
    }

    public OMFFeatureException(String errorMsg, MDFeature feature, Exception exception, ECriticality criticality) {
        super(generateErrorMessageWithPrefix(errorMsg, feature), exception, criticality);
    }
    public OMFFeatureException(String debugMessage, String userMessage, MDFeature feature, Exception exception, ECriticality criticality) {
        super(debugMessage, generateErrorMessageWithPrefix(userMessage, feature), exception, criticality);
    }

    private static String generateErrorMessageWithPrefix(String errorMsg, MDFeature feature) {
        return "[" + feature.getPlugin().getName() + "::" + feature.getName() + "] " + errorMsg;
    }


}
