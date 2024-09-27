package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.utils.ElementAction;

import java.util.*;
import java.util.stream.Collectors;

public class OMFLog {
    private final List<String> messageComponents = new ArrayList<>();
    private final Map<String, Runnable> linkActionMapping = new HashMap<>();
    private OMFLog expandedLog;

    public OMFLog text(String string) {
        messageComponents.add(string == null ? "" : string);
        return this;
    }

    public OMFLog text(String string, OMFLogLevel logLevel) {
        switch (logLevel) {
            case INFO:
                return info(string);
            case WARNING:
                return warn(string);
            case ERROR:
                return err(string);
            default:
                return text(string);
        }
    }

    public OMFLog expandText(OMFLog expandedLog) {
        this.expandedLog = expandedLog;
        return this;
    }

    public OMFLog breakLine() {
        return text("<BR>");
    }

    public OMFLog bold(String string) {
        return text("<B>" + string + "</B>");
    }

    public OMFLog italic(String string) {
        return text("<I>" + string + "</I>");
    }

    public OMFLog underline(String string) {
        return text("<U>" + string + "</U>");
    }

    public OMFLog strike(String string) {
        return text("<S>" + string + "</S>");
    }

    public OMFLog color(String string, String color) {
        return text("<font color=" + color + ">" + string + "</font>");
    }

    public OMFLog warn(String string) {
        return color(string, OMFColors.WARN);
    }

    public OMFLog warn(OMFLog log) {
        return warn(log.toString());
    }

    public OMFLog info(String string) {
        return color(string, OMFColors.INFO);
    }

    public OMFLog info(OMFLog log) {
        return info(log.toString());
    }

    public OMFLog err(String string) {
        return color(string, OMFColors.ERROR);
    }

    public OMFLog err(OMFLog log) {
        return err(log.toString());
    }

    public OMFLog linkElement(String linkText, Element elementToLink) {
        linkActionMapping.put(linkText, new ElementAction(elementToLink)::selectInBrowser);
        return text("<A>" + linkText + "</A>");
    }

    public OMFLog linkElementAndParent(Element elementToLink) {
        String linkElementName = "";
        String linkOwnerElementName = "DELETED";
        if (elementToLink != null) {
            linkElementName = elementToLink.getHumanName();
            linkActionMapping.put(linkElementName, new ElementAction(elementToLink)::selectInBrowser);
            if (elementToLink.getOwner() != null) {
                linkOwnerElementName = elementToLink.getOwner().getHumanName();
                linkActionMapping.put(linkOwnerElementName, new ElementAction(elementToLink.getOwner())::selectInBrowser);
            }
        }
        return text("<A>" + linkElementName + "</A>::<A>" + linkOwnerElementName + "</A>");
    }

    public OMFLog link(String linkText, String url) {
        return text("<A href=" + url + ">" + linkText + "</A>");
    }

    public OMFLog linkAction(String linkText, Runnable action) {
        linkActionMapping.put(linkText, action);
        return text("<A>" + linkText + "</A>");
    }

    /*
     * Log message formatting
     */

    public String toHTMLFormat(OMFLogLevel logLevel) {
        return "<font color=" + getMessageColor(logLevel) + ">" + getPrefix(logLevel)
                + " " + toString(" ") + "</font>";
    }

    public String toHTMLFormat(OMFLogLevel logLevel, String pluginName) {
        String expandedLogString = expandedLog != null ? "<BR>" + expandedLog : "";

        return "<font color=" + getMessageColor(logLevel) + ">"
                + getPrefix(logLevel, pluginName)
                + " " + toString(" ")
                + expandedLogString
                + "</font>";
    }

    public String toHTMLFormat(OMFLogLevel logLevel, String pluginName, String featureName) {
        return "<font color=" + getMessageColor(logLevel) + ">" + getPrefix(logLevel, pluginName, featureName) + " "
                + toString(" ") + "</font>";
    }

    public static String getPrefix(OMFLogLevel logLevel) {
        return "[" + getLogLevelPrefix(logLevel) + "]";
    }

    public static String getPrefix(OMFLogLevel logLevel, String pluginName) {
        return getPrefix(logLevel) + "[" + pluginName + "]";
    }

    public static String getPrefix(OMFLogLevel logLevel, String pluginName, String featureName) {
        return getPrefix(logLevel, pluginName) + "[" + featureName + "]";
    }

    private static String getLogLevelPrefix(OMFLogLevel logLevel) {
        switch (logLevel) {
            case WARNING:
                return "Warning";
            case ERROR:
                return "Error";
            case INFO:
            default:
                return "Info";
        }
    }

    private static String getMessageColor(OMFLogLevel logLevel) {
        switch (logLevel) {
            case WARNING:
                return OMFColors.WARN;
            case ERROR:
                return OMFColors.ERROR;
            case INFO:
            default:
                return OMFColors.INFO;
        }
    }

    public String toString(String delimiter) {
        StringBuilder message = new StringBuilder();
        messageComponents.forEach(component -> message.append(component).append(delimiter));
        return message.toString();
    }

    @Override
    public String toString() {
        return toString(" ");
    }

    /*
     * Syntaxic sugar to reduced boilerplate of logging
     */

    public OMFLog logToUiConsole(OMFLogLevel logLevel) {
        OMFLogger.logToUIConsole(this, logLevel);
        return this;
    }

    public OMFLog logToUiConsole(OMFLogLevel logLevel, OMFFeature feature) {
        OMFLogger.logToUIConsole(this, logLevel, feature);
        return this;
    }

    public OMFLog logToNotification(OMFLogLevel logLevel) {
        OMFLogger.logToNotification(this, logLevel);
        return this;
    }

    public OMFLog logToNotification(OMFLogLevel logLevel, OMFFeature feature) {
        OMFLogger.logToNotification(this, logLevel, feature);
        return this;
    }

    public OMFLog logToSystemConsole(OMFLogLevel logLevel) {
        OMFLogger.logToSystemConsole(this, logLevel);
        return this;
    }

    public OMFLog logToSystemConsole(OMFLogLevel logLevel, OMFFeature feature) {
        OMFLogger.logToSystemConsole(this, logLevel, feature);
        return this;
    }

    public OMFLog logWarn() {
        OMFLogger.warn(this);
        return this;
    }

    public OMFLog logErr() {
        OMFLogger.err(this);
        return this;
    }

    public OMFLog logWarnWithCause(Exception e) {
        OMFLogger.warn(this, e);
        return this;
    }

    public OMFLog logErrWithCause(Exception e) {
        OMFLogger.err(this, e);
        return this;
    }




    /*
     * Getters
     */

    public Map<String, Runnable> getLinkActionMapping() {
        return linkActionMapping;
    }

    public OMFLog replaceNewLinesWithBreaks() {
        messageComponents.replaceAll(s -> s.replaceAll("\n", "<BR>"));
        return this;
    }

    public OMFLog replaceNewLinesWithBreaksInExpandLog() {
        if (expandedLog == null) {
            return null;
        }

        expandedLog
                .messageComponents
                .stream().filter(Objects::nonNull)
                .collect(Collectors.toList())
                .replaceAll(s -> s.replaceAll("\n", "<BR>"));
        return this;
    }
}
