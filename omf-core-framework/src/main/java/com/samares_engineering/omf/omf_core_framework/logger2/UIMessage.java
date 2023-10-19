package com.samares_engineering.omf.omf_core_framework.logger2;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.utils.ElementAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIMessage {
    List<String> messageComponents = new ArrayList<>();
    OMFLogLevel logLevel = OMFLogLevel.INFO; // lowest log level by default
    static final String WARN_COLOR = "\"#FF8800\"";
    static final String ERROR_COLOR = "\"#AA0000\"";
    static final String INFO_COLOR = "\"#0033FF\"";

    private Map<String, Runnable> linkActionMapping = new HashMap<>();

    private UIMessage(OMFLogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public UIMessage logLevel(OMFLogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public static UIMessage info() {
        return new UIMessage(OMFLogLevel.INFO);
    }

    public static UIMessage warn() {
        return new UIMessage(OMFLogLevel.WARNING);
    }

    public static UIMessage err() {
        return new UIMessage(OMFLogLevel.ERROR);
    }

    public UIMessage text(String string) {
        messageComponents.add(string);
        return this;
    }

    public UIMessage bold(String string) {
        return text("<B>" + string + "</B>");
    }

    public UIMessage italic(String string) {
        return text("<I>" + string + "</I>");
    }

    public UIMessage underline(String string) {
        return text("<U>" + string + "</U>");
    }

    public UIMessage strike(String string) {
        return text("<S>" + string + "</S>");
    }

    public UIMessage color(String string, String color) {
        return text("<font color=" + color + ">" + string + "</font>");
    }

    public UIMessage link(String linkText, Element elementToLink) {
        linkActionMapping.put(linkText, new ElementAction(elementToLink)::selectInBrowser);
        return text("<A>" + linkText + "</A>");
    }

    public UIMessage linkAction(String linkText, Runnable action) {
        linkActionMapping.put(linkText, action);
        return text("<A>" + linkText + "</A>");
    }

    @Override
    public String toString() {
        return "<font color=" + getMessageColor() + ">" +
                    getLogLevelPrefix() + " " + getRawMessage(" ") +
                "</font>";
    }

    public String getRawMessage(String delimiter) {
        StringBuilder message = new StringBuilder();
        messageComponents.forEach(component -> message.append(component).append(delimiter));
        return message.toString();
    }

    public UIMessage logToConsole() {
        Application.getInstance().getGUILog().addHyperlinkedText(toString(), linkActionMapping);
        return this;
    }


    private String getLogLevelPrefix() {
        switch (logLevel) {
            case WARNING:
                return "[Warning]";
            case ERROR:
                return "[Error]";
            case INFO:
            default:
                return "[Info]";
        }
    }

    private String getMessageColor() {
        switch (logLevel) {
            case WARNING:
                return WARN_COLOR;
            case ERROR:
                return ERROR_COLOR;
            case INFO:
            default:
                return INFO_COLOR;
        }
    }
}
