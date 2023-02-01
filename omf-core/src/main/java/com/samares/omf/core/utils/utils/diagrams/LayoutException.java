/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.utils.utils.diagrams;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.errors.exceptions.GenericException;

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
}
