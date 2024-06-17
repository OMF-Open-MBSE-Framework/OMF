package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

/**
 * Extends this exception for your plugin development.
 */
public abstract class OMFDevException extends BaseOMFException{
    protected OMFDevException(String message) {
        super(message);
    }

    protected OMFDevException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message) {
        super(message);
    }

    protected OMFDevException(String message, Throwable cause) {
        super(message, cause);
    }

    protected OMFDevException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, Throwable cause) {
        super(message, cause);
    }
}
