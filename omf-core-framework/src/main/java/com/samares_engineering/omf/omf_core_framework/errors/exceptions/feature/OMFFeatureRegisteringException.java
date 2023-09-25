package com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature;

/**
 * This exception is thrown when the registration of a feature is compromised, and signals to the framework to
 * cancel the registration of the feature.
 */
public class OMFFeatureRegisteringException extends RuntimeException {
    public OMFFeatureRegisteringException(String message) {
        super("[Feature Registerer] " + message);
    }

    public OMFFeatureRegisteringException(String message, Throwable cause) {
        super("[Feature Registerer] " + message, cause);
    }
}
