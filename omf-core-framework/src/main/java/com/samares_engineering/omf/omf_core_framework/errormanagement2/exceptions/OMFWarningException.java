package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class OMFWarningException extends OMFDevException {


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
        this(new OMFLog2().warn(message), cause, modifiers);
    }



    /**
     * Full constructor wrapping causing exception
     */
    public OMFWarningException(OMFLog2 message, Throwable cause, OMFExceptionModifier2... modifiers) {
        super(message.toString(), cause);
        this.modifiers = new HashSet<>(Arrays.asList(modifiers));
        this.modifiers.add(OMFExceptionModifier2.WARNING);
    }

}
