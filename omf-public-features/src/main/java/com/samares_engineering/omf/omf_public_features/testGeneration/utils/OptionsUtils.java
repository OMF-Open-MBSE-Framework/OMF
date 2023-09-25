package com.samares_engineering.omf.omf_public_features.testGeneration.utils;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;

import java.util.List;

public class OptionsUtils {

    /**
     * Return all the property related to a given group id in environment option
     * @param groupId
     * @return
     */
    public static List<Property> getEnvOptionProperties(String groupId) {
        try {
            return ((AbstractPropertyOptionsGroup) Application.getInstance().getEnvironmentOptions().getGroup(groupId))
                    .getOptions()
                    .getProperties();
        }
        catch (Exception e) {
            OMFErrorHandler.handleException(new OMFException("Unable to find the \"" + groupId + "\" group.", GenericException.ECriticality.ALERT), true);
            return null;
        }
    }

    /**
     * Return the property belonging to the provided group with the provided id
     * @param groupId
     * @param propertyId
     * @return
     */
    public static Property getEnvOptionProperty(String groupId, String propertyId) {
        try {

            return ((AbstractPropertyOptionsGroup) Application.getInstance().getEnvironmentOptions().getGroup(groupId))
                    .getProperty(propertyId);
        }
        catch (Exception e) {
            OMFErrorHandler.handleException(new OMFException("Unable to find the \"" + groupId + "\" group or the \"" + propertyId + "\" property.", GenericException.ECriticality.ALERT), true);
            return null;
        }
    }

}
