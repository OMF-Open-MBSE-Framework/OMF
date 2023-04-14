package com.samares_engineering.omf.omf_core_framework.errors.exceptions;

import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

public class OMFPluginRegisteringException extends OMFException{

    APlugin currentPlugin;

    public OMFPluginRegisteringException(String errorMsg, APlugin plugin, ECriticality criticality) {
        super(errorMsg, criticality);
        currentPlugin = plugin;
    }

    public OMFPluginRegisteringException(String errorMsg, Exception exception, APlugin plugin, ECriticality criticality) {
        super(errorMsg, exception, criticality);
        currentPlugin = plugin;
    }

    public OMFPluginRegisteringException(String debugMessage, String userMessage, APlugin plugin, ECriticality criticality) {
        super(debugMessage, userMessage, criticality);
        currentPlugin = plugin;
    }

    public OMFPluginRegisteringException(String debugMessage, String userMessage, Exception exception, APlugin plugin, ECriticality criticality) {
        super(debugMessage, userMessage, exception, criticality);
        currentPlugin = plugin;
    }

    private String getDefaultUserMessage(){
        String pluginName = currentPlugin == null? "?": currentPlugin.getDescriptor().getName();

        return "An Error Occurred on On Plugin registering: " + pluginName + " , contact your provider";
    }
}
