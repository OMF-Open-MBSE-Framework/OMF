package com.samares_engineering.omf.omf_core_framework.errors.exceptions.core;

/**
 * This exception is thrown when the registration of a feature is compromised, and signals to the framework to
 * cancel the registration of the feature.
 */
public class FeatureRegisteringException extends RuntimeException {
    public FeatureRegisteringException(String message) {
        super("[Feature Registerer] " + message);
    }

    public FeatureRegisteringException(String message, Throwable cause) {
        super("[Feature Registerer] " + message, cause);
    }
}
