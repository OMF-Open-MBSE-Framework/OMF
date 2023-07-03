package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFeatureRegisteringException;

import java.util.List;

public interface IFeatureItemRegisterer<I extends RegistrableFeatureItem> {

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
