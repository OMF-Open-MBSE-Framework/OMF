package com.samares_engineering.omf.omf_core_framework.errors.exceptions;

public class OMFFeatureNotFoundException extends OMFException{
    public OMFFeatureNotFoundException(String errorMsg, ECriticality criticality) {
        super(errorMsg, criticality);
    }

    public OMFFeatureNotFoundException(String errorMsg, Exception exception, ECriticality criticality) {
        super(errorMsg, exception, criticality);
    }

    public OMFFeatureNotFoundException(String debugMessage, String userMessage, ECriticality criticality) {
        super(debugMessage, userMessage, criticality);
    }

    public OMFFeatureNotFoundException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super(debugMessage, userMessage, exception, criticality);
    }
}
