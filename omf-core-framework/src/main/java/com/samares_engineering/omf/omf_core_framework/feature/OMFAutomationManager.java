package com.samares_engineering.omf.omf_core_framework.feature;

public class OMFAutomationManager {
    private boolean hasAutomationBeenTriggered;
    private static  OMFAutomationManager instance;
    //Singleton
    public static OMFAutomationManager getInstance() {
        if (instance == null) {
            instance = new OMFAutomationManager();
        }
        return instance;
    }

    public boolean hasAutomationBeenTriggered() {
        return this.hasAutomationBeenTriggered;
    }
    public boolean noAutomationTriggered() {
        return !hasAutomationBeenTriggered;
    }

    public void setHasAutomationBeenTriggered(boolean hasAutomationBeenTriggered) {
        this.hasAutomationBeenTriggered = hasAutomationBeenTriggered;
    }
    public void automationTriggered() {
        this.hasAutomationBeenTriggered = true;
    }
    public void resetAutomationTriggered() {
        this.hasAutomationBeenTriggered = false;
    }
}
