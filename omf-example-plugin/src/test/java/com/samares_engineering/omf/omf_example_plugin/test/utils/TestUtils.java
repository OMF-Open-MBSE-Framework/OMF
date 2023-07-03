/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.utils;

import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_example_plugin.OMFExamplePlugin;
import com.samares_engineering.omf.omf_test_framework.errors.OMFTestFrameworkException;
import com.samares_engineering.omf.omf_test_framework.utils.TestHelper;

public class TestUtils {

    public static OMFExamplePlugin getOpenMBSEFrameworkPlugin() {
        try {
            return (OMFExamplePlugin) TestHelper.findTestedPluginInstance(OMFExamplePlugin.class);
        } catch (OMFTestFrameworkException e) {
            OMFErrorHandler.handleException(e, true);
        }
        return null;
    }

    public static OMFPropertyOptionsGroup getEnvOptions() {
        return getOpenMBSEFrameworkPlugin().getEnvironmentOptionsGroup();
    }
}
