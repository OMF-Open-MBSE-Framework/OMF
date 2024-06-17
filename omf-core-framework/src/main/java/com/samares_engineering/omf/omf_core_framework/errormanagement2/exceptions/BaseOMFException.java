package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;

import java.util.Set;

public abstract class BaseOMFException extends RuntimeException {
    protected com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog OMFLog;
    protected Set<OMFExceptionModifier> modifiers;

    /**
     * Exception without a cause... and a simple message.
     */
    protected BaseOMFException(String message) {
        this(new OMFLog().text(message));
    }

    /**
     * Just an exception without a cause...
     */
    protected BaseOMFException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message) {
        this(message, null);
    }

    /**
     * Simple message
     */
    protected BaseOMFException(String message, Throwable cause) {
        this(new OMFLog().text(message), cause);
    }

    /**
     * Full constructor wrapping causing exception
     */
    protected BaseOMFException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, Throwable cause) {
        super(message.toString(), cause);
        OMFLog = message;
    }


    public com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog getUiMessage() {
        return OMFLog;
    }

    public Set<OMFExceptionModifier> getModifiers() {
        return modifiers;
    }

    public boolean isSilent() {
        return modifiers.contains(OMFExceptionModifier.SILENT);
    }
    public boolean isNotSilent() {
        return !isSilent();
    }

    public boolean isDeactivateFeature() {
        return modifiers.contains(OMFExceptionModifier.DEACTIVATE_FEATURE);
    }

    public boolean isRollbackChanges() {
        return !modifiers.contains(OMFExceptionModifier.NO_ROLLBACK);
    }


}
