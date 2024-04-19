package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;

public abstract class BaseOMFException extends RuntimeException {
    protected OMFLog2 OMFLog;

    /**
     * Exception without a cause... and a simple message.
     */
    protected BaseOMFException(String message) {
        this(new OMFLog2().text(message));
    }

    /**
     * Just an exception without a cause...
     */
    protected BaseOMFException(OMFLog2 message) {
        this(message, null);
    }

    /**
     * Simple message
     */
    protected BaseOMFException(String message, Throwable cause) {
        this(new OMFLog2().text(message), cause);
    }

    /**
     * Full constructor wrapping causing exception
     */
    protected BaseOMFException(OMFLog2 message, Throwable cause) {
        super(message.toString(), cause);
        OMFLog = message;
    }
}
