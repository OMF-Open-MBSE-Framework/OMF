/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errors.exceptions;

public class OMFException extends GenericException {

    public OMFException(String errorMsg, ECriticality criticality) {
        this(errorMsg, errorMsg, null, criticality);
    }

    public OMFException(String errorMsg, Exception exception, ECriticality criticality) {
        this(errorMsg, errorMsg, exception, criticality);
    }

    public OMFException(String debugMessage, String userMessage, ECriticality criticality) {
        this(debugMessage, userMessage, null, criticality);
    }

    public OMFException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super(debugMessage, userMessage, exception, criticality);
    }
}
