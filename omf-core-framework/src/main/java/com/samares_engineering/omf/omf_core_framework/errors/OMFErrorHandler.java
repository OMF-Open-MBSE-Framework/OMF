/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errors;


import com.nomagic.esi.api.messages.exceptions.LockException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.notification.Notification;
import com.nomagic.magicdraw.ui.notification.NotificationManager;
import com.nomagic.magicdraw.ui.notification.NotificationSeverity;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.LayoutException;
import com.samares_engineering.omf.omf_core_framework.utils.ColorPrinter;

public class OMFErrorHandler {


    public static void handleException(LockException eLock) {
        displayUserMessage("UnCaughtException", eLock, OMFLogLevel.ERROR);
        displayDEVMessage("UnCaughtException", eLock, OMFLogLevel.ERROR);
    }

    public static void handleException(OMFLockException eLock) {
        eLock.lockedElements.stream().forEach(lockedElement -> OMFLogger.getInstance().log(eLock.getMessage(), lockedElement, OMFLogLevel.ERROR));
    }

    public static void handleException(OMFRollBackException rollBackException) {
        displayDEVMessage("RollBack requested", rollBackException, OMFLogLevel.INFO);
    }


    public static void handleException(OMFException omfException) {
        handleException(omfException, true);
    }

    public static void handleException(OMFException exception, boolean cancelSession) {
        defaultOMFExceptionHandling(exception, cancelSession);
    }

    public static void handleException(OMFUserSilentException omfException) {
        handleException(omfException, true);
    }
    public static void handleException(OMFUserSilentException exception, boolean cancelSession) {
        exception.displayDevMessage();
        handleRollBack(exception, cancelSession);
    }


    public static void handleException(LayoutException exception, boolean cancelSession) {
       defaultOMFExceptionHandling(exception, cancelSession);

    }

    public static void handleException(Exception uncaughtException) {
        handleException(uncaughtException, true);
    }
    public static void handleException(Exception uncaughtException, boolean cancelSession) {
        OMFUnCaughtException omfUnCaughtException = new OMFUnCaughtException(uncaughtException);
        defaultOMFExceptionHandling(omfUnCaughtException, cancelSession);
    }

    //-------------------------------- Behavior/ Rollback ------------------------------------------------
    private static void defaultOMFExceptionHandling(GenericException exception, boolean cancelSession) {
        exception.displayDevMessage();
        exception.displayUserMessage();
        NotificationManager.getInstance().showNotification(new Notification(
                "[Plugin Error]", //TODO REPLACE WITH GENERIC EXCEPTION TAG
                "[Plugin Error]",
                "[Plugin Error] " + exception.getUserMessage(),
                NotificationSeverity.ERROR));

        handleRollBack(exception, cancelSession);
    }

    private static void handleRollBack(GenericException exception, boolean cancelSession) {
        boolean isSessionCreated = SessionManager.getInstance().isSessionCreated(OMFUtils.currentProject);
        if (cancelSession && isSessionCreated)
            throw new OMFRollBackException(exception.getMessage());
    }


    //-------------------------------- LOGGING ------------------------------------------------

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
