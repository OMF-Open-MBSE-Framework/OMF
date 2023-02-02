/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.suites.testcases.stereotypes;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares.omf.core.factory.SysMLFactory;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;
import com.samares.omf.plugin.test.utils.StereotypesTestUtils;
import com.samares.omf.test.templates.AbstractModelComparatorTestCase;

public class ExampleStereotypesTest extends AbstractModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("Example stereotypes test");
        testCaseID = "Stereo1";
        testPackageName = "Example stereotypes test case";
    }

    @Override
    public void initEnvOptions() {
        String configFolder = StereotypesTestUtils.getStereotypeConfigFolder();

        // Set instance config csv file
        String instanceConfigFilePath = configFolder + "/stereotypes_example_test/instance_config.csv";
        setEnvironmentOptionValueByGroupName(OMFPluginEnvOptionsGroup.INSTANCE_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.INSTANCE_CONFIG_FILE_PATH_ID, instanceConfigFilePath);

        // Set type config csv file
        String typeConfigFilePath = configFolder + "/stereotypes_example_test/type_config.csv";
        setEnvironmentOptionValueByGroupName(OMFPluginEnvOptionsGroup.TYPE_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.TYPE_CONFIG_FILE_PATH_ID, typeConfigFilePath);

        // Set organizer config csv file
        String organizerConfigFilePath = configFolder + "/stereotypes_example_test/organizer_config.csv";
        setEnvironmentOptionValueByGroupName(OMFPluginEnvOptionsGroup.ORGANIZER_CONFIG_GRP,
                OMFPluginEnvOptionsGroup.ORGANIZER_CONFIG_FILE_PATH_ID, organizerConfigFilePath);
    }

    @Override
    public void testAction() {
        // Update stereotype rules (as if we clicked on the "Refresh stereotypes rules" button in cameo)
        StereotypesTestUtils.updateAllRulesBasedOnConfigFiles();


    }

    @Override
    public void reInitEnvOptions() {

    }
}

