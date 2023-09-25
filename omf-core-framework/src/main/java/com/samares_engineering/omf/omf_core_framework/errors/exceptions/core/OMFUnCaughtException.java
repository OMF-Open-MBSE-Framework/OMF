package com.samares_engineering.omf.omf_core_framework.errors.exceptions.core;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;

public class OMFUnCaughtException extends OMFException {
    static final String DEFAULT_MESSAGE = "An uncaught error occurred during the plugin execution.\n";
    public OMFUnCaughtException(Exception uncaughtException) {
        this(DEFAULT_MESSAGE + uncaughtException.getMessage(), uncaughtException);
    }
    public OMFUnCaughtException(String message, Exception uncaughtException) {
        super(message, uncaughtException, ECriticality.CRITICAL);
    }
}
