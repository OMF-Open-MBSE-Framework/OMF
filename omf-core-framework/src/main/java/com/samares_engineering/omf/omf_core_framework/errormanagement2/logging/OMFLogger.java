package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.notification.Notification;
import com.nomagic.magicdraw.ui.notification.NotificationManager;
import com.nomagic.magicdraw.ui.notification.NotificationSeverity;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;

public class OMFLogger {
    private static OMFLogger instance;
    private final OMFPlugin plugin;
    private final OMFLogLevel logLevel = OMFLogLevel.INFO;

    protected OMFLogger(OMFPlugin plugin) {
        this.plugin = plugin;
    }

    //Rational, to avoid typing OMFLogger2.getInstance() everytime, it's included in each static method
    private static OMFLogger getInstance() {
        if (instance.plugin == null) {
            throw new CoreException2("The OMFLogger has not been initialized yet. Please call the init() method first.");
        }
        return instance;
    }

    public static void init(OMFPlugin plugin) {
        if (instance != null) {
            throw new CoreException2("Can't initialize the OMFLogger has it has already been initialized.");
        }
        instance = new OMFLogger(plugin);
    }

    public static void logToUIConsole(OMFLog logMessage, OMFLogLevel logLevel) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) { //if the log level is higher than the current log level
            logMessage.replaceNewLinesWithBreaks();
            String formattedLog = logMessage.toHTMLFormat(logLevel, getInstance().plugin.getName());
            Application.getInstance().getGUILog().addHyperlinkedText(formattedLog, logMessage.getLinkActionMapping());
        }
    }

    public static void logToUIConsole(String message, OMFLogLevel logLevel) {
        logToUIConsole(new OMFLog().text(message), logLevel);
    }

    public static void logToUIConsole(OMFLog logMessage, OMFLogLevel logLevel, OMFFeature feature) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            String formattedLog = logMessage.toHTMLFormat(logLevel, getInstance().plugin.getName(), feature.getName());
            Application.getInstance().getGUILog().addHyperlinkedText(formattedLog, logMessage.getLinkActionMapping());
        }
    }

    public static void logToUIConsole(String message, OMFLogLevel logLevel, OMFFeature feature) {
        logToUIConsole(new OMFLog().text(message), logLevel, feature);
    }

    public static void logToNotificationWithExpandableText(OMFLog displayedMessage, OMFLog expandedMessage, OMFLogLevel logLevel, OMFFeature feature) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            Notification notification = new Notification(
                    "[Plugin Error]", //id (not sure what is does)
                    OMFLog.getPrefix(logLevel, getInstance().plugin.getName(), feature.getName()), //title
                    displayedMessage.replaceNewLinesWithBreaks().toString(),
                    getNotificationSeverity(logLevel)
            );
            notification.setLongText(expandedMessage.replaceNewLinesWithBreaks().toString());

            NotificationManager.getInstance().showNotification(notification);
        }
    }

    public static void logToNotification(OMFLog logMessage, OMFLogLevel logLevel, OMFFeature feature) {
        logToNotificationWithExpandableText(logMessage, new OMFLog().text(""), logLevel, feature);
    }

    public static void logToNotificationWithExpandableText(OMFLog displayedMessage, OMFLog expandedMessage, OMFLogLevel logLevel) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            Notification notification = new Notification(
                    "[Plugin Error]", //id (not sure what is does)
                    OMFLog.getPrefix(logLevel, getInstance().plugin.getName()), //title
                    displayedMessage.replaceNewLinesWithBreaks().toString(),
                    getNotificationSeverity(logLevel)
            );
            notification.setLongText(expandedMessage.replaceNewLinesWithBreaks().toString());

            NotificationManager.getInstance().showNotification(notification);
        }
    }

    public static void logToNotification(OMFLog logMessage, OMFLogLevel logLevel) {
        logToNotificationWithExpandableText(logMessage, new OMFLog().text(""), logLevel);
    }

    public static void logToNotification(String message, OMFLogLevel logLevel, OMFFeature feature) {
            logToNotification(new OMFLog().text(message).replaceNewLinesWithBreaks(), logLevel, feature);
    }

    public static void logToNotification(String message, OMFLogLevel logLevel) {
        logToNotification(new OMFLog().text(message), logLevel);
    }

    public static void logToSystemConsole(OMFLog logMessage, OMFLogLevel logLevel) {
        if (logLevel.ordinal() >= getInstance().logLevel.ordinal()) {
            switch (logLevel) {

                case WARNING:
                    SysoutColorPrinter.warn(logMessage.toString());
                    break;
                case ERROR:
                    SysoutColorPrinter.err(logMessage.toString());
                    break;
                case INFO:
                default:
                    SysoutColorPrinter.print(logMessage.toString());
                    break;
            }
        }
    }

    public static void logToSystemConsole(String message, OMFLogLevel logLevel) {
        logToSystemConsole(new OMFLog().text(message), logLevel);
    }

    public static void logToSystemConsole(OMFLog logMessage, OMFLogLevel logLevel, OMFFeature feature) {
        logToSystemConsole("[" + feature.getName() + "]" + logMessage.toString(), logLevel);
    }

    private static NotificationSeverity getNotificationSeverity(OMFLogLevel logLevel) {
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
        logToNotification(message, OMFLogLevel.WARNING);
    }

    public static void errorToNotification(String message) {
        logToNotification(message, OMFLogLevel.ERROR);
    }

    public static void infoToNotification(String message) {
        logToNotification(message, OMFLogLevel.INFO);
    }

    public static void warnToUIConsole(String message) {
        logToUIConsole(message, OMFLogLevel.WARNING);
    }

    public static void warnToNotification(OMFLog message) {
        logToNotification(message, OMFLogLevel.WARNING);
    }

    public static void errorToNotification(OMFLog message) {
        logToNotification(message, OMFLogLevel.ERROR);
    }

    public static void infoToNotification(OMFLog message) {
        logToNotification(message, OMFLogLevel.INFO);
    }

    public static void warnToUIConsole(OMFLog message) {
        logToUIConsole(message, OMFLogLevel.WARNING);
    }

    public static void errorToUIConsole(String message) {
        logToUIConsole(message, OMFLogLevel.ERROR);
    }

    public static void errorToUIConsole(OMFLog message) {
        logToUIConsole(message, OMFLogLevel.ERROR);
    }

    public static void infoToUIConsole(OMFLog message) {
        logToUIConsole(message, OMFLogLevel.INFO);
    }

    public static void infoToUIConsole(String message) {
        logToUIConsole(message, OMFLogLevel.INFO);
    }

    public static void warnToSystemConsole(String message) {
        logToSystemConsole(message, OMFLogLevel.WARNING);
    }

    public static void errorToSystemConsole(String message) {
        logToSystemConsole(message, OMFLogLevel.ERROR);
    }

    public static void infoToSystemConsole(String message) {
        logToSystemConsole(message, OMFLogLevel.INFO);
    }

    public static void warnToSystemConsole(OMFLog message) {
        logToSystemConsole(message, OMFLogLevel.WARNING);
    }

    public static void errorToSystemConsole(OMFLog message) {
        logToSystemConsole(message, OMFLogLevel.ERROR);
    }

    public static void infoToSystemConsole(OMFLog message) {
        logToSystemConsole(message, OMFLogLevel.INFO);
    }

    // The following methods are shorthand helpers to log to the default locations
    public static void warn(OMFLogException exception) {
        warn(exception.getLog(), exception);
    }

    public static void err(OMFLogException exception) {
        err(exception.getLog(), exception);
    }

    public static void warn(Exception exception) {
        warn(exception.getMessage(), exception);
    }

    public static void err(Exception exception) {
        err(exception.getMessage(), exception);
    }

    public static void warn(OMFLog message, Exception e) {
        e.printStackTrace();
        warn(message);
    }

    public static void err(OMFLog message, Exception e) {
        e.printStackTrace();
        err(message);
    }

    public static void warn(String message, Exception e) {
        e.printStackTrace();
        warn(message);
    }

    public static void err(String message, Exception e) {
        e.printStackTrace();
        err(message);
    }

    public static void warn(OMFLog message) {
        warnToNotification(message);
        warnToUIConsole(message);
        warnToSystemConsole(message);
    }

    public static void err(OMFLog message) {
        errorToNotification(message);
        errorToUIConsole(message);
        errorToSystemConsole(message);
    }

    public static void warn(String message) {
        warnToNotification(message);
        warnToUIConsole(message);
        warnToSystemConsole(message);
    }

    public static void err(String message) {
        errorToNotification(message);
        errorToUIConsole(message);
        errorToSystemConsole(message);
    }
}
