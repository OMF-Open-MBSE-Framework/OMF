/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.utils;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesEnvOptionsHelper;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesFeature;

public class StereotypesTestUtils {
    private StereotypesTestUtils() {}

    /**
     * Get path of test plugin resources dir
     */
    public static String getStereotypeConfigFolder() {
        return OMFUtils.getUserDir() + "/plugins/com.samares_engineering.omf.omf_example_plugin.test/resources/stereotypes_example_test";
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
        return (StereotypesFeature) TestUtils.getOpenMBSEFrameworkPlugin().getFeatureByName(StereotypesFeature.FEATURE_NAME)
                .orElseThrow(() -> new OMFCriticalException("No feature named " + StereotypesFeature.FEATURE_NAME
                        + " has been found in the plugin"));
    }
}
