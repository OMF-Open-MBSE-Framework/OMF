package com.samares_engineering.omf.omf_core_framework.errors.exceptions.core;

public class CoreException extends LegacyOMFException {
    public CoreException(String message, Exception originalException, ECriticality eCriticality) {
        super("[OMFCore] " + message, originalException, eCriticality);
    }

    @Override
    public void displayUserMessage() {
        //No display to the user?
    }


}
