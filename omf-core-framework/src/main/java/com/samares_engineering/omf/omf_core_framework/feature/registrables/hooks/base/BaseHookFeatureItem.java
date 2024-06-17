package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

public class BaseHookFeatureItem implements IHook {
    private MDFeature feature;
    private boolean activated = true;

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

    public APlugin getPlugin() {
        return getFeature().getPlugin();
    }

    @Override
    public void executeHook(Runnable runnable, String event) {
        try {
            runnable.run();
        } catch (OMFCriticalException cause) {
            ErrorHandler.getInstance().handleException(new HookExecutionException(event, cause), getFeature());
        } catch (RuntimeException e) {
            ErrorHandler.getInstance().handleException(e, getFeature());
        }
    }
    @Override
    public void executeInSessionHook(Runnable runnable, String event) {
        OMFBarrierExecutor.executeInSessionWithinBarrier(runnable, event, getFeature());
    }
}
