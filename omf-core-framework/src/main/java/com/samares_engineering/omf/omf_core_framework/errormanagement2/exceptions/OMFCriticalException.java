package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;

import java.util.Set;

/**
 * Throw this exception when you want to let error be handled by the framework, usually in cases where the business
 * functionality has critically failed.
 * By using this exception, you can send information to the framework about how to handle the error:
 * - With the message you can declare a specific message to be displayed to the user either as a string or a OMFLog.
 * - With the modifiers, you can specify additional actions to be taken by the framework when handling the error.
 */
public class OMFCriticalException extends OMFLogException {
    protected Set<OMFExceptionModifier> modifiers;

    /**
     * Exception without a cause... and a simple message.
     *
     * @param message   the message to be displayed to the user
     * @param modifiers the modifiers to be set on the exception
     */
    public OMFCriticalException(String message, OMFExceptionModifier... modifiers) {
        this(new OMFLog().text(message), modifiers);
    }

    /**
     * Just an exception without a cause...
     *
     * @param message   the message to be displayed to the user
     * @param modifiers the modifiers to be set on the exception
     */
    public OMFCriticalException(OMFLog message, OMFExceptionModifier... modifiers) {
        this(message, null, modifiers);
    }

    /**
     * Simple message
     *
     * @param message   the message to be displayed to the user
     * @param cause     the exception that caused this exception
     * @param modifiers the modifiers to be set on the exception
     */
    public OMFCriticalException(String message, Exception cause, OMFExceptionModifier... modifiers) {
        this(new OMFLog().text(message), cause, modifiers);
    }

    /**
     * Simple message
     *
     * @param message   the message to be displayed to the user
     * @param cause     the exception that caused this exception
     * @param modifiers the modifiers to be set on the exception
     */
    public OMFCriticalException(String message, Throwable cause, OMFExceptionModifier... modifiers) {
        this(new OMFLog().text(message), cause, modifiers);
    }

    /**
     * Full constructor wrapping causing exception
     *
     * @param message   the message to be displayed to the user
     * @param cause     the exception that caused this exception
     * @param modifiers the modifiers to be set on the exception
     */
    public OMFCriticalException(OMFLog message, Throwable cause, OMFExceptionModifier... modifiers) {
        super(message.toString(), cause);
        this.omfLog = message;
        this.modifiers = Set.of(modifiers);
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
