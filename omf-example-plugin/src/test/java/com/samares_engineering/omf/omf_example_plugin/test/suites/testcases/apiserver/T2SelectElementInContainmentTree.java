/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.apiserver;

import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCase;

public class T2SelectElementInContainmentTree extends AModelComparatorTestCase {



    @Override
    public void initVariables() {
        setName("[API] Select Block in Containment Tree");
        testCaseID = "API2";
        testPackageName = "T2 Select Block in Containment Tree";
        setOracleNeeded(false);
    }

    @Override
    public void initOptions() {

    }

    @Override
    public void testAction() {
        // Create a GET request to "/openProject" with a "projectPath" query parameter
        getApiTestComponent().selectElementInContainmentTree("_2021x_2_302b0611_1679329161433_622224_2811");
    }


    @Override
    public void verifyResults() {
        // Verify that the response content contains the expected project path
        getApiTestComponent().verifyResultElementSelection("_2021x_2_302b0611_1679329161433_622224_2811");

    }


    @Override
    public void reInitEnvOptions() {

    }



}

