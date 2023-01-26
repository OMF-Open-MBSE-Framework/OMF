/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.utils.errorManagement;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.utils.SelectInBrowserRunnable;

import java.util.HashMap;
import java.util.Map;

public class OMFLogger {

    final static String warn = "\"#FF8800\"";
    final static String err = "\"#AA0000\"";

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

    public void log(String message, Element elementTolink, OMFLogLevel level) {
        String link = "";
        if (elementTolink != null) {
            link = "Debug: " + elementTolink.getID();
            callbacks.put(link, new SelectInBrowserRunnable(elementTolink));
        }

        if (level.equals(OMFLogLevel.INFO)) {
            Application.getInstance().getGUILog().log("<font color=\"#0033FF\">[Info] " + message + "</font>");
        }
        if (level.equals(OMFLogLevel.WARNING)) {
            Application.getInstance().getGUILog().addHyperlinkedText("<font color=\"#FF8800\">[Warning] " + message + " - </font> <A>" + link + "</A>", callbacks);
        }
        if (level.equals(OMFLogLevel.ERROR)) {
            Application.getInstance().getGUILog().addHyperlinkedText("<font color=\"#AA0000\">[Error] " + message + " - </font> <A>" + link + "</A>", callbacks);
        }
    }


    public void warn(String message, Element elementToLink) {
        String link = "";
        if (elementToLink != null) {
            link = "Debug: " + elementToLink.getID();
            callbacks.put(link, new SelectInBrowserRunnable(elementToLink));
        }
        print(warn, "[Warning] " + message + " - </font> <A>" + link + "</A>");
    }

    public void err(String message, Element elementToLink) {
        String link = "";
        if (elementToLink != null) {
            link = "Debug: " + elementToLink.getID();
            callbacks.put(link, new SelectInBrowserRunnable(elementToLink));
        }
        print(warn, "[Error] " + message + " - </font> <A>" + link + "</A>");
    }



    public void logWithOwner(String message, Element elementToLink, OMFLogLevel level) {
        String linkElement = "";
        String linkOwnerElement = "DELETED";
        if(elementToLink!=null){
            linkElement = elementToLink.getHumanName();
            callbacks.put(linkElement, new SelectInBrowserRunnable(elementToLink));
            if(elementToLink.getOwner() != null) {
                linkOwnerElement = elementToLink.getOwner().getHumanName();
                callbacks.put(linkOwnerElement, new SelectInBrowserRunnable(elementToLink.getOwner()));
            }


        }

        if(level.equals(OMFLogLevel.INFO)){
//			Application.getInstance().getGUILog().log("<font color=\"#0033FF\">[Info] " + message + "</font>");
            Application.getInstance().getGUILog().addHyperlinkedText("<font color=\"#0033FF\">[Info] " + message + " - </font> " +
                    "<A>"+linkOwnerElement+"</A>" + " -> <A>"+linkElement+"</A>", callbacks);
        }
        if(level.equals(OMFLogLevel.WARNING)){
            Application.getInstance().getGUILog().addHyperlinkedText("<font color=\"#FF8800\">[Warning] " + message + " - </font> " +
                    "<A>"+linkOwnerElement+"</A>" + " -> <A>"+linkElement+"</A>", callbacks);
        }
        if(level.equals(OMFLogLevel.ERROR)){
            Application.getInstance().getGUILog().addHyperlinkedText("<font color=\"#AA0000\">[Error] " + message + " - </font> " +
                    "<A>"+linkOwnerElement+"</A>" + " -> <A>"+linkElement+"</A>", callbacks);
        }
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
