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

public class T4_InstanceHWComponantToHWComponentPart_InBlock extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("5.Instance HWComponent instantiation in Block");
        testCaseID = "instanceHWComponant_To_HWComponentPart_InBlock ";
        testPackageName = "5.Instance HWComponent instantiation in Block";
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
        openDiagram("_2021x_2_da1032a_1685091944491_146235_3162"); // IBD functionalPart
        Class metamorph = (Class) findTestedElementByID("_2021x_2_da1032a_1685091944372_437736_2886");
        Class component = (Class) findTestedElementByID("_2021x_2_da1032a_1685091944372_112571_2887");
        Property functionalPart = SysMLFactory.getInstance().createPartProperty(component, metamorph);
        functionalPart.setVisibility(VisibilityKindEnum.PUBLIC);
    }


    @Override
    public void reInitEnvOptions() {
        StereotypesTestUtils.resetStereotypesConfigFilePathEnvOptions();
    }
}

