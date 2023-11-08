package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.notification.Notification;
import com.nomagic.magicdraw.ui.notification.NotificationManager;
import com.nomagic.magicdraw.ui.notification.NotificationSeverity;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel2;
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
            throw new CoreException2("The OMFLogger has not been initialized yet. Please call the init() method first.");
        }
        return instance;
    }

    public static void init(APlugin plugin) {
        if (instance != null) {
            throw new CoreException2("Can't initialize the OMFLogger has it has already been initialized.");
        }
        instance = new OMFLogger2(plugin);
    }

    public static void logToConsole(OMFLog2 logMessage, OMFLogLevel2 logLevel) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            String formattedLog = logMessage.toHTMLFormat(logLevel, getInstance().plugin.getName());
            Application.getInstance().getGUILog().addHyperlinkedText(formattedLog, logMessage.getLinkActionMapping());
        }
    }

    public static void logToConsole(String message, OMFLogLevel2 logLevel) {
        logToConsole(new OMFLog2().text(message), logLevel);
    }

    public static void logToConsole(OMFLog2 logMessage, OMFLogLevel2 logLevel, MDFeature feature) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            String formattedLog = logMessage.toHTMLFormat(logLevel, getInstance().plugin.getName(), feature.getName());
            Application.getInstance().getGUILog().addHyperlinkedText(formattedLog, logMessage.getLinkActionMapping());
        }
    }

    public static void logToConsole(String message, OMFLogLevel2 logLevel, MDFeature feature) {
        logToConsole(new OMFLog2().text(message), logLevel, feature);
    }

    public static void logToNotification(OMFLog2 logMessage, OMFLogLevel2 logLevel, MDFeature feature) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            NotificationManager.getInstance().showNotification(new Notification(
                    "[Plugin Error]", //id (not sure what is does)
                    OMFLog2.getPrefix(logLevel, getInstance().plugin.getName(), feature.getName()), //title
                    logMessage.toString(),
                    getNotificationSeverity(logLevel))
            );
        }
    }

    public static void logToNotification(OMFLog2 logMessage, OMFLogLevel2 logLevel) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            NotificationManager.getInstance().showNotification(new Notification(
                    "[Plugin Error]", //id (not sure what is does)
                    OMFLog2.getPrefix(logLevel, getInstance().plugin.getName()), //title
                    logMessage.toString(),
                    getNotificationSeverity(logLevel))
            );
        }
    }

    public static void logToNotification(String message, OMFLogLevel2 logLevel, MDFeature feature) {
            logToNotification(new OMFLog2().text(message), logLevel, feature);
    }

    public static void logToNotification(String message, OMFLogLevel2 logLevel) {
        logToNotification(new OMFLog2().text(message), logLevel);
    }

    private static NotificationSeverity getNotificationSeverity(OMFLogLevel2 logLevel) {
        switch (logLevel) {
            case WARNING:
                return NotificationSeverity.WARNING;
            case ERROR:
                return NotificationSeverity.ERROR;
            case INFO:
            default:
                return NotificationSeverity.INFO;
        }
    }
}
