package com.samares_engineering.omf.omf_core_framework.errors.exceptions;

public class OMFFrameworkException extends OMFException{
    public OMFFrameworkException(String errorMsg, ECriticality criticality) {
        super("[OMFFramework]" + errorMsg, criticality);
    }

    public OMFFrameworkException(String errorMsg, Exception exception, ECriticality criticality) {
        super("[OMFFramework]" + errorMsg, exception, criticality);
    }

    public OMFFrameworkException(String debugMessage, String userMessage, ECriticality criticality) {
        super("[OMFFramework]" + debugMessage, userMessage, criticality);
    }

    public OMFFrameworkException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super("[OMFFramework]" + debugMessage, userMessage, exception, criticality);
    }
}
