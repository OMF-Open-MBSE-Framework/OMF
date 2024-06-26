package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature;

import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;

public interface OnFeatureUnregisteringHook extends FeatureLifeCycleHook {
    /**
     * Trigger the onFeatureUnregistering hook
     * @param feature the feature that is being unregistered
     */
    default void triggerOnFeatureUnregisteringHook(OMFFeature feature){
        executeHook(() -> onFeatureUnregistering(feature), "onFeatureUnregistering");
    }

    /**
     * This method is called when the feature is being unregistered.
     * Developers should implement this method to add custom logic.
     * @param feature the feature that is being unregistered
     */
    void onFeatureUnregistering(OMFFeature feature);


}
