package com.samares_engineering.omf.omf_core_framework.errors.exceptions.plugin;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.BaseOMFException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

public class OMFPluginRegisteringException extends BaseOMFException {
    public OMFPluginRegisteringException(String message) {
        super(message);
    }

    public OMFPluginRegisteringException(OMFLog2 message) {
        super(message);
    }

    public OMFPluginRegisteringException(String message, Throwable cause) {
        super(message, cause);
    }

    public OMFPluginRegisteringException(OMFLog2 message, Throwable cause) {
        super(message, cause);
    }
}
