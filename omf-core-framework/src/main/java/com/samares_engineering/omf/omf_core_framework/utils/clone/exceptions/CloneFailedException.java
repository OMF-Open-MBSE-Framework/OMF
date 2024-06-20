package com.samares_engineering.omf.omf_core_framework.utils.clone.exceptions;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;

public class CloneFailedException extends LegacyOMFException {
    public CloneFailedException(String errorMsg, ECriticality criticality) {
        super(errorMsg, criticality);
    }

    public CloneFailedException(String errorMsg, Exception exception, ECriticality criticality) {
        super(errorMsg, exception, criticality);
    }

    public CloneFailedException(String debugMessage, String userMessage, ECriticality criticality) {
        super(debugMessage, userMessage, criticality);
    }

    public CloneFailedException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super(debugMessage, userMessage, exception, criticality);
    }
}
