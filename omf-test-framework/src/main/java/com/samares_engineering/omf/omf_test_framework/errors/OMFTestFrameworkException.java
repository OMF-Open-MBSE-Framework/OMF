/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.errors;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;

public class OMFTestFrameworkException extends GenericException {
    public OMFTestFrameworkException(String errorMsg, ECriticality criticality) {
        this(errorMsg, errorMsg, null, criticality);
    }

    public OMFTestFrameworkException(String errorMsg, Exception exception, ECriticality criticality) {
        this(errorMsg, errorMsg, exception, criticality);
    }

    public OMFTestFrameworkException(String debugMessage, String userMessage, ECriticality criticality) {
        this(debugMessage, userMessage, null, criticality);
    }

    public OMFTestFrameworkException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        super(debugMessage, userMessage, exception, criticality);
    }

    public OMFTestFrameworkException() {
        super("[TEST Framework exception]");
    }
}