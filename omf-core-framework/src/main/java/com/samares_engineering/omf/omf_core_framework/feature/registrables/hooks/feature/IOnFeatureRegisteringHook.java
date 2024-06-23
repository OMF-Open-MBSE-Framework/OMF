package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature;

import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;

public interface IOnFeatureRegisteringHook extends FeatureLifeCycleHook {

    /**
     * NOTE: This method is called by the framework, do not call it directly or override it.
     * This method is used to trigger the hook when the feature is registered.
     */
    default void triggerOnFeatureRegisteringHook(OMFFeature feature) {
        executeHook(() -> onFeatureRegistering(feature), "onFeatureRegistering");
    }

    /**
     * This method is called when the feature is being registered.
     * Developers should implement this method to add custom logic.
     */
     void onFeatureRegistering(OMFFeature feature);
}
