/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.utils;

import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_example_plugin.OpenMBSEFrameworkPlugin;
import com.samares_engineering.omf.omf_example_plugin.options.OMFPluginEnvOptionsGroup;
import com.samares_engineering.omf.omf_test_framework.errors.OMFTestFrameworkException;
import com.samares_engineering.omf.omf_test_framework.utils.TestHelper;

public class TestUtils {
    private TestUtils() {}

    public static OpenMBSEFrameworkPlugin getOpenMBSEFrameworkPlugin() {
        try {
            return (OpenMBSEFrameworkPlugin) TestHelper.findTestedPluginInstance(OpenMBSEFrameworkPlugin.class);
        } catch (OMFTestFrameworkException e) {
            OMFErrorHandler.handleException(e, true);
        }
        return null;
    }

    public static OMFPluginEnvOptionsGroup getEnvOptions() {
        return (OMFPluginEnvOptionsGroup) getOpenMBSEFrameworkPlugin().getEnvironmentOptionsGroup();
    }
}
