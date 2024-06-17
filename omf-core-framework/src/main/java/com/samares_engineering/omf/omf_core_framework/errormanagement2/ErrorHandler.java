package com.samares_engineering.omf.omf_core_framework.errormanagement2;

import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFDevException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;

public class ErrorHandler {
    private static ErrorHandler instance;
    private final APlugin plugin;

    private ErrorHandler(APlugin plugin) {
        this.plugin = plugin;
    }

    public static ErrorHandler getInstance() {
        if (instance.plugin == null) {
            throw new CoreException2("The ErrorHandler has not been initialized yet. Please call the init() method first.");
        }
        return instance;
    }

    public static void init(APlugin plugin) {
        if (instance != null) {
            throw new CoreException2("Can't initialize the ErrorHandler has it has already been initialized.");
        }
        instance = new ErrorHandler(plugin);
    }


    /**
     * Case where the framework user threw the OMF runtime exception to signal to the framework that a recoverable
     * error occurred
     * @param exception the exception to handle
     * @param impactedFeature the feature that was impacted by the exception
     */
    public void handleException(OMFCriticalException exception, MDFeature impactedFeature) {
        defaultHandlingDevException(exception, impactedFeature, OMFLogLevel.ERROR);
    }


    /**
     * Case where the framework user threw the OMF runtime exception to signal to the framework that a recoverable
     * error occurred
     * @param exception the exception to handle
     */
    public void handleException(OMFCriticalException exception ) {
        defaultHandlingDevException(exception, null, OMFLogLevel.ERROR);
    }
    /**
     * Case where the framework user threw the OMF runtime exception to signal to the framework that a recoverable
     * error occurred
     * @param exception the exception to handle
     * @param impactedFeature the feature that was impacted by the exception
     */
    public void handleException(OMFDevException exception, MDFeature impactedFeature) {
        defaultHandlingDevException(exception, impactedFeature, OMFLogLevel.ERROR);
    }

    /**
     * Case where the framework user threw the OMF runtime exception to signal to the framework that a recoverable
     * error occurred
     * @param exception the exception to handle
     */
    public void handleException(OMFDevException exception ) {
        defaultHandlingDevException(exception, null, OMFLogLevel.ERROR);
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFCriticalException2.
     * In that case, we will just display a generic error to the user.
     * @param exception the exception to handle
     * @param impactedFeature the feature that was impacted by the exception
     */
    public void handleException(RuntimeException exception, MDFeature impactedFeature) {
        exception.printStackTrace();
        OMFLogger.logToNotification("An error occurred during plugin execution: " + exception.getMessage(), OMFLogLevel.ERROR, impactedFeature);
        unregisterFeature(impactedFeature);
        rollbackChanges();
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFCriticalException2.
     * In that case, we will just display a generic error to the user.
     * <br><b>Call the version of the method with the impacted feature if possible.</b>
     * @param exception the exception to handle
     */
    public void handleException(RuntimeException exception) {
        exception.printStackTrace();
        OMFLogger.logToNotification("An error occurred during plugin execution: " + exception.getMessage(), OMFLogLevel.ERROR);
        rollbackChanges();
    }
    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFCriticalException2.
     * In that case, we will just display a generic error to the user.
     * @param exception the exception to handle
        * @param impactedFeature the feature that was impacted by the exception
     */
    public void handleException(Exception exception, MDFeature impactedFeature) {
        exception.printStackTrace();
        OMFLogger.logToNotification("An error occurred during plugin execution: " + exception.getMessage(), OMFLogLevel.ERROR, impactedFeature);
        unregisterFeature(impactedFeature);
        rollbackChanges();
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFCriticalException2.
     * In that case, we will just display a generic error to the user.
     * <br><b>Call the version of the method with the impacted feature if possible.</b>
     * @param exception the exception to handle
     */
    public void handleException(Exception exception) {
        exception.printStackTrace();
        OMFLogger.logToNotification("An error occurred during plugin execution: " + exception.getMessage(), OMFLogLevel.ERROR);
        rollbackChanges();
    }

    /**
     * Handle Core RollBackException for unexpected exceptions inside the framework.
     * Will log the exception and display a generic error to the user.
     * TODO shall log the exception in a dedicated log file.
     * @param exception the exception to handle
     */
    public void handleException(CoreException2 exception) {
        exception.printStackTrace();
        OMFLogger.errorToNotification("An internal Core error occurred during plugin execution: " + exception.getMessage());
        OMFLogger.errorToSystemConsole("An internal Core error occurred during plugin execution: " + exception.getMessage());
    }


    /**
     * Handle Core RollBackException: Will do nothing the rollback is already requested.
     * REMEMBER: Rollback are handled by the framework, you should not throw them yourself.
     * - In UI Action/LiveAction: throw any exception you want, the framework will handle the rollback.
     * @param rollBackException the exception to handle
     */
    public void handleException(RollbackException rollBackException) {
        OMFLogger.infoToSystemConsole("RollBack requested");
    }


    /**
     * This method handles the default behavior for developer exceptions in the application.
     * It logs the exception, checks if the exception should be silent or not, and performs
     * necessary actions based on the properties of the exception.
     * <br>Throws a RollbackException if the exception requires a rollback.
     *
     * @param exception The developer exception that needs to be handled.
     * @param impactedFeature The feature that was impacted by the exception. This can be null.
     * @param logLevel The level at which the exception should be logged.
     */
    private static void defaultHandlingDevException(OMFDevException exception, @CheckForNull MDFeature impactedFeature, OMFLogLevel logLevel) {
        exception.printStackTrace();
        if (exception.isNotSilent()) {
            if (impactedFeature != null)
                OMFLogger.logToNotification(exception.getUiMessage(), logLevel, impactedFeature);
            else
                OMFLogger.logToNotification(exception.getUiMessage(), logLevel);
        }

        if (exception.isDeactivateFeature()) {
            if (impactedFeature != null) {
                unregisterFeature(impactedFeature);
            }else {
                OMFLogger.warnToSystemConsole("Could not deactivate feature as the feature is not known: " + exception.getClass().getSimpleName());
            }
        }
        if (exception.isRollbackChanges()) {
            rollbackChanges(); //Throws RollbackException
        }
    }


    private static void unregisterFeature(MDFeature impactedFeature) {
        new OMFLog().text("Deactivating feature").bold(impactedFeature.getName()).text("as it suffered a critical error.")
                .text("You can reactivate it in the environment options.")
                .logToUiConsole(OMFLogLevel.ERROR);
        impactedFeature.getPlugin().getFeatureRegister().unregisterFeature(impactedFeature);
    }

    private static void rollbackChanges() {
        //new OMFLog2().text("Rolling back action's changes after encountering critical error")
        //        .logToConsole(OMFLogLevel.ERROR);
        if (SessionManager.getInstance().isSessionCreated(OMFUtils.getProject())) {
            throw new RollbackException();
        }
    }
}
