package com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;

public class OptionNotFound extends OMFException {
    public OptionNotFound(String optionPropertyName) {
        super("Option " + optionPropertyName + " not found", ECriticality.CRITICAL);
    }
}
