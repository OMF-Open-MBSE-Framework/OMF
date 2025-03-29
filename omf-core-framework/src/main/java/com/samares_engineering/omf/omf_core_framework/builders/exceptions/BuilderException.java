/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.builders.exceptions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.builders.generic.IGenericBuilder;

public class BuilderException extends RuntimeException {
    public BuilderException(String message, IGenericBuilder builder) {
        super("[Builder - " + getNameFromBuilder(builder) + "] " + message);
    }

    public BuilderException(String message, Element createdElement, String builderName) {
        super("[Builder - " + builderName + "] " + message + "\n on created element: " + createdElement.getHumanName());
    }

    private static String getNameFromBuilder(IGenericBuilder builder) {
        String builderName = builder.getClass().getName();
        String[] tmp = builderName.split("\\.");
        return tmp[tmp.length - 1];
    }
}
