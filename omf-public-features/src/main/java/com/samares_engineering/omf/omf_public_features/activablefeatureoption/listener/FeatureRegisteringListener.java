package com.samares_engineering.omf.omf_public_features.activablefeatureoption.listener;

import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.CoreException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OptionNotFound;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
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
        OMFFeature feature = (OMFFeature) evt.getOldValue();
        try {
            if(feature == null) return;
            Property optionProperty = getOptionFromFeature(feature);
            if(optionProperty == null) throw new OptionNotFound(feature.getName());
            optionProperty.setValue(false);
        }catch (OptionNotFound e) {
            LegacyErrorHandler.handleException(new CoreException("Cannot actualize :" + feature.getName() + ". The related option was not found...", e, GenericException.ECriticality.ALERT), false);
        }catch (Exception unknown) {
            OMFLogger.err(new OMFLog().err("Cannot actualize :" + feature.getName() + ". An unknown error occurred...")
                    .expandText(new OMFLog().err(unknown.getMessage())));
        }
    }

    /**
     * This method is called when a feature is registered.
     * It will update the option value accordingly, so that the option is always in sync with the feature status.
     * @param evt the event
     * @throws LegacyOMFException if the option is not found
     */
    @Override
    public void featureRegistered(PropertyChangeEvent evt) throws LegacyOMFException {
        OMFFeature feature = null;
        try {
            feature = (OMFFeature) evt.getNewValue();
            if (feature == null) return;
            Property optionProperty = getOptionFromFeature(feature);
            if (optionProperty == null) throw new OptionNotFound(feature.getName());
            optionProperty.setValue(true);
        } catch (OptionNotFound e) {
            LegacyErrorHandler.handleException(new CoreException("Cannot actualize :" + ((OMFFeature) evt.getNewValue()).getName() + ". The related option was not found...", e, GenericException.ECriticality.ALERT), false);
        } catch (Exception unknown) {
            throw new LegacyOMFException("Cannot actualize :" + feature.getName() + ". An unknown error occurred...", unknown, GenericException.ECriticality.CRITICAL);
        }
    }

    /**
     * This method is used to get the option from a feature
     * @param feature the feature
     * @return the option
     * @throws OptionNotFound if the option is not found
     */
    private Property getOptionFromFeature(OMFFeature feature) throws OptionNotFound {
        if(activationFeature.getEnvOptionsHelper() == null) return null;
        return activationFeature.getEnvOptionsHelper()
                .getOptionFromFeature(feature);
    }

}
