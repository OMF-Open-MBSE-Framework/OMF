package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;

import java.util.Arrays;
import java.util.HashSet;

public class OMFWarningException extends OMFDevException {

    /**
     * Exception without a cause... and a simple message.
     */
    public OMFWarningException(String message, OMFExceptionModifier... modifiers) {
        this(new OMFLog().warn(message), modifiers);
    }

    /**
     * Just an exception without a cause...
     */
    public OMFWarningException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, OMFExceptionModifier... modifiers) {
        this(message, null, modifiers);
    }

    /**
     * Simple message
     */
    public OMFWarningException(String message, Throwable cause, OMFExceptionModifier... modifiers) {
        this(new OMFLog().warn(message), cause, modifiers);
    }

    /**
     * Full constructor wrapping causing exception
     */
    public OMFWarningException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, Throwable cause, OMFExceptionModifier... modifiers) {
        super(message.toString(), cause);
        this.modifiers = new HashSet<>(Arrays.asList(modifiers));
        this.modifiers.add(OMFExceptionModifier.WARNING);
    }

}
