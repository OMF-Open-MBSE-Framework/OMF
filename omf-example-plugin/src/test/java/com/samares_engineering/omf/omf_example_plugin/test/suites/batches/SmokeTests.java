/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.batches;


import com.nomagic.magicdraw.tests.MagicDrawTestCase;
import com.samares_engineering.omf.omf_example_plugin.OpenMBSEFrameworkPlugin;
import com.samares_engineering.omf.omf_test_framework.utils.TestHelper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;

public class SmokeTests extends MagicDrawTestCase {
    OpenMBSEFrameworkPlugin plugin;

    /**
     * Rule to calculate/show time for each testcase
     */
    @Rule
    public TestRule watcher = new TestWatcher() {
        private long start;

        protected void starting(Description description) {
            getLogger().info("TESTS JUNIT - Starting test: " + description.getMethodName());
            start = System.currentTimeMillis();
        }

        @Override
        protected void finished(Description description) {
            long end = System.currentTimeMillis();
            getLogger().info("TESTS JUNIT - Test " + description.getMethodName() + " took " + (end - start) + "ms");
        }
    };

    @Before
    public void setUpTest() throws Exception {
        setSkipMemoryTest(true);
        super.setUpTest();
        plugin = (OpenMBSEFrameworkPlugin) TestHelper.findTestedPluginInstance(OpenMBSEFrameworkPlugin.class);
    }

    @Test
    public void checkIfPluginIsInstalled() {
        assertNotNull("Can't find test plugin", plugin);
    }
}

