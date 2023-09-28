package com.samares_engineering.omf.omf_core_framework.feature.registrables.listener;

import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFCoreException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public abstract class RegisteringPropertyChangeListener implements PropertyChangeListener {

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            String propertyName = evt.getPropertyName();
            if (FeatureRegisteringEventHandler.FEATURE_REGISTERED.equals(propertyName)) {
                featureRegistered(evt);
            } else if (FeatureRegisteringEventHandler.FEATURE_UNREGISTERED.equals(propertyName)) {
                featureUnregistered(evt);
            }
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFCoreException("Error during Feature Registering/Unregistering listener, please investigate", e, GenericException.ECriticality.ALERT), false);
        }
    }

    public abstract void featureUnregistered(PropertyChangeEvent evt) throws OMFException;

    public abstract void featureRegistered(PropertyChangeEvent evt) throws OMFException;
}
