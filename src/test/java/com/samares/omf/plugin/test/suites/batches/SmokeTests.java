/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.suites.batches;


import com.nomagic.magicdraw.plugins.PluginUtils;
import com.nomagic.magicdraw.tests.MagicDrawTestCase;
import com.samares.omf.plugin.OpenMBSEFrameworkPlugin;
import org.junit.Assume;
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

        PluginUtils.getPlugins().stream()
                .filter(s -> s instanceof OpenMBSEFrameworkPlugin).findFirst()
                .ifPresent(p -> plugin = (OpenMBSEFrameworkPlugin) p);
    }


    @Test
    public void checkIfPluginIsInstalled() {
        assertNotNull("Plugin is null", plugin);
    }

    @Test
    public void checkIfPluginIsInitialized() {
        Assume.assumeTrue(plugin != null);
        assertTrue("PLUGIN NOT INITIALIZED", OpenMBSEFrameworkPlugin.getInstance().isInitialized());
    }
}

