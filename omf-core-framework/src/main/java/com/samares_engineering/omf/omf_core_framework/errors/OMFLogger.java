/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errors;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.utils.ElementAction;

import java.util.HashMap;
import java.util.Map;

public class OMFLogger {

    final static String warn = "\"#FF8800\"";
    final static String err = "\"#AA0000\"";
    final static String info = "\"#0033FF\"";

    private Map<String, Runnable> callbacks;

    private OMFLogger() {
        callbacks = new HashMap<String, Runnable>();
    }

    public static OMFLogger getInstance() {
        return LoggerNotificationWindowHolder.instance;
    }

    public void clearCallBack() {
        this.callbacks.clear();
    }

    public void log(String message, Element elementToLink, OMFLogLevel level) {
        String linkText = "";
        if (elementToLink != null) {
            linkText = "Debug: " + elementToLink.getID();
            callbacks.put(linkText, new ElementAction(elementToLink)::selectInBrowser);
        }

        String color;
        String logCategory;

        switch (level) {
            case WARNING:
                color = warn;
                logCategory = "[Warning] ";
                break;
            case ERROR:
                color = err;
                logCategory = "[Error] ";
                break;

            case INFO:
            default:
                color = info;
                logCategory = "[Info] ";
                break;
        }

        Application.getInstance().getGUILog().addHyperlinkedText(
                "<font color=" + color + ">" +        //Starting HTML tag and color setting
                   logCategory + message +              //message
                    "</font>" + " - " +                //Ending HTML tag and separator
                   " <A>" + linkText + "</A>", callbacks); //link to element if any

    }


    public void info(String message) {
        log(message, null, OMFLogLevel.INFO);
    }
    public void info(String message, Element elementToLink) {
        log(message, elementToLink, OMFLogLevel.INFO);
    }
    public void warn(String message) {
        log(message, null, OMFLogLevel.WARNING);
    }

    public void warn(String message, Element elementToLink) {
        log(message, elementToLink, OMFLogLevel.WARNING);
    }

    public void error(String message) {
        log(message, null, OMFLogLevel.ERROR);
    }
    public void error(String message, Element elementToLink) {
        log(message, elementToLink, OMFLogLevel.ERROR);
    }



    public void logWithOwner(String message, Element elementToLink, OMFLogLevel level) {
        String linkElement = "";
        String linkOwnerElement = "DELETED";
        if(elementToLink!=null){
            linkElement = elementToLink.getHumanName();
            callbacks.put(linkElement, new ElementAction(elementToLink)::selectInBrowser);
            if(elementToLink.getOwner() != null) {
                linkOwnerElement = elementToLink.getOwner().getHumanName();
                callbacks.put(linkOwnerElement, new ElementAction(elementToLink.getOwner())::selectInBrowser);
            }
        }
        String color;
        String logCategory;

        switch (level) {
            case WARNING:
                color = warn;
                logCategory = "[Warning] ";
                break;
            case ERROR:
                color = err;
                logCategory = "[Error] ";
                break;

            case INFO:
            default:
                color = info;
                logCategory = "[Info] ";
                break;
        }
        Application.getInstance().getGUILog().addHyperlinkedText(
                "<font color=" + color + ">" +        //Starting HTML tag and color setting
                        logCategory + message +          //message
                    "</font>" + " - " +                  //Ending HTML tag
                    "<A>"+linkOwnerElement+"</A>" + " -> " + //link to owner element
                    "<A>" + linkElement+"</A>", callbacks); //link to element if any

    }

    public void logLn(String message,Element elementToLink, OMFLogLevel level) {
        log(message, elementToLink, level);
        Application.getInstance().getGUILog().log(" ");
    }


    private void print(String color, String message){
        Application.getInstance().getGUILog().addHyperlinkedText("<font color=" + color + ">[Info] ", callbacks);
    }
    private static class LoggerNotificationWindowHolder {
        private final static OMFLogger instance = new OMFLogger();
    }
}
