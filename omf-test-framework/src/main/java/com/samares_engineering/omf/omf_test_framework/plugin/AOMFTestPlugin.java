package com.samares_engineering.omf.omf_test_framework.plugin;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.PluginRegisteringException;
import com.samares_engineering.omf.omf_core_framework.plugin.AOMFPlugin;

public abstract class AOMFTestPlugin extends AOMFPlugin {
    @Override
    public void init() {
        try {
            //OMFLogger.init(this); // TODO Won't work as conflict with logger & handler from the base plugin.
            //OMFErrorHandler.init(this);
        } catch (Exception e) {
            throw new PluginRegisteringException("Error occurred during error management initialization", e);
        }

        try {
            configurePlugin();
            onPluginInit(); // Call the overridable on plugin init hook
        } catch (Exception e) {
            OMFErrorHandler.getInstance().handleException(new PluginRegisteringException("Error occurred during Plugin Initialization", e));
        }
    }
}
