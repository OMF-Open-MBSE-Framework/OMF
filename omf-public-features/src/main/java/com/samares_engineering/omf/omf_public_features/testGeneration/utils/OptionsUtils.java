package com.samares_engineering.omf.omf_public_features.testGeneration.utils;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.AbstractPropertyOptionsGroup;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;

import java.util.List;

public class OptionsUtils {

    /**
     * Return all the property related to a given group id in environment option
     * @param groupId the id of the Option group
     * @return the options as a list of properties
     */
    public static List<Property> getEnvOptionProperties(String groupId) {
        try {
            return ((AbstractPropertyOptionsGroup) Application.getInstance().getEnvironmentOptions().getGroup(groupId))
                    .getOptions()
                    .getProperties();
        }
        catch (Exception e) {
            throw new OMFCriticalException("Unable to find the \"" + groupId + "\" group.", e);
        }
    }

    /**
     * Return the property belonging to the provided group with the provided id
     * @param groupId the id of the Option group
     * @param propertyId  the id of the property
     * @return the property
     */
    public static Property getEnvOptionProperty(String groupId, String propertyId) {
        try {

            return ((AbstractPropertyOptionsGroup) Application.getInstance().getEnvironmentOptions().getGroup(groupId))
                    .getProperty(propertyId);
        }
        catch (Exception e) {
            throw new OMFCriticalException("Unable to find the \"" + groupId + "\" group or the \"" + propertyId + "\" property.", e);
        }
    }

}
