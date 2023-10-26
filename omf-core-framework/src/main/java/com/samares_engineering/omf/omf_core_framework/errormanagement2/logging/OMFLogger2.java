package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging;

import com.nomagic.magicdraw.core.Application;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel2;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

public class OMFLogger2 {
    private static OMFLogger2 instance;
    private APlugin plugin;
    private OMFLogLevel2 logLevel = OMFLogLevel2.INFO;

    private OMFLogger2(APlugin plugin) {
        this.plugin = plugin;
    }

    public static OMFLogger2 getInstance() {
        if (instance.plugin == null) {
            throw new RuntimeException("The OMFLogger has not been initialized yet. Please call the init() method first.");
        }
        return instance;
    }

    public static void init(APlugin plugin) {
        if (instance != null) {
            throw new RuntimeException("Can't initialize the OMFLogger has it has already been initialized.");
        }
        instance = new OMFLogger2(plugin);
    }

    public static void logToConsole(OMFLog logMessage, OMFLogLevel logLevel) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            String formattedLog = logMessage.toHTMLFormat(logLevel, getInstance().plugin.getName());
            Application.getInstance().getGUILog().addHyperlinkedText(formattedLog, logMessage.getLinkActionMapping());
        }
    }
    public static void logToConsole(String message, OMFLogLevel logLevel) {
        logToConsole(new OMFLog().text(message), logLevel);
    }

    public static void logToConsole(OMFLog logMessage, OMFLogLevel logLevel, MDFeature feature) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            String formattedLog = logMessage.toHTMLFormat(logLevel, getInstance().plugin.getName(), feature.getName());
            Application.getInstance().getGUILog().addHyperlinkedText(formattedLog, logMessage.getLinkActionMapping());
        }
    }

    public static void logToConsole(String message, OMFLogLevel logLevel, MDFeature feature) {
        logToConsole(new OMFLog().text(message), logLevel, feature);
    }
}
