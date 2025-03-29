/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.stereotypes;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.VisibilityKindEnum;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_example_plugin.test.utils.StereotypesTestUtils;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCase;

/**
 * This example test creates a property part under a block & typed by a block, and then checks if instance automations
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
    public void initOptions() {
        String configFolder = StereotypesTestUtils.getStereotypeConfigFolder();

        // Set instance config csv file
        String instanceConfigFilePath = configFolder + "/stereotypes_example_test/instance_config.csv";
        StereotypesTestUtils.getStereotypesFeature().getOptionsHelper().setInstanceConfigFilePath(instanceConfigFilePath);
        StereotypesTestUtils.getStereotypesFeature().getOptionsHelper().setInstanceActivated(true);


        // Set type config csv file
        String typeConfigFilePath = configFolder + "/stereotypes_example_test/type_config.csv";
        StereotypesTestUtils.getStereotypesFeature().getOptionsHelper().setTypeConfigFilePath(typeConfigFilePath);
        StereotypesTestUtils.getStereotypesFeature().getOptionsHelper().setTypeActivated(false);

        // Set organizer config csv file
        String organizerConfigFilePath = configFolder + "/stereotypes_example_test/organizer_config.csv";
        StereotypesTestUtils.getStereotypesFeature().getOptionsHelper().setOrganizerConfigFilePath(organizerConfigFilePath);
        StereotypesTestUtils.getStereotypesFeature().getOptionsHelper().setOrganizerActivated(false);
    }

    @Override
    public void testAction() {
        // Update stereotype rules (as if we clicked on the "Refresh stereotypes rules" button in cameo)
        StereotypesTestUtils.getStereotypesFeature().getRuleUpdater().updateAllRulesBasedOnConfigFiles();

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

