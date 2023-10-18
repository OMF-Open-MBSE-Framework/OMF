package com.samares_engineering.omf.omf_public_features.activablefeatureoption.listener;

import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFUserSilentException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OptionNotFound;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.listener.RegisteringPropertyChangeListener;
import com.samares_engineering.omf.omf_public_features.activablefeatureoption.FeatureActivationFromOptionFeature;

import java.beans.PropertyChangeEvent;

/**
 * This class is used to listen to the registering/unregistering of a feature
 * It will update the option value accordingly, so that the option is always in sync with the feature status.
 */
public class FeatureRegisteringListener extends RegisteringPropertyChangeListener {
    private final FeatureActivationFromOptionFeature activationFeature;

    public FeatureRegisteringListener(FeatureActivationFromOptionFeature feature) {
        this.activationFeature = feature;

    }

    /**
     * This method is called when a feature is unregistered.
     * It will update the option value accordingly, so that the option is always in sync with the feature status.
     * @param evt the event
     */
    @Override
    public void featureUnregistered(PropertyChangeEvent evt) {
        MDFeature feature = (MDFeature) evt.getOldValue();
        try {
            if(feature == null) return;
            Property optionProperty = getOptionFromFeature(feature);
            if(optionProperty == null) throw new OptionNotFound(feature.getName());
            optionProperty.setValue(false);
        }catch (OptionNotFound e) {
            OMFErrorHandler.handleException(new OMFUserSilentException("Cannot actualize :" + feature.getName() + ". The related option was not found...", e, GenericException.ECriticality.ALERT), false);
        }
    }

    /**
     * This method is called when a feature is registered.
     * It will update the option value accordingly, so that the option is always in sync with the feature status.
     * @param evt the event
     * @throws OMFException if the option is not found
     */
    @Override
    public void featureRegistered(PropertyChangeEvent evt) throws OMFException {
        MDFeature feature = (MDFeature) evt.getNewValue();
        if(feature == null) return;
        Property optionProperty = getOptionFromFeature(feature);
        if(optionProperty == null) throw new OptionNotFound(feature.getName());
        optionProperty.setValue(true);
    }

    /**
     * This method is used to get the option from a feature
     * @param feature the feature
     * @return the option
     * @throws OptionNotFound if the option is not found
     */
    private Property getOptionFromFeature(MDFeature feature) throws OptionNotFound {
        if(activationFeature.getEnvOptionsHelper() == null) return null;
        return activationFeature.getEnvOptionsHelper()
                .getOptionFromFeature(feature);
    }

}
