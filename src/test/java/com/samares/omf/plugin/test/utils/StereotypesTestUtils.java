/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.utils;

import com.samares.omf.plugin.features.stereotypes.StereotypesRuleUpdater;

public class StereotypesTestUtils {
    private StereotypesTestUtils() {}

    /**
     * Refreshes stereotype config files. Paths are set in corresponding environment variables
     */
    static void updateAllRulesBasedOnConfigFiles() {
        StereotypesRuleUpdater.getInstance().updateAllRulesBasedOnConfigFiles();
    }

    /**
     * Get path of test plugin resources dir
     */
    static String getStereotypeConfigFolder() {
        return System.getProperty("user.dir") + "/plugins/com.samares.omf.test.plugin/resources";
    }
}
