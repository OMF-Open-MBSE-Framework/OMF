package com.samares_engineering.omf.omf_core_framework.errors.exceptions.core;

public class UnCaughtException extends LegacyOMFException {
    static final String DEFAULT_MESSAGE = "An uncaught error occurred during the plugin execution.\n";
    public UnCaughtException(Exception uncaughtException) {
        this(DEFAULT_MESSAGE + uncaughtException.getMessage(), uncaughtException);
    }
    public UnCaughtException(String message, Exception uncaughtException) {
        super(message, uncaughtException, ECriticality.CRITICAL);
    }
}
