package com.samares_engineering.omf.omf_core_framework.errormanagement2;

import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;

public class OMFErrorHandler {
    private static OMFErrorHandler instance;
    private final OMFPlugin plugin;

    private OMFErrorHandler(OMFPlugin plugin) {
        this.plugin = plugin;
    }

    public static OMFErrorHandler getInstance() {
        if (instance.plugin == null) {
            throw new CoreException2("The ErrorHandler has not been initialized yet. Please call the init() method first.");
        }
        return instance;
    }

    public static void init(OMFPlugin plugin) {
        if (instance != null) {
            throw new CoreException2("Can't initialize the ErrorHandler has it has already been initialized.");
        }
        instance = new OMFErrorHandler(plugin);
    }

    /**
     * Case where the framework user threw the OMF runtime exception to signal to the framework that an irrecoverable
     * error occurred
     *
     * @param exception       The exception to handle
     * @param impactedFeature The feature that was impacted by the exception. This can be null.
     */
    public void handleException(OMFLogException exception, OMFFeature impactedFeature) {
        if (exception instanceof OMFCriticalException) {
            handleCriticalException((OMFCriticalException) exception, impactedFeature, OMFLogLevel.ERROR);
        } else {
            handleOMFLogException(exception, impactedFeature);
        }
    }

    /**
     * Case where the framework user threw the OMF runtime exception to signal to the framework that an irrecoverable
     * error occurred
     *
     * @param exception The exception to handle
     */
    public void handleException(OMFLogException exception) {
        if (exception instanceof OMFCriticalException) {
            handleCriticalException((OMFCriticalException) exception, null, OMFLogLevel.ERROR);
        } else {
            handleOMFLogException(exception, null);
        }
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFCriticalException2.
     * In that case, we will just display a generic error to the user.
     * <br>Throws a RollbackException if the exception requires a rollback.
     * <br>Throws a CoreException2 if the exception is a CoreException2.
     * @param exception       The exception to handle*
     * @param impactedFeature The feature that was impacted by the exception. This can be null.
     */
    public void handleException(Exception exception, OMFFeature impactedFeature) {
        exception.printStackTrace();
        OMFLogger.logToNotification(generateUserMessage(exception), OMFLogLevel.ERROR, impactedFeature);
        rollbackChanges();
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFCriticalException2.
     * In that case, we will just display a generic error to the user.
     * <br><b>Call the version of the method with the impacted feature if possible.</b>
     * <br>Throws a RollbackException if the exception requires a rollback.
     * <br>Throws a CoreException2 if the exception is a CoreException2.
     * @param exception The exception to handle
     */
    public void handleException(Exception exception) {
        exception.printStackTrace();
        OMFLogger.logToNotification(generateUserMessage(exception), OMFLogLevel.ERROR);
        rollbackChanges();
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFCriticalException2.
     * In that case, we will just display a generic error to the user.
     *
     * @param exception       The exception to handle
     * @param impactedFeature The feature that was impacted by the exception. This can be null.
     */
    public void handleException(Error exception, OMFFeature impactedFeature) {
        exception.printStackTrace();
        OMFLogger.logToNotification(generateUserMessage(exception), OMFLogLevel.ERROR, impactedFeature);
        rollbackChanges();
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFCriticalException2.
     * In that case, we will just display a generic error to the user.
     * <br><b>Call the version of the method with the impacted feature if possible.</b>
     *
     * @param exception The exception to handle
     */
    public void handleException(Error exception) {
        exception.printStackTrace();
        OMFLogger.logToNotification(generateUserMessage(exception), OMFLogLevel.ERROR);
        rollbackChanges();
    }

    /**
     * Handle Core RollBackException for unexpected exceptions inside the framework.
     * Will log the exception and display a generic error to the user.
     * TODO shall log the exception in a dedicated log file.
     *
     * @param exception The exception to handle
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
     *
     * @param rollBackException The exception to handle
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
     * @param exception       The developer exception that needs to be handled.
     * @param impactedFeature The feature that was impacted by the exception. This can be null.
     * @param logLevel        The level at which the exception should be logged.
     */
    private static void handleCriticalException(OMFCriticalException exception, @CheckForNull OMFFeature impactedFeature, OMFLogLevel logLevel) {
        exception.printStackTrace();
        if (exception.isNotSilent()) {
            if (impactedFeature != null)
                OMFLogger.logToNotification(exception.getLog(), logLevel, impactedFeature);
            else
                OMFLogger.logToNotification(exception.getLog(), logLevel);
        }

        if (exception.isDeactivateFeature()) {
            if (impactedFeature != null) {
                unregisterFeature(impactedFeature);
            } else {
                OMFLogger.warnToSystemConsole("Could not deactivate feature as the feature is not known: " + exception.getClass().getSimpleName());
            }
        }
        if (exception.isRollbackChanges()) {
            rollbackChanges(); //Throws RollbackException
        }
    }

    private void handleOMFLogException(OMFLogException exception, @CheckForNull OMFFeature impactedFeature) {
        exception.printStackTrace();
        if (impactedFeature != null)
            OMFLogger.logToNotification(exception.getLog(), OMFLogLevel.ERROR, impactedFeature);
        else
            OMFLogger.logToNotification(exception.getLog(), OMFLogLevel.ERROR);
    }

    private static void unregisterFeature(OMFFeature impactedFeature) {
        new OMFLog().text("Deactivating feature").bold(impactedFeature.getName()).text("as it suffered a critical error.")
                .text("You can reactivate it in the environment options.")
                .logToUiConsole(OMFLogLevel.ERROR);
        impactedFeature.getPlugin().getFeatureRegisterer().unregisterFeature(impactedFeature);
    }

    private static void rollbackChanges() {
        if (SessionManager.getInstance().isSessionCreated(OMFUtils.getProject())) {
            throw new RollbackException();
        }
    }

    private static String generateUserMessage(Error error) {
        String message = error.getMessage();
        if (message != null && !message.isBlank() && !message.isEmpty()) {
            return message;
        } else {
            return "An error occurred during plugin execution : " + error.getClass().getSimpleName();
        }
    }

    private static String generateUserMessage(Exception exception) {
        String message = exception.getMessage();
        if (message != null && !message.isBlank() && !message.isEmpty()) {
            return message;
        } else {
            return "An error occurred during plugin execution : " + exception.getClass().getSimpleName();
        }
    }
}
