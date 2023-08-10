package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer;

import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.RegistrableFeatureItem;

import java.util.List;

public interface FeatureItemRegisterer<I extends RegistrableFeatureItem> {

    void init(FeatureRegisterer featureRegisterer);
    void registerFeatureItems(List<I> item);
    void unregisterFeatureItems(List<I> mdFeature);

    void registerFeatureItem(I item);
    void unregisterFeatureItem(I item);
    void registerFeatureItems(MDFeature feature);
    void unregisterFeatureItems(MDFeature feature);

    FeatureRegisterer getFeatureRegisterer();

    void setFeatureRegisterer(FeatureRegisterer featureRegisterer);
}
