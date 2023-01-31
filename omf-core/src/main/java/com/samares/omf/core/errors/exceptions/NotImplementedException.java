/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */
package com.samares.omf.core.errors.exceptions;

public class NotImplementedException extends OMFException {

    public NotImplementedException(String errorMsg, GenericException.ECriticality criticality){
        this(errorMsg, errorMsg, null,  criticality);
    }

    public NotImplementedException(String errorMsg, Exception exception, GenericException.ECriticality criticality){
        this(errorMsg, errorMsg, exception, criticality);
    }

    public NotImplementedException(String debugMessage, String userMessage, GenericException.ECriticality criticality){
        this(debugMessage, userMessage, null, criticality);
    }

    public NotImplementedException(String debugMessage, String userMessage, Exception exception, GenericException.ECriticality criticality){
        super("[Not Implemented Yet]- " + debugMessage, userMessage, exception, criticality);
    }
}
