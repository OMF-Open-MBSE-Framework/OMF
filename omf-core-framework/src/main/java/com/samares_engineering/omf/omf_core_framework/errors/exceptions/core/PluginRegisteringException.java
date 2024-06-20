package com.samares_engineering.omf.omf_core_framework.errors.exceptions.core;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;

public class PluginRegisteringException extends OMFLogException {
    public PluginRegisteringException(String message) {
        super(message);
    }

    public PluginRegisteringException(OMFLog message) {
        super(message);
    }

    public PluginRegisteringException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginRegisteringException(OMFLog message, Throwable cause) {
        super(message, cause);
    }
}
