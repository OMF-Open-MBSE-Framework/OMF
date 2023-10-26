package com.samares_engineering.omf.omf_core_framework.errormanagement2;

import com.nomagic.ci.persistence.local.a.U;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errors.cancelsession.UndoManager;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

import static com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel.ERROR;

public class ErrorHandler2 {
    private static ErrorHandler2 instance;
    private final APlugin plugin;

    private ErrorHandler2(APlugin plugin) {
        this.plugin = plugin;
    }

    public static ErrorHandler2 getInstance() {
        if (instance.plugin == null) {
            throw new RuntimeException("The ErrorHandler has not been initialized yet. Please call the init() method first.");
        }
        return instance;
    }

    public static void init(APlugin plugin) {
        if (instance != null) {
            throw new RuntimeException("Can't initialize the ErrorHandler has it has already been initialized.");
        }
        instance = new ErrorHandler2(plugin);
    }

    /**
     * Case where the framework user threw the OMF runtime exception to signal to the framework that an unrecoverable
     * error occurred
     */
    public void handleException(OMFException2 exception, MDFeature impactedFeature) {
        exception.printStackTrace();
        if (!exception.isSilent()) {
            OMFLogger2.logToConsole(exception.getUiMessage(), ERROR, impactedFeature);
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
    public void handleException(OMFException2 exception) {
        exception.printStackTrace();
        if (!exception.isSilent()) {
            OMFLogger2.logToConsole(exception.getUiMessage(), ERROR);
        }
        if (exception.isRollbackChanges()) {
            rollbackChanges();
        }
        if (exception.isDeactivateFeature()) {
            // We don't have the contextual feature
        }
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFException2.
     * In that case, we will just display a generic error to the user.
     */
    public void handleException(RuntimeException exception, MDFeature impactedFeature) {
        exception.printStackTrace();
        OMFLogger2.logToConsole("An unexpected error occurred during plugin execution: " + exception.getMessage(),
                ERROR, impactedFeature);
        rollbackChanges();
        unregisterFeature(impactedFeature);
    }

    /**
     * Catches all other unchecked exceptions that have not been wrapped by the framework user into a OMFException2.
     * In that case, we will just display a generic error to the user.
     * <br><b>Call the version of the method with the impacted feature if possible.</b>
     */
    public void handleException(RuntimeException exception) {
        exception.printStackTrace();
        OMFLogger2.logToConsole("An unexpected error occurred during plugin execution: " + exception.getMessage(), ERROR);
        rollbackChanges();
    }

    private static void unregisterFeature(MDFeature impactedFeature) {
        new OMFLog().text("Deactivating feature").bold(impactedFeature.getName()).text("as it suffered a critical error.")
                .logToConsole(ERROR);
        impactedFeature.getPlugin().getFeatureRegister().unregisterFeature(impactedFeature);
    }

    private static void rollbackChanges() {
        new OMFLog().text("Rolling back action's changes after encountering critical error")
                .logToConsole(ERROR);
        throw new RollbackException2();
    }
}
