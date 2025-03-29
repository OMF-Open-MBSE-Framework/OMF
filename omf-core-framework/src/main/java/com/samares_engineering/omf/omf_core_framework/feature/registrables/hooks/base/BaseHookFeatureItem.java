package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;

public class BaseHookFeatureItem implements Hook {
    private OMFFeature feature;
    private boolean activated = true;

    public OMFFeature getFeature() {
        return feature;
    }

    public void initRegistrableItem(OMFFeature feature) {
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

    public OMFPlugin getPlugin() {
        return getFeature().getPlugin();
    }

    @Override
    public void executeHook(Runnable runnable, String event) {
        OMFBarrierExecutor.executeWithinBarrier(runnable, getFeature(), shallDeactivateListener());

    }
    @Override
    public void executeInSessionHook(Runnable runnable, String event, boolean deactivateListener) {
        OMFBarrierExecutor.executeInSessionWithinBarrier(runnable, event, getFeature(), shallDeactivateListener());
    }


}
