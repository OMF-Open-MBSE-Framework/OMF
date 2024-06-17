package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;

public class HooksExecutionException extends OMFCriticalException {
    public HooksExecutionException(String message, OMFExceptionModifier... modifiers) {
        super(message, modifiers);
    }

    public HooksExecutionException(String message, Throwable cause, OMFExceptionModifier... modifiers) {
        super(message, cause, modifiers);
    }
}
