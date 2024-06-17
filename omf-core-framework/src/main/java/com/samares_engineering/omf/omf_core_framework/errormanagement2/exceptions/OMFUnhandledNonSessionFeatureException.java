package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exception for non-handled errors insides features, which should not be rolled back.
 */
public class OMFUnhandledNonSessionFeatureException extends OMFCriticalException {
    /**
     * Exception without a cause... and a simple message.
     */
    public OMFUnhandledNonSessionFeatureException(String message, OMFExceptionModifier... modifiers) {
        this(new OMFLog().text(message), modifiers);
    }

    /**
     * Just an exception without a cause...
     */
    public OMFUnhandledNonSessionFeatureException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, OMFExceptionModifier... modifiers) {
        this(message, null, modifiers);
    }


    public OMFUnhandledNonSessionFeatureException(Exception uncauchtException) {
        this(new OMFLog().text("Unhandled exception in feature: Unknown"), uncauchtException);
    }
    public OMFUnhandledNonSessionFeatureException(MDFeature feature, Exception uncauchtException) {
        this(new OMFLog().text("Unhandled exception in feature: " + feature.getName()), uncauchtException);
    }

    public OMFUnhandledNonSessionFeatureException(OMFDevException devException, Set<OMFExceptionModifier> modifiers) {
        this(new OMFLog().text("Unhandled exception in feature: Unknown"), devException, modifiers);
    }

    public OMFUnhandledNonSessionFeatureException(MDFeature feature, OMFDevException devException, Set<OMFExceptionModifier> modifiers) {
        this(new OMFLog().text("Unhandled exception in feature: " + feature.getName()), devException, modifiers);
    }
    /**
     * Simple message
     */
    public OMFUnhandledNonSessionFeatureException(String message, Exception cause, OMFExceptionModifier... modifiers) {
        this(new OMFLog().text(message), cause, modifiers);
    }

    /**
     * Simple message
     */
    public OMFUnhandledNonSessionFeatureException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, Exception cause, Set<OMFExceptionModifier> modifiers) {
        this(message, cause, modifiers.toArray(new OMFExceptionModifier[0]));
    }


    /**
     * Full constructor wrapping causing exception
     */
    public OMFUnhandledNonSessionFeatureException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, Exception cause, OMFExceptionModifier... modifiers) {
        super(message.toString(), cause);
        OMFLog = message;
        this.modifiers = new HashSet<>(List.of(modifiers));
        this.modifiers.add(OMFExceptionModifier.NO_ROLLBACK);
    }


}
