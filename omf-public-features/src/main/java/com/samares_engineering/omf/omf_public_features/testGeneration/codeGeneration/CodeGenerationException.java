package com.samares_engineering.omf.omf_public_features.testGeneration.codeGeneration;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;

public class CodeGenerationException extends LegacyOMFException {

    public CodeGenerationException(String errorMsg, ECriticality criticality) {
        super(errorMsg, criticality);
    }

    public CodeGenerationException(String errorMsg, Exception exception, ECriticality criticality) {
        super(errorMsg, exception, criticality);
    }

    public CodeGenerationException(String debugMessage, String userMessage, ECriticality criticality) {
        super(debugMessage, userMessage, criticality);
    }

    public CodeGenerationException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super(debugMessage, userMessage, exception, criticality);
    }
}
