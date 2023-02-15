/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.utils;

import com.samares.omf.plugin.features.stereotypes.StereotypesFeature;
import com.samares.omf.plugin.features.stereotypes.StereotypesRuleUpdater;
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
        OMFPluginEnvOptionsGroup.getInstance().setInstanceConfigFilePath(
                OMFPluginEnvOptionsGroup.getInstanceConfigFilePathDefaultValue());
        OMFPluginEnvOptionsGroup.getInstance().setTypeConfigFilePath(
                OMFPluginEnvOptionsGroup.getTypeConfigFilePathDefaultValue());
        OMFPluginEnvOptionsGroup.getInstance().setOrganizerConfigFilePath(
                OMFPluginEnvOptionsGroup.getOrganizerConfigFilePathDefaultValue());
    }
}
