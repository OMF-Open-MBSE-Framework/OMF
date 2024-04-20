package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature;

import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;

public interface IOnFeatureRegisteringHook extends IFeatureLifeCycleHook {

    /**
     * NOTE: This method is called by the framework, do not call it directly or override it.
     * This method is used to trigger the hook when the feature is registered.
     */
    default void triggerOnMagicDrawStartHook(MDFeature feature) {
        executeHook(() -> onFeatureRegistering(feature), "onFeatureRegistering");
    }

    /**
     * This method is called when the feature is being registered.
     * Developers should implement this method to add custom logic.
     */
     void onFeatureRegistering(MDFeature feature);
}
