package com.samares_engineering.omf.omf_core_framework.errormanagement2;

import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel2;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import static com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel.ERROR;

public class ErrorHandler2 {
    private static ErrorHandler2 instance;
    private final APlugin plugin;

    private ErrorHandler2(APlugin plugin) {
        this.plugin = plugin;
    }

    public static ErrorHandler2 getInstance() {
        if (instance.plugin == null) {
            throw new CoreException2("The ErrorHandler has not been initialized yet. Please call the init() method first.");
        }
        return instance;
    }

    public static void init(APlugin plugin) {
        if (instance != null) {
            throw new CoreException2("Can't initialize the ErrorHandler has it has already been initialized.");
        }
        instance = new ErrorHandler2(plugin);
    }

    /**
     * Case where the framework user threw the OMF runtime exception to signal to the framework that an unrecoverable
     * error occurred
     */
    public void handleException(OMFCriticalException2 exception, MDFeature impactedFeature) {
        exception.printStackTrace();
        if (!exception.isSilent()) {
            OMFLogger2.logToNotification(exception.getUiMessage(), OMFLogLevel2.ERROR, impactedFeature);
        }
        if (exception.isRollbackChanges()) {
            rollbackChanges();
        }
        if (exception.isDeactivateFeature()) {
            unregisterFeature(impactedFeature);
        }
    }

    /**
     * Case where the framework user threw the OMF runtime exception to signal to the framework that an unrecoverable
     * error occurred
     * <br><b>Call the version of the method with the impacted feature if possible.</b>
     */
    public void handleException(OMFCriticalException2 exception) {
        exception.printStackTrace();
        if (!exception.isSilent()) {
            OMFLogger2.logToNotification(exception.getUiMessage(), OMFLogLevel2.ERROR);
        }
        if (exception.isRollbackChanges()) {
            rollbackChanges();
        }
        if (exception.isDeactivateFeature()) {
            // We don't have the contextual feature
        }
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFCriticalException2.
     * In that case, we will just display a generic error to the user.
     */
    public void handleException(RuntimeException exception, MDFeature impactedFeature) {
        exception.printStackTrace();
        OMFLogger2.logToNotification("An error occurred during plugin execution: " + exception.getMessage(), OMFLogLevel2.ERROR, impactedFeature);
        rollbackChanges();
        unregisterFeature(impactedFeature);
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFCriticalException2.
     * In that case, we will just display a generic error to the user.
     * <br><b>Call the version of the method with the impacted feature if possible.</b>
     */
    public void handleException(RuntimeException exception) {
        exception.printStackTrace();
        OMFLogger2.logToNotification("An error occurred during plugin execution: " + exception.getMessage(), OMFLogLevel2.ERROR);
        rollbackChanges();
    }

    private static void unregisterFeature(MDFeature impactedFeature) {
        new OMFLog2().text("Deactivating feature").bold(impactedFeature.getName()).text("as it suffered a critical error.")
                .text("You can reactivate it in the environment options.")
                .logToConsole(OMFLogLevel2.ERROR);
        impactedFeature.getPlugin().getFeatureRegister().unregisterFeature(impactedFeature);
    }

    private static void rollbackChanges() {
        //new OMFLog2().text("Rolling back action's changes after encountering critical error")
        //        .logToConsole(OMFLogLevel2.ERROR);
        if (SessionManager.getInstance().isSessionCreated(OMFUtils.getProject())) {
            throw new RollbackException2();
        }
    }
}
