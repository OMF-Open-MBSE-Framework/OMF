/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errors.exceptions;

public class OMFUserSilentException extends GenericException {

    public OMFUserSilentException(String errorMsg, ECriticality criticality) {
        this(errorMsg, null, criticality);
    }

    public OMFUserSilentException(String errorMsg, Exception exception, ECriticality criticality) {
        super(errorMsg, "", exception, criticality);
    }

}
