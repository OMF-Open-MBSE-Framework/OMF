/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errors.exceptions;

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
