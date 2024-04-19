package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;

import java.util.Set;

public class OMFCriticalException2 extends OMFDevException {
    /**
     * Exception without a cause... and a simple message.
     */
    public OMFCriticalException2(String message, OMFExceptionModifier2... modifiers) {
        this(new OMFLog2().text(message), modifiers);
    }

    /**
     * Just an exception without a cause...
     */
    public OMFCriticalException2(OMFLog2 message, OMFExceptionModifier2... modifiers) {
        this(message, null, modifiers);
    }

    /**
     * Simple message
     */
    public OMFCriticalException2(String message, Throwable cause, OMFExceptionModifier2... modifiers) {
        this(new OMFLog2().text(message), cause, modifiers);
    }

    /**
     * Full constructor wrapping causing exception
     */
    public OMFCriticalException2(OMFLog2 message, Throwable cause, OMFExceptionModifier2... modifiers) {
        super(message.toString(), cause);
        OMFLog = message;
        this.modifiers = Set.of(modifiers);
    }


}
