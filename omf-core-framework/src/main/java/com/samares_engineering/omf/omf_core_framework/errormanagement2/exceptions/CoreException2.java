package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;

/**
 * Used for critical exceptions inside of the framework.
 */
public class CoreException2 extends BaseOMFException {
    public CoreException2(String message) {
        super(message);
    }

    public CoreException2(OMFLog2 message) {
        super(message);
    }

    public CoreException2(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreException2(OMFLog2 message, Throwable cause) {
        super(message, cause);
    }
}
