package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;

/**
 * Describes an item like an option or action that can be registered in a feature.
 */
public interface RegistrableFeatureItem {
    OMFFeature getFeature();
    default OMFPlugin getPlugin(){
        return getFeature().getPlugin();
    }
    void initRegistrableItem(OMFFeature feature);

    void activate();

    void deactivate();

    boolean isActivated();
}
