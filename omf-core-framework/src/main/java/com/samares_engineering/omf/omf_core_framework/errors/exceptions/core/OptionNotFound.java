package com.samares_engineering.omf.omf_core_framework.errors.exceptions.core;

public class OptionNotFound extends LegacyOMFException {
    public OptionNotFound(String optionPropertyName) {
        super("Option " + optionPropertyName + " not found", ECriticality.CRITICAL);
    }
}
