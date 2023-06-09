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

public class T5_InstanceMetamorphtToComponentPart_InComponent extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("4.Instance Metamorph instantiation in Component");
        testCaseID = "instanceMetamorpht_To_ComponentPart_InComponent";
        testPackageName = "4.Instance Metamorph instantiation in Component";
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
        openDiagram("_2021x_2_da1032a_1685091944498_20075_3178"); // IBD genericOwner
        Class HWComponent = (Class) findTestedElementByID("_2021x_2_da1032a_1685091944373_151848_2892");
        Class genericOwner = (Class) findTestedElementByID("_2021x_2_da1032a_1685091944373_338216_2890");
        Property functionalPart = SysMLFactory.getInstance().createPartProperty(genericOwner, HWComponent);
        functionalPart.setVisibility(VisibilityKindEnum.PUBLIC);
    }


    @Override
    public void reInitEnvOptions() {
        StereotypesTestUtils.resetStereotypesConfigFilePathEnvOptions();
    }
}

