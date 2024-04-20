package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature;

import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;

public interface IOnFeatureUnregisteringHook extends IFeatureLifeCycleHook{
    /**
     * Trigger the onFeatureUnregistering hook
     * @param feature the feature that is being unregistered
     */
    default void triggerOnFeatureUnregisteringHook(MDFeature feature){
        executeHook(() -> onFeatureUnregistering(feature), "onFeatureUnregistering");
    }

    /**
     * This method is called when the feature is being unregistered.
     * Developers should implement this method to add custom logic.
     * @param feature the feature that is being unregistered
     */
    void onFeatureUnregistering(MDFeature feature);


}
