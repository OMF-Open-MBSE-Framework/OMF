/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.stereotypes;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.VisibilityKindEnum;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_example_plugin.test.utils.StereotypesTestUtils;
import com.samares_engineering.omf.omf_example_plugin.test.utils.TestUtils;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCase;

public class T3_InstanceMetamorphtToFctPart_InFunction extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("3.Instance Metamorph instantiation in Function");
        testCaseID = "instanceMetamorpht_To_FctPart_InFunction";
        testPackageName = "3.Instance Metamorph instantiation in Function";
    }

    @Override
    public void initOptions() {
        TestUtils.getEnvOptions().setAutomationsActivated(true);
        //Will be uncommented after it works in simple test
        String configFolder = StereotypesTestUtils.getStereotypeConfigFolder();

        // Set instance config csv file
        String instanceConfigFilePath = configFolder + "/instance_config.csv";
        StereotypesTestUtils.getStereotypesFeature().getOptionsHelper().setInstanceConfigFilePath(instanceConfigFilePath);
        StereotypesTestUtils.getStereotypesFeature().getOptionsHelper().setInstanceActivated(true);
    }

    @Override
    public void testAction() {
        triggerBrowserAction(OMFUtils.currentProject.getPrimaryModel(), "Stereotypes", "Refresh stereotypes rules based on config files" );
        openDiagram("_2021x_2_da1032a_1685091944450_435357_3066"); // IBD function
        Class metamorph = (Class) findTestedElementByID("_2021x_2_da1032a_1685091944371_141946_2879");
        Class function = (Class) findTestedElementByID("_2021x_2_da1032a_1685091944371_341927_2877");
        Property functionalPart = SysMLFactory.getInstance().createPartProperty(function, metamorph);
        functionalPart.setVisibility(VisibilityKindEnum.PUBLIC);
    }


    @Override
    public void reInitEnvOptions() {
        StereotypesTestUtils.resetStereotypesConfigFilePathEnvOptions();
    }
}

