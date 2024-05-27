package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Exception for non-handled errors insides features, which should not be rolled back.
 */
public class OMFUnhandledNonSessionFeatureException extends OMFCriticalException2 {
    /**
     * Exception without a cause... and a simple message.
     */
    public OMFUnhandledNonSessionFeatureException(String message, OMFExceptionModifier2... modifiers) {
        this(new OMFLog2().text(message), modifiers);
    }

    /**
     * Just an exception without a cause...
     */
    public OMFUnhandledNonSessionFeatureException(OMFLog2 message, OMFExceptionModifier2... modifiers) {
        this(message, null, modifiers);
    }


    public OMFUnhandledNonSessionFeatureException(Exception uncauchtException) {
        this(new OMFLog2().text("Unhandled exception in feature: Unknown"), uncauchtException);
    }
    public OMFUnhandledNonSessionFeatureException(MDFeature feature, Exception uncauchtException) {
        this(new OMFLog2().text("Unhandled exception in feature: " + feature.getName()), uncauchtException);
    }

    public OMFUnhandledNonSessionFeatureException(OMFDevException devException, Set<OMFExceptionModifier2> modifiers) {
        this(new OMFLog2().text("Unhandled exception in feature: Unknown"), devException, modifiers);
    }

    public OMFUnhandledNonSessionFeatureException(MDFeature feature, OMFDevException devException, Set<OMFExceptionModifier2> modifiers) {
        this(new OMFLog2().text("Unhandled exception in feature: " + feature.getName()), devException, modifiers);
    }
    /**
     * Simple message
     */
    public OMFUnhandledNonSessionFeatureException(String message, Exception cause, OMFExceptionModifier2... modifiers) {
        this(new OMFLog2().text(message), cause, modifiers);
    }

    /**
     * Simple message
     */
    public OMFUnhandledNonSessionFeatureException(OMFLog2 message, Exception cause, Set<OMFExceptionModifier2> modifiers) {
        this(message, cause, modifiers.toArray(new OMFExceptionModifier2[0]));
    }


    /**
     * Full constructor wrapping causing exception
     */
    public OMFUnhandledNonSessionFeatureException(OMFLog2 message, Exception cause, OMFExceptionModifier2... modifiers) {
        super(message.toString(), cause);
        OMFLog = message;
        this.modifiers = new HashSet<>(List.of(modifiers));
        this.modifiers.add(OMFExceptionModifier2.NO_ROLLBACK);
    }


}
