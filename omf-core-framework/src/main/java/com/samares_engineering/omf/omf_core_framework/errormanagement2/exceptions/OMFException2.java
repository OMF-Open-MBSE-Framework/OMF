package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;

import java.util.Set;

public class OMFException2 extends RuntimeException {
    private OMFLog OMFLog;
    private Set<OMFExceptionModifier> modifiers;

    /**
     * Exception without a cause... and a simple message.
     */
    public OMFException2(String message, OMFExceptionModifier... modifiers) {
        this(new OMFLog().text(message), modifiers);
    }

    /**
     * Just an exception without a cause...
     */
    public OMFException2(OMFLog message, OMFExceptionModifier... modifiers) {
        this(message, null, modifiers);
    }

    /**
     * Simple message
     */
    public OMFException2(String message, Throwable cause, OMFExceptionModifier... modifiers) {
        this(new OMFLog().text(message), cause, modifiers);
    }

    /**
     * Full constructor wrapping causing exception
     */
    public OMFException2(OMFLog message, Throwable cause, OMFExceptionModifier... modifiers) {
        super(message.toString(), cause);
        this.OMFLog = message;
        this.modifiers = Set.of(modifiers);
    }

    /*
     * Getters
     */

    public OMFLog getUiMessage() {
        return OMFLog;
    }

    public Set<OMFExceptionModifier> getModifiers() {
        return modifiers;
    }

    public boolean isSilent() {
        return modifiers.contains(OMFExceptionModifier.SILENT);
    }

    public boolean isDeactivateFeature() {
        return modifiers.contains(OMFExceptionModifier.DEACTIVATE_FEATURE);
    }

    public boolean isRollbackChanges() {
        return modifiers.contains(OMFExceptionModifier.ROLLBACK_CHANGES);
    }
}
