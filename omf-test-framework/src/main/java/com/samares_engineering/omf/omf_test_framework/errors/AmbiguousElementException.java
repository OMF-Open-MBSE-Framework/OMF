package com.samares_engineering.omf.omf_test_framework.errors;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;

public class AmbiguousElementException extends GenericException {
    public AmbiguousElementException(String message) {
        super(message);
    }

    public AmbiguousElementException(String message, Element createdElement, String featureName) {
        super(message, createdElement, featureName);
    }

    public AmbiguousElementException(String errorMsg, ECriticality criticality) {
        super(errorMsg, criticality);
    }

    public AmbiguousElementException(String errorMsg, Exception exception, ECriticality criticality) {
        super(errorMsg, exception, criticality);
    }

    public AmbiguousElementException(String debugMessage, String userMessage, ECriticality criticality) {
        super(debugMessage, userMessage, criticality);
    }

    public AmbiguousElementException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super(debugMessage, userMessage, exception, criticality);
    }
}
