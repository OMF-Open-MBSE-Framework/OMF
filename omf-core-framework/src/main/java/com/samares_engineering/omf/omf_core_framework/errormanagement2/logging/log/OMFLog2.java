package com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2;
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

    public OMFLog2 breakLine() {
        return text("<BR>");
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
    public OMFLog2 warn(String string) {return color(string, OMFColors2.WARN);}
    public OMFLog2 warn(OMFLog2 log) {return warn(log.toString());}
    public OMFLog2 info(String string) {
        return color(string, OMFColors2.INFO);
    }
    public OMFLog2 info(OMFLog2 log) {return info(log.toString());}
    public OMFLog2 err(String string) {
        return color(string, OMFColors2.ERROR);
    }
    public OMFLog2 err(OMFLog2 log) {return err(log.toString());}

    public OMFLog2 linkElement(String linkText, Element elementToLink) {
        linkActionMapping.put(linkText, new ElementAction(elementToLink)::selectInBrowser);
        return text("<A>" + linkText + "</A>");
    }

    public OMFLog2 linkElementAndParent(Element elementToLink) {
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
        return text("<A>" + linkElementName + "</A> -> <A>" + linkOwnerElementName + "</A>");
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

    public String toHTMLFormat(OMFLogLevel2 logLevel) {
        return "<font color=" + getMessageColor(logLevel) + ">" + getPrefix(logLevel)
                + " " + toString(" ") + "</font>";
    }

    public String toHTMLFormat(OMFLogLevel2 logLevel, String pluginName) {
        return "<font color=" + getMessageColor(logLevel) + ">" + getPrefix(logLevel, pluginName)
                + " " + toString(" ") + "</font>";
    }
    public String toHTMLFormat(OMFLogLevel2 logLevel, String pluginName, String featureName) {
        return "<font color=" + getMessageColor(logLevel) + ">" + getPrefix(logLevel, pluginName, featureName)+ " "
                + toString(" ") + "</font>";
    }

    public static String getPrefix(OMFLogLevel2 logLevel) {
        return "[" + getLogLevelPrefix(logLevel) + "]";
    }
    public static String getPrefix(OMFLogLevel2 logLevel, String pluginName) {
        return getPrefix(logLevel) + "[" + pluginName + "]";
    }
    public static String getPrefix(OMFLogLevel2 logLevel, String pluginName, String featureName) {
        return getPrefix(logLevel, pluginName) + "[" + featureName + "]";
    }

    private static String getLogLevelPrefix(OMFLogLevel2 logLevel) {
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

    private static String getMessageColor(OMFLogLevel2 logLevel) {
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

    public OMFLog2 logToConsole(OMFLogLevel2 logLevel) {
        OMFLogger2.logToUIConsole(this, logLevel);
        return this;
    }

    public void logToConsole(OMFLogLevel2 logLevel, MDFeature feature) {
        OMFLogger2.logToUIConsole(this, logLevel, feature);
    }

    /*
     * Getters
     */

    public Map<String, Runnable> getLinkActionMapping() {
        return linkActionMapping;
    }

    public OMFLog2 replaceNewLinesWithBreaks() {
        messageComponents.replaceAll(s -> s.replaceAll("\n", "<BR>"));
        return this;
    }


}
