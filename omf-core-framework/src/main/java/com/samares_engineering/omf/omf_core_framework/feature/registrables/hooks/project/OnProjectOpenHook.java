package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.api.BaseHookFeatureItem;

public abstract class OnProjectOpenHook extends BaseHookFeatureItem implements TriggerOnProjectOpenHook {

    @Override
    public final void triggerOnProjectOpenHook() {
        try {
            onProjectOpened();
        } catch (OMFCriticalException2 e) {
            ErrorHandler2.getInstance().handleException(e, getFeature());
        } catch (RuntimeException e) {
            ErrorHandler2.getInstance().handleException(e, getFeature());
        }
    }

    public abstract void onProjectOpened();

}
