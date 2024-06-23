package com.samares_engineering.omf.omf_core_framework.feature;

/**
 * Describes an item like an option or action that can be registered in a feature.
 */
public interface RegistrableFeatureItem {
    OMFFeature getFeature();
    void initRegistrableItem(OMFFeature feature);

    void activate();

    void deactivate();

    boolean isActivated();
}
