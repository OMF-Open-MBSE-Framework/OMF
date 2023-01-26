/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */

package com.samares.omf.plugin.test.suite.md_actions;


import com.samares.omf.test.templates.MDActionsBrowserMenuTestCase;


public class ExampleMDAActionsTest extends MDActionsBrowserMenuTestCase {

    @Override
    public void initVariables() {
        setName("Example MDActions Test");
        testCaseID = "Action1";
        testPackageName = "Browser action name";
        elementToTestID = "_2021x_2_1dd704ae_1661505164201_456002_108641";
        actionToTestName = "Browser action name";
        mdActionsCategoryName = "Feature";
    }

    @Override
    public void initEnvOptions() {

    }

    @Override
    public void reInitEnvOptions() {

    }


}

