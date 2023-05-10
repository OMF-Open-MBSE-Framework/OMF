package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.feature.errors.FeatureException;

import java.util.List;

public interface IFeatureItemRegisterer<I extends RegistrableFeatureItem> {

    void init(FeatureRegisterer featureRegisterer) throws FeatureException;
    void registerFeatureItems(List<I> item) throws FeatureException;
    void unregisterFeatureItems(List<I> mdFeature) throws FeatureException;

    void registerFeatureItem(I item);
    void unregisterFeatureItem(I item);
    void registerFeature(MDFeature feature) throws FeatureException;
    void unregisterFeature(MDFeature feature) throws FeatureException;

    FeatureRegisterer getFeatureRegisterer();

    void setFeatureRegisterer(FeatureRegisterer featureRegisterer);
}
