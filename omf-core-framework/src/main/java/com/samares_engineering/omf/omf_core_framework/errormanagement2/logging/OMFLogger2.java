package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.notification.Notification;
import com.nomagic.magicdraw.ui.notification.NotificationManager;
import com.nomagic.magicdraw.ui.notification.NotificationSeverity;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFWarningException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel2;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;
import com.samares_engineering.omf.omf_core_framework.utils.ColorPrinter;

public class OMFLogger2 {
    private static OMFLogger2 instance;
    private APlugin plugin;
    private OMFLogLevel2 logLevel = OMFLogLevel2.INFO;

    private OMFLogger2(APlugin plugin) {
        this.plugin = plugin;
    }

    //Rational, to avoid typing OMFLLogger2.getInstance() everytime, it's included in each static method
    private static OMFLogger2 getInstance() {
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

    public static void logToUIConsole(OMFLog2 logMessage, OMFLogLevel2 logLevel) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) { //if the log level is higher than the current log level
            logMessage.replaceNewLinesWithBreaks();
            String formattedLog = logMessage.toHTMLFormat(logLevel, getInstance().plugin.getName());
            Application.getInstance().getGUILog().addHyperlinkedText(formattedLog, logMessage.getLinkActionMapping());
        }
    }

    public static void logToUIConsole(String message, OMFLogLevel2 logLevel) {
        logToUIConsole(new OMFLog2().text(message), logLevel);
    }

    public static void logToUIConsole(OMFLog2 logMessage, OMFLogLevel2 logLevel, MDFeature feature) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            String formattedLog = logMessage.toHTMLFormat(logLevel, getInstance().plugin.getName(), feature.getName());
            Application.getInstance().getGUILog().addHyperlinkedText(formattedLog, logMessage.getLinkActionMapping());
        }
    }

    public static void logToUIConsole(String message, OMFLogLevel2 logLevel, MDFeature feature) {
        logToUIConsole(new OMFLog2().text(message), logLevel, feature);
    }

    public static void logToNotification(OMFLog2 logMessage, OMFLogLevel2 logLevel, MDFeature feature) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            NotificationManager.getInstance().showNotification(new Notification(
                    "[Plugin Error]", //id (not sure what is does)
                    OMFLog2.getPrefix(logLevel, getInstance().plugin.getName(), feature.getName()), //title
                    logMessage.replaceNewLinesWithBreaks().toString(),
                    getNotificationSeverity(logLevel))
            );
        }
    }

    public static void logToNotification(OMFLog2 logMessage, OMFLogLevel2 logLevel) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            NotificationManager.getInstance().showNotification(new Notification(
                    "[Plugin Error]", //id (not sure what is does)
                    OMFLog2.getPrefix(logLevel, getInstance().plugin.getName()), //title
                    logMessage.replaceNewLinesWithBreaks().toString(),
                    getNotificationSeverity(logLevel))
            );
        }
    }

    public static void logToNotification(String message, OMFLogLevel2 logLevel, MDFeature feature) {
            logToNotification(new OMFLog2().text(message).replaceNewLinesWithBreaks(), logLevel, feature);
    }

    public static void logToNotification(String message, OMFLogLevel2 logLevel) {
        logToNotification(message, logLevel, null);
    }

    public static void logToSystemConsole(OMFLog2 logMessage, OMFLogLevel2 logLevel) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            switch (logLevel) {

                case WARNING:
                    ColorPrinter.warn(logMessage.toString());
                    break;
                case ERROR:
                    ColorPrinter.err(logMessage.toString());
                    break;
                case INFO:
                default:
                    ColorPrinter.print(logMessage.toString());
                    break;
            }
        }
    }

    public static void logToSystemConsole(String message, OMFLogLevel2 logLevel) {
        logToSystemConsole(new OMFLog2().text(message), logLevel);
    }

    public static void logToSystemConsole(OMFLog2 logMessage, OMFLogLevel2 logLevel, MDFeature feature) {
        logToSystemConsole("[" + feature.getName() + "]" + logMessage.toString(), logLevel);
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

    public static void warnToNotification(String message) {
        logToNotification(message, OMFLogLevel2.WARNING);
    }

    public static void errorToNotification(String message) {
        logToNotification(message, OMFLogLevel2.ERROR);
    }

    public static void infoToNotification(String message) {
        logToNotification(message, OMFLogLevel2.INFO);
    }

    public static void warnToUIConsole(String message) {
        logToUIConsole(message, OMFLogLevel2.WARNING);
    }

    public static void warnToNotification(OMFLog2 message) {
        logToNotification(message, OMFLogLevel2.WARNING);
    }

    public static void errorToNotification(OMFLog2 message) {
        logToNotification(message, OMFLogLevel2.ERROR);
    }

    public static void infoToNotification(OMFLog2 message) {
        logToNotification(message, OMFLogLevel2.INFO);
    }

    public static void warnToUIConsole(OMFLog2 message) {
        logToUIConsole(message, OMFLogLevel2.WARNING);
    }

    public static void errorToUIConsole(String message) {
        logToUIConsole(message, OMFLogLevel2.ERROR);
    }

    public static void errorToUIConsole(OMFLog2 message) {
        logToUIConsole(message, OMFLogLevel2.ERROR);
    }

    public static void infoToUIConsole(OMFLog2 message) {
        logToUIConsole(message, OMFLogLevel2.INFO);
    }

    public static void infoToUIConsole(String message) {
        logToUIConsole(message, OMFLogLevel2.INFO);
    }

    public static void warnToSystemConsole(String message) {
        logToSystemConsole(message, OMFLogLevel2.WARNING);
    }

    public static void errorToSystemConsole(String message) {
        logToSystemConsole(message, OMFLogLevel2.ERROR);
    }

    public static void infoToSystemConsole(String message) {
        logToSystemConsole(message, OMFLogLevel2.INFO);
    }

    public static void warnToSystemConsole(OMFLog2 message) {
        logToSystemConsole(message, OMFLogLevel2.WARNING);
    }

    public static void errorToSystemConsole(OMFLog2 message) {
        logToSystemConsole(message, OMFLogLevel2.ERROR);
    }

    public static void infoToSystemConsole(OMFLog2 message) {
        logToSystemConsole(message, OMFLogLevel2.INFO);
    }


    public static void defaultWarningException(OMFWarningException warningException) {
        warnToNotification(warningException.getUiMessage());
        warnToUIConsole(warningException.getUiMessage());
        warnToSystemConsole(warningException.getUiMessage());
    }
}
