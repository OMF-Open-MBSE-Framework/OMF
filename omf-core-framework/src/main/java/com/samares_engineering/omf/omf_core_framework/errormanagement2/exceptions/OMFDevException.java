package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;

/**
 * Extends this exception for your plugin development.
 */
public abstract class OMFDevException extends BaseOMFException{
    protected OMFDevException(String message) {
        super(message);
    }

    protected OMFDevException(OMFLog2 message) {
        super(message);
    }

    protected OMFDevException(String message, Throwable cause) {
        super(message, cause);
    }

    protected OMFDevException(OMFLog2 message, Throwable cause) {
        super(message, cause);
    }
}
