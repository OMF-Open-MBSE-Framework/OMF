/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */

package com.samares.omf.core.errors.exceptions;

public class DevelopmentException extends GenericException {
    public DevelopmentException(String message) {
        super(message);
    }

    public DevelopmentException(String errorMsg, ECriticality criticality) {
        super(errorMsg, criticality);
    }

    public DevelopmentException(String errorMsg, Exception exception, ECriticality criticality) {
        super(errorMsg, exception, criticality);
    }

    public DevelopmentException(String debugMessage, String userMessage, ECriticality criticality) {
        super(debugMessage, userMessage, criticality);
    }

    public DevelopmentException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super("[DEV] " + debugMessage, userMessage, exception, criticality);
    }
}
