package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFLogException;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;

public class HookExecutionException extends OMFLogException {
    public HookExecutionException(String event, Exception cause) {
        super(new OMFLog().err("[HookExecution - " + event +"] Error while executing hook, due to: " + cause.getMessage()), cause);
    }
}
