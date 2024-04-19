package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFColors2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;

import java.util.Set;

public class OMFWarningException extends BaseOMFException {
    private Set<OMFExceptionModifier2> modifiers;


    /**
     * Exception without a cause... and a simple message.
     */
    public OMFWarningException(String message, OMFExceptionModifier2... modifiers) {
        this(new OMFLog2().warn(message), modifiers);
    }

    /**
     * Just an exception without a cause...
     */
    public OMFWarningException(OMFLog2 message, OMFExceptionModifier2... modifiers) {
        this(message, null, modifiers);
    }

    /**
     * Simple message
     */
    public OMFWarningException(String message, Throwable cause, OMFExceptionModifier2... modifiers) {
        this(new OMFLog2().text(message), cause, modifiers);
    }

    /**
     * Full constructor wrapping causing exception
     */
    public OMFWarningException(OMFLog2 message, Throwable cause, OMFExceptionModifier2... modifiers) {
        super(message.toString(), cause);
        OMFLog = message;
        this.modifiers = Set.of(modifiers);
    }

}
