package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;

import java.util.Set;

public abstract class BaseOMFException extends RuntimeException {
    protected OMFLog2 OMFLog;
    protected Set<OMFExceptionModifier2> modifiers;

    /**
     * Exception without a cause... and a simple message.
     */
    protected BaseOMFException(String message) {
        this(new OMFLog2().text(message));
    }

    /**
     * Just an exception without a cause...
     */
    protected BaseOMFException(OMFLog2 message) {
        this(message, null);
    }

    /**
     * Simple message
     */
    protected BaseOMFException(String message, Throwable cause) {
        this(new OMFLog2().text(message), cause);
    }

    /**
     * Full constructor wrapping causing exception
     */
    protected BaseOMFException(OMFLog2 message, Throwable cause) {
        super(message.toString(), cause);
        OMFLog = message;
    }


    public OMFLog2 getUiMessage() {
        return OMFLog;
    }

    public Set<OMFExceptionModifier2> getModifiers() {
        return modifiers;
    }

    public boolean isSilent() {
        return modifiers.contains(OMFExceptionModifier2.SILENT);
    }
    public boolean isNotSilent() {
        return !isSilent();
    }

    public boolean isDeactivateFeature() {
        return modifiers.contains(OMFExceptionModifier2.DEACTIVATE_FEATURE);
    }

    public boolean isRollbackChanges() {
        return !modifiers.contains(OMFExceptionModifier2.NO_ROLLBACK);
    }
}
