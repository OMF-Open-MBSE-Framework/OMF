package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

/**
 * Used for critical exceptions inside the framework.
 */
public class CoreException2 extends OMFLogException {
    public CoreException2() {
        super();
    }

    public CoreException2(String message) {
        super(message);
    }

    public CoreException2(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreException2(Throwable cause) {
        super(cause);
    }
}
