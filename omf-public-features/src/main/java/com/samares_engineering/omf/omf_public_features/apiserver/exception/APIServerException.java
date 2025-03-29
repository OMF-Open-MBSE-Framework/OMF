package com.samares_engineering.omf.omf_public_features.apiserver.exception;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;

public class APIServerException extends LegacyOMFException {

    public APIServerException(String errorMsg, ECriticality criticality) {
        super(errorMsg, criticality);
    }

    public APIServerException(String errorMsg, Exception exception, ECriticality criticality) {
        super(errorMsg, exception, criticality);
    }

    public APIServerException(String debugMessage, String userMessage, ECriticality criticality) {
        super(debugMessage, userMessage, criticality);
    }

    public APIServerException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super(debugMessage, userMessage, exception, criticality);
    }
}
