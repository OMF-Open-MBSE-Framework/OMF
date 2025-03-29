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

import java.util.List;

public class T1_BasicSysML_MultiActionsBlockCreation extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("1.Create and rename a block in several user actions");
        testCaseID = "T1_BasicSysML_MultiActionsBlockCreation";
        testPackageName = "1.Create and rename a block in several user actions";
    }

    @Override
    public void initOptions() {}

    @Override
    public void testAction() {}

    @Override
    public List<Runnable> testActions() {
        List<Runnable> userActions = List.of(
                this::createClass,
                this::renameBlock);

        return userActions;
    }

    private void createClass() {
        Element owner = findTestedElementByName("1.Create and rename a block in several user actions"); // test package
//        Element owner = findTestedElementByID("_2021x_2_da1032a_1686310024566_293572_2914"); // test package
        Class myBlock = SysMLFactory.getInstance().createBlock(owner);
        elementsStoredInTestCase.storeElement(myBlock, "createdBlock");
    }

    private void renameBlock() {
        Class myBlock = (Class) elementsStoredInTestCase.getStoredElement("createdBlock");
        myBlock.setName("MyBlock");
    }

    @Override
    public void reInitEnvOptions() {}


}

