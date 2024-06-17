/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.errors;


import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.BaseOMFException;

public class OMFTestFrameworkException extends BaseOMFException {

    public OMFTestFrameworkException(String message) {
        super(message);
    }

    public OMFTestFrameworkException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message) {
        super(message);
    }

    public OMFTestFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public OMFTestFrameworkException(com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog message, Throwable cause) {
        super(message, cause);
    }
}