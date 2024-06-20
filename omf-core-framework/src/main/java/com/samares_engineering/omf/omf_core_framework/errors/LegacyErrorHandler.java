/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errors;


import com.google.common.base.Strings;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.notification.NotificationSeverity;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.UnCaughtException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.LockException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.LayoutException;

public class LegacyErrorHandler {
    public static void handleException(com.nomagic.esi.api.messages.exceptions.LockException eLock) {
        displayUserMessage("UnCaughtException", eLock, OMFLogLevel.ERROR);
        displayDEVMessage("UnCaughtException", eLock, OMFLogLevel.ERROR);
    }

    public static void handleException(LockException eLock) {
        eLock.lockedElements.forEach(lockedElement -> new OMFLog().err(eLock.getMessage()).linkElementAndParent(lockedElement));
    }

    /**
     * Handle Core RollBackException: Will do nothing the rollback is already requested.
     * REMEMBER: Rollback are handled by the framework, you should not throw them yourself.
     * - In UI Action/LiveAction: throw any exception you want, the framework will handle the rollback.
     *
     * @param rollBackException the exception to handle
     */
    public static void handleException(RollbackException rollBackException) {
        displayDEVMessage("RollBack requested", rollBackException, OMFLogLevel.INFO);
    }

    public static void handleException(LegacyOMFException omfException) {
        handleException(omfException, true);
    }

    public static void handleException(LegacyOMFException exception, boolean cancelSession) {
        defaultOMFExceptionHandling(exception, cancelSession);
    }

    public static void handleException(LayoutException exception, boolean cancelSession) {
        defaultOMFExceptionHandling(exception, cancelSession);

    }

    /**
     * Handle all Exceptions: Any unchecked exception will be handled by this method.
     * It will display a DEV message, a user message, and handle rollback if requested.
     *
     * @param uncaughtException the exception to handle
     */
    public static void handleException(Exception uncaughtException) {
        handleException(uncaughtException, true);
    }

    public static void handleException(Exception uncaughtException, boolean cancelSession) {
        UnCaughtException unCaughtException = new UnCaughtException(uncaughtException);
        defaultOMFExceptionHandling(unCaughtException, cancelSession);
    }


    //-------------------------------- Behavior/ Rollback ------------------------------------------------

    /**
     * Default behavior for OMFException: Display DEV message, display user message, handle rollback if requested.
     * It will also display a notification to the user depending on the criticality of the exception.
     *
     * @param exception     the exception to handle
     * @param cancelSession if true, the session will be rolled back if it exists.
     */
    private static void defaultOMFExceptionHandling(GenericException exception, boolean cancelSession) {
        exception.displayDevMessage();

        if (exception.getCriticality() == GenericException.ECriticality.SILENT) return;

        exception.displayUserMessage();

        handleRollBack(exception, cancelSession);
    }

    private static NotificationSeverity getNotificationSeverity(GenericException.ECriticality criticality) {
        switch (criticality) {
            case CRITICAL:
                return NotificationSeverity.ERROR;
            case SILENT:
            case ALERT:
            default:
                return NotificationSeverity.WARNING;
        }
    }

    private static void handleRollBack(GenericException exception, boolean cancelSession) {
        boolean isSessionCreated = SessionManager.getInstance().isSessionCreated(OMFUtils.getProject());
        if (cancelSession && isSessionCreated)
            throw new RollbackException();
    }


    //-------------------------------- LOGGING ------------------------------------------------
    public static void displayUserMessage(String tag, Exception exception, OMFLogLevel errorLvl) {
        String tagDisplay = "";
        if (!Strings.isNullOrEmpty(tag)) tagDisplay = "[" + tag + "] - ";
        new OMFLog().text(tagDisplay + exception.getMessage(), errorLvl).logToSystemConsole(errorLvl);
    }

    public static void displayDEVMessage(String tag, Exception exception, OMFLogLevel errorLvl) {
        String tagDisplay = "";
        if (!Strings.isNullOrEmpty(tag)) tagDisplay = "[" + tag + "] - ";
        String errorMSG = tagDisplay + exception.getMessage();
        switch (errorLvl) {
            case INFO:
                SysoutColorPrinter.status(errorMSG);
                break;
            case WARNING:
                SysoutColorPrinter.warn(errorMSG);
                exception.printStackTrace();
                break;
            case ERROR:
                SysoutColorPrinter.err(errorMSG);
                exception.printStackTrace();
                break;
        }
    }
}
