package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.api;

import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;

public class BaseHookFeatureItem {
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
}
