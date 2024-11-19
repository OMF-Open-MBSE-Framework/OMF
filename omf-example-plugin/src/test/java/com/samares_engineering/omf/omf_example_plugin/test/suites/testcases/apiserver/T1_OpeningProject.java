/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.apiserver;

import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCase;

import java.io.File;

public class T1_OpeningProject extends AModelComparatorTestCase {

//
//    String projectName;
//    String projectPath;

    @Override
    public void initVariables() {
        setName("[B]  Port Deletion");
        testCaseID = "Delete1";
        testPackageName = "1 Delete block";
//
//        projectName = "OMF_DEVELOPING.mdzip";
//        projectPath = "C:\\workspace\\DEV\\SAMARES\\OMF\\omf-example-plugin\\src\\main\\resources\\";

        setOracleNeeded(false);
    }

    @Override
    public void initOptions() {

    }

    @Override
    protected void checkPrecondition() {
    }
//C:\workspace\DEV\SAMARES\OMF\omf-example-plugin\build\install\plugins\com.samares.omf.plugin.test\projects\
    @Override
    public void testAction() {
        // Create a GET request to "/openProject" with a "projectPath" query parameter
        String projectName = getInitZipProject();
        File projectFile = new File(System.getProperty("tests.resources") + File.separator + projectName);

        getApiTestComponent().openProjectUsingServerAPI(projectFile);
        verifyProjectOpening(projectName);
        setInitProject(getCurrentProject());
    }

    @Override
    public void verifyResults() {
        //ALREADY DONE IN openProjectUsingServerAPI
    }




    @Override
    public void reInitEnvOptions() {

    }



}

