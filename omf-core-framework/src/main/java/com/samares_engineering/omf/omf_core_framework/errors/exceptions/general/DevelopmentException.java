/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.errors.exceptions.general;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;

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
