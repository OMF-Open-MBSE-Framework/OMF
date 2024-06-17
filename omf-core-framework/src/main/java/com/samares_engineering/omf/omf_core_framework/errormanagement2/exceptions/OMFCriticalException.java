package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;

import java.util.Set;

public class OMFCriticalException extends OMFDevException {
    /**
     * Exception without a cause... and a simple message.
     */
    public OMFCriticalException(String message, OMFExceptionModifier... modifiers) {
        this(new OMFLog().text(message), modifiers);
    }

    /**
     * Just an exception without a cause...
     */
    public OMFCriticalException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, OMFExceptionModifier... modifiers) {
        this(message, null, modifiers);
    }

    /**
     * Simple message
     */
    public OMFCriticalException(String message, Exception cause, OMFExceptionModifier... modifiers) {
        this(new OMFLog().text(message), cause, modifiers);
    }
    /**
     * Simple message
     */
    public OMFCriticalException(String message, Throwable cause, OMFExceptionModifier... modifiers) {
        this(new OMFLog().text(message), cause, modifiers);
    }

    /**
     * Full constructor wrapping causing exception
     */
    public OMFCriticalException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, Throwable cause, OMFExceptionModifier... modifiers) {
        super(message.toString(), cause);
        OMFLog = message;
        this.modifiers = Set.of(modifiers);
    }

}
