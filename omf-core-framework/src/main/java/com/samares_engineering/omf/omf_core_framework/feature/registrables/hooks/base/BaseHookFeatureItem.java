package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base;

import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

public class BaseHookFeatureItem implements IHook {
    private MDFeature feature;
    private boolean activated;

    public MDFeature getFeature() {
        return feature;
    }

    public void initRegistrableItem(MDFeature feature) {
        this.feature = feature;
    }

    public void activate() {
        activated = true;
    }

    public void deactivate() {
        activated = false;
    }

    public boolean isActivated() {
        return activated;
    }

    @Override
    public void executeHook(Runnable runnable, String event) {
        try {
            runnable.run();
        } catch (OMFCriticalException2 cause) {
            ErrorHandler2.getInstance().handleException(new HookExecutionException(event, cause), getFeature());
        } catch (RuntimeException e) {
            ErrorHandler2.getInstance().handleException(e, getFeature());
        }
    }
    @Override
    public void executeInSessionHook(Runnable runnable, String event) {
        try {
            SessionManager.getInstance().executeInsideSession(OMFUtils.getProject(), "Hook call", runnable);
        } catch (OMFCriticalException2 cause) {
            ErrorHandler2.getInstance().handleException(new HookExecutionException(event, cause), getFeature());
        } catch (RuntimeException e) {
            ErrorHandler2.getInstance().handleException(e, getFeature());
        }
    }
}
