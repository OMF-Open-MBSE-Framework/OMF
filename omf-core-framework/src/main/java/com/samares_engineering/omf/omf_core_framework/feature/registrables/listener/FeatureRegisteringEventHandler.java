package com.samares_engineering.omf.omf_core_framework.feature.registrables.listener;

import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class FeatureRegisteringEventHandler {
    private final PropertyChangeSupport support;
    public static final String FEATURE_REGISTERED = "featureRegistered";
    public static final String FEATURE_UNREGISTERED = "featureUnregistered";
    private final FeatureRegisterer featureRegisterer;

    public FeatureRegisteringEventHandler(FeatureRegisterer featureRegisterer) {
        this.support = new PropertyChangeSupport(this);
        this.featureRegisterer = featureRegisterer;
    }

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        this.support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        this.support.removePropertyChangeListener(pcl);
    }

    public void fireFeatureRegistered(OMFFeature feature) {

        support.firePropertyChange(FEATURE_REGISTERED, null, feature);
    }

    public void fireFeatureUnregistered(OMFFeature feature) {
        support.firePropertyChange(FEATURE_UNREGISTERED, feature, null);
    }

    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegisterer;
    }
}

