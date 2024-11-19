/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.errors;


import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;

public class OMFTestFrameworkException extends OMFLogException {

    public OMFTestFrameworkException(String message) {
        super(message);
    }

    public OMFTestFrameworkException(OMFLog message) {
        super(message);
    }

    public OMFTestFrameworkException(String message, Throwable cause) {
        super(message, cause);
    }

    public OMFTestFrameworkException(OMFLog message, Throwable cause) {
        super(message, cause);
    }
}