/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.utils;

import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.exceptions.OMFException;
import com.samares.omf.features.stereotypes.StereotypesEnvOptionsHelper;
import com.samares.omf.features.stereotypes.StereotypesFeature;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

public class StereotypesTestUtils {
    private StereotypesTestUtils() {}

    /**
     * Get path of test plugin resources dir
     */
    public static String getStereotypeConfigFolder() {
        return System.getProperty("user.dir") + "/plugins/com.samares.omf.plugin.test/resources";
    }

    public static void resetStereotypesConfigFilePathEnvOptions() {
        getStereotypesFeature().getOptionsHelper().setInstanceConfigFilePath(
                StereotypesEnvOptionsHelper.getInstanceConfigFilePathDefaultValue());
        getStereotypesFeature().getOptionsHelper().setTypeConfigFilePath(
                StereotypesEnvOptionsHelper.getTypeConfigFilePathDefaultValue());
        getStereotypesFeature().getOptionsHelper().setOrganizerConfigFilePath(
                StereotypesEnvOptionsHelper.getOrganizerConfigFilePathDefaultValue());
    }

    /**
     * @return the instance of the stereotypes feature currently loaded in the plugin
     */
    public static StereotypesFeature getStereotypesFeature() {
        try {
            return (StereotypesFeature) TestUtils.getOpenMBSEFrameworkPlugin().getFeatureByName(StereotypesFeature.FEATURE_NAME);
        } catch (OMFException e) {
            OMFErrorHandler.handleException(e, true);
        }
        return null;
    }
}
