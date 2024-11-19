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
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_example_plugin.test.utils.StereotypesTestUtils;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCase;

import java.util.List;

public class T5_InstanceHWComponantToHWComponentPart_InBlock extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("5.Instance HWComponent instantiation in Block");
        testCaseID = "instanceHWComponant_To_HWComponentPart_InBlock ";
        testPackageName = "5.Instance HWComponent instantiation in Block";
    }

    @Override
    public void initOptions() {
        // Set instance config csv file
        String configFolder = StereotypesTestUtils.getStereotypeConfigFolder();
        String instanceConfigFilePath = configFolder + "/instance_config.csv";
        StereotypesTestUtils.getStereotypesFeature().getOptionsHelper().setInstanceConfigFilePath(instanceConfigFilePath);
        StereotypesTestUtils.getStereotypesFeature().getOptionsHelper().setInstanceActivated(true);
    }

    @Override
    public void testAction() {}

    @Override
    public List<Runnable> testActions() {
        List<Runnable> userActions = List.of(
                this::triggerRefreshStereotypesRulesBasedOnConfigFiles,
                this::createPart
        );

        return userActions;
    }

    private void triggerRefreshStereotypesRulesBasedOnConfigFiles() {
        triggerBrowserAction(OMFUtils.getProject().getPrimaryModel(), "OMF", "Refresh stereotypes rules based on config files" );
    }

    private void createPart() {
        openDiagram("_2021x_2_da1032a_1685091603433_926561_5780"); // IBD genericOwner
        Class HWComponent = (Class) findTestedElementByID("_2021x_2_da1032a_1685091603432_933690_5777");
        Class genericOwner = (Class) findTestedElementByID("_2021x_2_da1032a_1685091603431_353698_5775");
        Property HWComponentPart = SysMLFactory.getInstance().createPartProperty(genericOwner, HWComponent);
        HWComponentPart.setVisibility(VisibilityKindEnum.PUBLIC);
    }


    @Override
    public void reInitEnvOptions() {
        StereotypesTestUtils.resetStereotypesConfigFilePathEnvOptions();
    }
}

