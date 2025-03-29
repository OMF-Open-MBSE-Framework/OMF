package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature;

import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;

public interface OnFeatureRegisteringHook extends FeatureLifeCycleHook {

    /**
     * NOTE: This method is called by the framework, do not call it directly or override it.
     * This method is used to trigger the hook when the feature is registered.
     * @param feature the feature being registered
     */
    default void triggerOnFeatureRegisteringHook(OMFFeature feature) {
        executeHook(() -> onFeatureRegistering(feature), "onFeatureRegistering");
    }

    /**
     * This method is called when the feature is being registered.
     * Developers should implement this method to add custom logic.
     * @param feature the feature being registered
     */
     void onFeatureRegistering(OMFFeature feature);
}
