package com.samares.omf.core.feature;

import java.util.List;

public abstract class FeatureItemRegisterer<I extends RegistrableFeatureItem> {
    protected FeatureRegisterer featureRegisterer;

    protected FeatureItemRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }

    protected abstract void register(MDFeature mdFeature);

    protected abstract void unregister(MDFeature mdFeature);

    protected abstract void register(I item);
    protected abstract void unregister(I item);
}
