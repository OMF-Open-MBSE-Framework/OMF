package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer;

import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.RegistrableFeatureItem;

import java.util.List;

public interface FeatureItemRegisterer<I extends RegistrableFeatureItem> {

    void init(FeatureRegisterer featureRegisterer);
    void registerFeatureItems(List<I> items);
    void unregisterFeatureItems(List<I> items);

    void registerFeatureItem(I item);
    void unregisterFeatureItem(I item);
    void registerFeatureItems(OMFFeature feature);
    void unregisterFeatureItems(OMFFeature feature);

    FeatureRegisterer getFeatureRegisterer();

    List<I> getRegisteredFeatureItems();

    void setFeatureRegisterer(FeatureRegisterer featureRegisterer);
}
