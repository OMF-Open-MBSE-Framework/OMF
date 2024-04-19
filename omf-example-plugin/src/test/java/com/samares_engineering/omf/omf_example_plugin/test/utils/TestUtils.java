/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.utils;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_example_plugin.OMFExamplePlugin;
import com.samares_engineering.omf.omf_test_framework.utils.TestHelper;

public class TestUtils {

    public static OMFExamplePlugin getOpenMBSEFrameworkPlugin() {
        return (OMFExamplePlugin) TestHelper.findTestedPluginInstance(OMFExamplePlugin.class);
    }

    public static OMFPropertyOptionsGroup getEnvOptions() {
        return getOpenMBSEFrameworkPlugin().getEnvironmentOptionsGroup()
                    .orElseThrow(() -> new OMFCriticalException2("No environment options groups have been declared" +
                    "for this plugin"));
    }
}
