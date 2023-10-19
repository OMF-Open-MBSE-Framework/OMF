/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errors;


import com.google.common.base.Strings;
import com.nomagic.esi.api.messages.exceptions.LockException;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.notification.NotificationSeverity;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFRollBackException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFUnCaughtException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFUserSilentException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.CriticalFeatureException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.OMFLockException;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.utils.ColorPrinter;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.LayoutException;

public class OMFErrorHandler {


    public static void handleException(LockException eLock) {
        displayUserMessage("UnCaughtException", eLock, OMFLogLevel.ERROR);
        displayDEVMessage("UnCaughtException", eLock, OMFLogLevel.ERROR);
    }

    public static void handleException(OMFLockException eLock) {
        eLock.lockedElements.stream().forEach(lockedElement -> OMFLogger.getInstance().log(eLock.getMessage(), lockedElement, OMFLogLevel.ERROR));
    }

    public static void handleException(CriticalFeatureException criticalFeatureException) {
        handleException(criticalFeatureException, true);
    }
    public static void handleException(CriticalFeatureException criticalFeatureException, boolean cancelSession) {
        MDFeature brokenFeature = criticalFeatureException.getFeature();
        if (brokenFeature != null) new FeatureErrorHandler().handleException(criticalFeatureException);
        defaultOMFExceptionHandling(criticalFeatureException, true);
    }

    /**
     * Handle Core RollBackException: Will do nothing the rollback is already requested.
     * REMEMBER: Rollback are handled by the framework, you should not throw them yourself.
     * - In UI Action/LiveAction: throw any exception you want, the framework will handle the rollback.
     * @param rollBackException the exception to handle
     */
    public static void handleException(OMFRollBackException rollBackException) {
        displayDEVMessage("RollBack requested", rollBackException, OMFLogLevel.INFO);
    }


    public static void handleException(OMFException omfException) {
        handleException(omfException, true);
    }
    public static void handleException(OMFException exception, boolean cancelSession) {
        defaultOMFExceptionHandling(exception, cancelSession);
    }

    /**
     * Handle UserSilentException: Will handle silently the exception, no user message will be displayed.
     * Rollback will be triggered.
     * Use this exception when you want to handle silently an exception:
     * e.g. when you want a custom message to be displayed to the user but still need to rollback.
     * @param omfException the exception to handle
     */
    public static void handleException(OMFUserSilentException omfException) {
        handleException(omfException, true);
    }

    /**
     * Handle UserSilentException: Will handle silently the exception, no user message will be displayed.
     * Rollback will be handled if requested.
     * Use this exception when you want to handle silently an exception:
     * e.g. when you want a custom message to be displayed to the user but still need to rollback.
     * @param exception the exception to handle
     * @param cancelSession if true, the session will be rolled back if it exists.
     */
    public static void handleException(OMFUserSilentException exception, boolean cancelSession) {
        exception.displayDevMessage();
        handleRollBack(exception, cancelSession);
    }


    public static void handleException(LayoutException exception, boolean cancelSession) {
       defaultOMFExceptionHandling(exception, cancelSession);

    }

    /**
     * Handle all Exceptions: Any unchecked exception will be handled by this method.
     * It will display a DEV message, a user message, and handle rollback if requested.
     * @param uncaughtException the exception to handle
     */
    public static void handleException(Exception uncaughtException) {
        handleException(uncaughtException, true);
    }
    public static void handleException(Exception uncaughtException, boolean cancelSession) {
        OMFUnCaughtException omfUnCaughtException = new OMFUnCaughtException(uncaughtException);
        defaultOMFExceptionHandling(omfUnCaughtException, cancelSession);
    }


    //-------------------------------- Behavior/ Rollback ------------------------------------------------

    /**
     * Default behavior for OMFException: Display DEV message, display user message, handle rollback if requested.
     * It will also display a notification to the user depending on the criticality of the exception.
     * @param exception the exception to handle
     * @param cancelSession if true, the session will be rolled back if it exists.
     */
    private static void defaultOMFExceptionHandling(GenericException exception, boolean cancelSession) {
        exception.displayDevMessage();

        if(exception.getCriticality() == GenericException.ECriticality.SILENT) return;

        exception.displayUserMessage();

        handleRollBack(exception, cancelSession);
    }

    private static NotificationSeverity getNotificationSeverity(GenericException.ECriticality criticality) {
        switch (criticality){
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
            throw new OMFRollBackException(exception.getMessage());
    }


    //-------------------------------- LOGGING ------------------------------------------------
    public static void displayUserMessage(String tag, Exception exception, OMFLogLevel errorLvl) {
        String tagDisplay = "";
        if(!Strings.isNullOrEmpty(tag)) tagDisplay = "[" + tag + "] - ";
        OMFLogger.getInstance().log(tagDisplay + exception.getMessage(), null, errorLvl);
    }

    public static void displayDEVMessage(String tag, Exception exception, OMFLogLevel errorLvl) {
        String tagDisplay = "";
        if(!Strings.isNullOrEmpty(tag)) tagDisplay = "[" + tag + "] - ";
        String errorMSG = tagDisplay + exception.getMessage();
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
