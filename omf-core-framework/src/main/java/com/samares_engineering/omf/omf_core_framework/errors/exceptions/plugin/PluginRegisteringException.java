package com.samares_engineering.omf.omf_core_framework.errors.exceptions.plugin;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.BaseOMFException;

public class PluginRegisteringException extends BaseOMFException {
    public PluginRegisteringException(String message) {
        super(message);
    }

    public PluginRegisteringException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message) {
        super(message);
    }

    public PluginRegisteringException(String message, Throwable cause) {
        super(message, cause);
    }

    public PluginRegisteringException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, Throwable cause) {
        super(message, cause);
    }
}
