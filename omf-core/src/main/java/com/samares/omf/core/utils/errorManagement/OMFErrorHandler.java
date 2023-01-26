/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.utils.errorManagement;


import com.nomagic.esi.api.messages.exceptions.LockException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.samares.omf.core.utils.diagrams.LayoutException;
import com.samares.omf.core.utils.ColorPrinter;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.utils.errorManagement.exceptions.OMFException;
import com.samares.omf.core.utils.errorManagement.exceptions.OMFLockException;

public class OMFErrorHandler {


    public static void handleException(LockException eLock) {
        displayUserMessage("UnCaughtException", eLock, OMFLogLevel.ERROR);
        displayDEVMessage("UnCaughtException", eLock, OMFLogLevel.ERROR);
    }

    public static void handleException(OMFLockException eLock) {
        eLock.lockedElements.stream().forEach(lockedElement -> OMFLogger.getInstance().log(eLock.getMessage(), lockedElement, OMFLogLevel.ERROR));
    }

    public static void handleException(Exception uncaughtException) {
        handleException(uncaughtException, true);
    }

    public static void handleException(OMFException exception, boolean cancelSession) {
        boolean isSessionCreated = SessionManager.getInstance().isSessionCreated(OMFUtils.currentProject);
        exception.displayDevMessage();
        if (cancelSession && isSessionCreated)
            throw new RuntimeException(exception.getMessage());

        exception.displayUserMessage();
    }

    public static void handleException(LayoutException exception, boolean cancelSession) {
        boolean isSessionCreated = SessionManager.getInstance().isSessionCreated(OMFUtils.currentProject);
        exception.displayDevMessage();
        if (cancelSession && isSessionCreated)
            throw new RuntimeException(exception.getMessage());

        exception.displayUserMessage();
    }

    public static void handleException(Exception uncaughtException, boolean cancelSession) {
        boolean isSessionCreated = SessionManager.getInstance().isSessionCreated(OMFUtils.currentProject);
//        displayUserMessage("UnCaughtException", uncaughtException, LOG_LEVEL.ERROR);
        displayDEVMessage("UnCaughtException", uncaughtException, OMFLogLevel.ERROR);
        if (cancelSession && isSessionCreated)
            throw new RuntimeException("Error during transaction, cancel session requested");
    }

    public static void displayUserMessage(String tag, Exception exception, OMFLogLevel errorLvl) {
        OMFLogger.getInstance().log("[" + tag + "] - " + exception.getMessage(), null, errorLvl);
    }

    public static void displayDEVMessage(String tag, Exception exception, OMFLogLevel errorLvl) {
        String errorMSG = "[" + tag + "] - " + exception.getMessage();
        switch (errorLvl) {
            case INFO:
                ColorPrinter.status(errorMSG);
                break;
            case WARNING:
                ColorPrinter.warn(errorMSG);
                exception.printStackTrace();
                break;
            case ERROR:
                ColorPrinter.err(errorMSG);
                exception.printStackTrace();
                break;
        }
    }
}
