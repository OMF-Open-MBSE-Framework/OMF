/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.utils;

import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.plugin.OpenMBSEFrameworkPlugin;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;
import com.samares.omf.test.errors.OMFTestFrameworkException;
import com.samares.omf.test.utils.TestHelper;

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
