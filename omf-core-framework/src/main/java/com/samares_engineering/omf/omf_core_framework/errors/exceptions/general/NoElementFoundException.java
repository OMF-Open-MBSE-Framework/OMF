/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.errors.exceptions.general;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;

public class NoElementFoundException extends GenericException {
    public NoElementFoundException(String message) {
        super(message);
    }

    public NoElementFoundException(String errorMsg, ECriticality criticality) {
        super(errorMsg, criticality);
    }

    public NoElementFoundException(String errorMsg, Exception exception, ECriticality criticality) {
        super(errorMsg, exception, criticality);
    }

    public NoElementFoundException(String debugMessage, String userMessage, ECriticality criticality) {
        super(debugMessage, userMessage, criticality);
    }

    public NoElementFoundException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super("[No Element Found] " + debugMessage, userMessage, exception, criticality);
    }
}
