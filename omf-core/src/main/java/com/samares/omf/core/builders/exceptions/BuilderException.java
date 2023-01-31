/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.builders.exceptions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.builders.generic.IGenericBuilder;
import com.samares.omf.core.errors.exceptions.GenericException;

public class BuilderException extends GenericException {
    private String message;
    private Element createdElement;


    public BuilderException(String message, IGenericBuilder builder) {
        super("[Builder - " + getNameFromBuilder(builder) + "] " + message);
        this.message = message;
    }

    public BuilderException(String message, Element createdElement, String builderName) {
        super("[Builder - " + builderName + "] " + message + "\n on created element: " + createdElement.getHumanName());
        this.message = message;
        this.createdElement = createdElement;
    }

    private static String getNameFromBuilder(IGenericBuilder builder) {
        String builderName = builder.getClass().getName();
        String[] tmp = builderName.split("\\.");
        return tmp[tmp.length - 1];
    }
}
