package com.samares.omf.core.feature;

/**
 * Describes an item like an option or action that can be registered in a feature.
 */
public interface RegistrableFeatureItem {
    MDFeature getFeature();
    void setFeature(MDFeature feature);
}
