/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errors.exceptions.core;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;

public class LegacyOMFException extends GenericException {

    public LegacyOMFException(String errorMsg, ECriticality criticality) {
        this(errorMsg, errorMsg, null, criticality);
    }

    public LegacyOMFException(String errorMsg, Exception exception, ECriticality criticality) {
        this(errorMsg, errorMsg, exception, criticality);
    }

    public LegacyOMFException(String debugMessage, String userMessage, ECriticality criticality) {
        this(debugMessage, userMessage, null, criticality);
    }

    public LegacyOMFException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super(debugMessage, userMessage, exception, criticality);
    }
}