package com.samares.omf.core.feature;

import com.samares.omf.core.feature.errors.FeatureException;

import java.util.List;

public abstract class FeatureItemRegisterer<I extends RegistrableFeatureItem> {
    protected FeatureRegisterer featureRegisterer;

    protected FeatureItemRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }

    protected abstract void registerFeatureItems(List<I> item) throws FeatureException;
    protected abstract void unregisterFeatureItems(List<I> mdFeature) throws FeatureException;

    protected abstract void registerFeatureItem(I item);
    protected abstract void unregisterFeatureItem(I item);
}
