package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;

import java.util.Set;

/**
 * Extends this exception for your plugin development.
 */
public abstract class OMFDevException extends BaseOMFException {
    protected Set<OMFExceptionModifier2> modifiers;

    protected OMFDevException(String message) {
        super(message);
    }

    protected OMFDevException(OMFLog2 message) {
        super(message);
    }

    protected OMFDevException(String message, Throwable cause) {
        super(message, cause);
    }

    protected OMFDevException(OMFLog2 message, Throwable cause) {
        super(message, cause);
    }




    public boolean isNotSilent() {
        return !isSilent();
    }

    public boolean isSilent() {
        return modifiers.contains(OMFExceptionModifier2.SILENT);
    }


    public boolean isDeactivateFeature() {
        return modifiers.contains(OMFExceptionModifier2.DEACTIVATE_FEATURE);
    }


    public boolean isRollbackChanges() {
        return !modifiers.contains(OMFExceptionModifier2.NO_ROLLBACK);
    }


    /*
     * Getters
     */

    public OMFLog2 getUiMessage() {
        return OMFLog;
    }

    public Set<OMFExceptionModifier2> getModifiers() {
        return modifiers;
    }

}
