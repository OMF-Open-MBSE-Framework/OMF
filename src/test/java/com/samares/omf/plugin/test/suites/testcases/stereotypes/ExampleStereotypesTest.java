/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.suites.testcases.stereotypes;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.VisibilityKindEnum;
import com.samares.omf.core.factory.SysMLFactory;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;
import com.samares.omf.plugin.test.utils.StereotypesTestUtils;
import com.samares.omf.test.templates.AModelComparatorTestCase;

/**
 * This example test creates a property part under a block & typed by a block, and then checks ifinstance automations
 * have automatically added the block stereotype to it as specified in the provided "instance_config.csv" file for this
 * test case
 */
public class ExampleStereotypesTest extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("Example stereotypes test");
        testCaseID = "Stereo1";
        testPackageName = "Example stereotypes test case";
    }

    @Override
    public void initEnvOptions() {
        OMFPluginEnvOptionsGroup.getInstance().setAutomationsActivated(true);

        String configFolder = StereotypesTestUtils.getStereotypeConfigFolder();

        // Set instance config csv file
        String instanceConfigFilePath = configFolder + "/stereotypes_example_test/instance_config.csv";
        OMFPluginEnvOptionsGroup.getInstance().setInstanceConfigFilePath(instanceConfigFilePath);
        OMFPluginEnvOptionsGroup.getInstance().setInstanceActivated(true);

        // Set type config csv file
        String typeConfigFilePath = configFolder + "/stereotypes_example_test/type_config.csv";
        OMFPluginEnvOptionsGroup.getInstance().setTypeConfigFilePath(typeConfigFilePath);
        OMFPluginEnvOptionsGroup.getInstance().setTypeActivated(false);

        // Set organizer config csv file
        String organizerConfigFilePath = configFolder + "/stereotypes_example_test/organizer_config.csv";
        OMFPluginEnvOptionsGroup.getInstance().setOrganizerConfigFilePath(organizerConfigFilePath);
        OMFPluginEnvOptionsGroup.getInstance().setOrganizerActivated(false);

    }

    @Override
    public void testAction() {
        // Update stereotype rules (as if we clicked on the "Refresh stereotypes rules" button in cameo)
        //TODO get feature
        //feature.getRuleUpdater().updateAllRulesBasedOnConfigFiles();

        // Open IBD
        openDiagram("_2021x_2_1a330482_1675267228353_653606_61");

        Class block = (Class) findTestedElementByID("_2021x_2_1a330482_1675267202944_781082_60");
        Property partProperty = SysMLFactory.getInstance().createPartProperty(block, block);
        partProperty.setVisibility(VisibilityKindEnum.PUBLIC);
    }

    @Override
    public void reInitEnvOptions() {
        StereotypesTestUtils.resetStereotypesConfigFilePathEnvOptions();
    }
}

