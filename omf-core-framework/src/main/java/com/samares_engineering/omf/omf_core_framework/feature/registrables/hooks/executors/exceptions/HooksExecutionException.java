package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFExceptionModifier2;

public class HooksExecutionException extends OMFCriticalException2 {
    public HooksExecutionException(String message, OMFExceptionModifier2... modifiers) {
        super(message, modifiers);
    }

    public HooksExecutionException(String message, Throwable cause, OMFExceptionModifier2... modifiers) {
        super(message, cause, modifiers);
    }
}
