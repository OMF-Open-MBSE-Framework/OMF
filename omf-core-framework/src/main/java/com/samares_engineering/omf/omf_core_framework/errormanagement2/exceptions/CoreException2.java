package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

/**
 * Used for critical exceptions inside the framework.
 */
public class CoreException2 extends BaseOMFException {
    public CoreException2(String message) {
        super(message);
    }

    public CoreException2(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message) {
        super(message);
    }

    public CoreException2(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreException2(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, Throwable cause) {
        super(message, cause);
    }
}
