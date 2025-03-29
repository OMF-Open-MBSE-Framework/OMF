/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;

public class LayoutException extends GenericException {
    public LayoutException(String message) {
        super(message);
    }

    public LayoutException(String message, String featureName) {
        super(message, null, featureName);
    }

    public LayoutException(String message, Element createdElement, String featureName) {
        super(message, createdElement, featureName);
    }

    public LayoutException(String errorMsg, Exception exception) {
        super(errorMsg, exception, ECriticality.ALERT);
    }
}
