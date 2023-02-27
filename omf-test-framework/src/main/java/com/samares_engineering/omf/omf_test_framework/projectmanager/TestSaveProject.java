/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.projectmanager;

import com.google.common.base.Strings;
import com.nomagic.magicdraw.esi.EsiUtils;

import java.io.File;

public class TestSaveProject extends AbstractTestProject {

    @Override
    public void initVariables() {
        setName("[F] Simple Port Creation");
        testCaseID         = "";
        testPackageName    = "";
    }

    @Override
    public void initEnvOptions() {

    }

    @Override
    public void testAction() {
        loggerTest.log("- [START] Saving projects:");
        if(oracleNeeded)
            loggerTest.log("* " + oracleProject.getName());
        loggerTest.log("* " + initProject.getName());

        saveModel();
    }

    @Override
    public void reInitEnvOptions() {

    }

    @Override
    protected void checkPrecondition() {
//        ColorPrinter.status("noPrecondition");
    }
    @Override
    public void verifyResults() {
        assertTrue(initProject != null && oracleProject != null);
    }
    protected void saveModel() {
        loggerTest.log("- [SAVING RESULT] Saving test case file: - ");
        loggerTest.log("* " + initProject.getName() + "_save.mdzip");
        File resultTestFile = new File(System.getProperty("tests.resources"), initProject.getName() + "_save.mdzip");
        if(!Strings.isNullOrEmpty(initZipProject)){
            saveProject(initProject, resultTestFile);
        }else{
            EsiUtils.convertToLocal(initProject, resultTestFile);
        }
    }


    @Override
    public void tearDownTest() throws Exception {
        super.tearDownTest();
        testBatch.setInitProject(initProject);
        testBatch.setOracleProject(oracleProject);
    }

}

