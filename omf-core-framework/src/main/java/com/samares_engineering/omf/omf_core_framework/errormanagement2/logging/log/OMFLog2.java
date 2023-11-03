package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.utils.ElementAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OMFLog2 {
    private final List<String> messageComponents = new ArrayList<>();
    private final Map<String, Runnable> linkActionMapping = new HashMap<>();

    public OMFLog2 text(String string) {
        messageComponents.add(string);
        return this;
    }

    public OMFLog2 bold(String string) {
        return text("<B>" + string + "</B>");
    }

    public OMFLog2 italic(String string) {
        return text("<I>" + string + "</I>");
    }

    public OMFLog2 underline(String string) {
        return text("<U>" + string + "</U>");
    }

    public OMFLog2 strike(String string) {
        return text("<S>" + string + "</S>");
    }

    public OMFLog2 color(String string, String color) {
        return text("<font color=" + color + ">" + string + "</font>");
    }

    public OMFLog2 linkElement(String linkText, Element elementToLink) {
        linkActionMapping.put(linkText, new ElementAction(elementToLink)::selectInBrowser);
        return text("<A>" + linkText + "</A>");
    }

    public OMFLog2 link(String linkText, String url) {
        return text("<A href=" + url + ">" + linkText + "</A>");
    }

    public OMFLog2 linkAction(String linkText, Runnable action) {
        linkActionMapping.put(linkText, action);
        return text("<A>" + linkText + "</A>");
    }

    /*
     * Log message formatting
     */

    public String toHTMLFormat(OMFLogLevel logLevel) {
        return "<font color=" + getMessageColor(logLevel) + ">" +
                    getLogLevelPrefix(logLevel) + " " + toString(" ") +
                "</font>";
    }

    public String toHTMLFormat(OMFLogLevel logLevel, String pluginName) {
        return "<font color=" + getMessageColor(logLevel) + ">" + getLogLevelPrefix(logLevel)+ "[" + pluginName + "]"
                + " " + toString(" ") +
                "</font>";
    }
    public String toHTMLFormat(OMFLogLevel logLevel, String pluginName, String featureName) {
        return "<font color=" + getMessageColor(logLevel) + ">" + getLogLevelPrefix(logLevel) + "[" + pluginName + "]"
                + "[" + featureName + "]" + " " + toString(" ") +
                "</font>";
    }

    private static String getLogLevelPrefix(OMFLogLevel logLevel) {
        switch (logLevel) {
            case WARNING:
                return "[Warning] ";
            case ERROR:
                return "[Error] ";
            case INFO:
            default:
                return "[Info] ";
        }
    }

    private static String getMessageColor(OMFLogLevel logLevel) {
        switch (logLevel) {
            case WARNING:
                return OMFColors2.WARN;
            case ERROR:
                return OMFColors2.ERROR;
            case INFO:
            default:
                return OMFColors2.INFO;
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

    public OMFLog2 logToConsole(OMFLogLevel logLevel) {
        OMFLogger2.logToConsole(this, logLevel);
        return this;
    }

    public void logToConsole(OMFLogLevel logLevel, MDFeature feature) {
        OMFLogger2.logToConsole(this, logLevel, feature);
    }

    /*
     * Getters
     */

    public Map<String, Runnable> getLinkActionMapping() {
        return linkActionMapping;
    }
}
