/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.blockCreation;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCase;

public class T2_BasicSysML_SingleActionBlockCreation extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("2.Create a block in a single user action");
        testCaseID = "T2_BasicSysML_SingleActionBlockCreation";
        testPackageName = "2.Create a block in a single user action";
    }

    @Override
    public void initOptions() {}

    @Override
    public void testAction() {
        Element owner = findTestedElementByID("_2021x_2_da1032a_1686310758915_84973_2922"); // test package
        Class myBlock = SysMLFactory.getInstance().createBlock(owner);
        myBlock.setName("MyBlock");
    }

    @Override
    public void reInitEnvOptions() {}


}

