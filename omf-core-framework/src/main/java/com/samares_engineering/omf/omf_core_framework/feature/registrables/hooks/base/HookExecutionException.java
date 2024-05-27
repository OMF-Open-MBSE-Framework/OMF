package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;

public class HookExecutionException extends OMFCriticalException2 {


    public HookExecutionException(String event, Exception cause) {
        super(new OMFLog2().err("[HookExecution - " + event +"] Error while executing hook, due to: " + cause.getMessage()), cause);
    }
}
