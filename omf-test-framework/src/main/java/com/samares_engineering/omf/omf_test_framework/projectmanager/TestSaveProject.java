/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.projectmanager;

import com.google.common.base.Strings;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.esi.EsiUtils;
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatch;

import java.io.File;

public class TestSaveProject extends AbstractTestProject {

    @Override
    public void initVariables() {
        setName("[F] Simple Port Creation");
        testCaseID         = "";
        testPackageName    = "";
        oracleNeeded = isOracleProjectOpened();
    }

    @Override
    public void initOptions() {

    }

    @Override
    public void testAction() {
        getLoggerTest().log("- [START] Saving projects:");
        if(isOracleNeeded()) {
            getLoggerTest().log("* " + getOracleProject().getName());
        }
        getLoggerTest().log("* " + getInitProject().getName());

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
        assertTrue(getInitProject() != null && getOracleProject() != null);
    }
    public void saveModel() {
        getLoggerTest().log("- [SAVING RESULT] Saving test case file: - ");
        Project initProject = getInitProject();
        getLoggerTest().log("* " + initProject.getName() + "_save.mdzip");
        File resultTestFile = new File(System.getProperty("tests.resources"), initProject.getName() + "_save.mdzip");
        if(!Strings.isNullOrEmpty(getInitZipProject())){
            saveProject(initProject, resultTestFile);
        }else{
            EsiUtils.convertToLocal(initProject, resultTestFile);
        }
    }


    @Override
    public void tearDownTest() throws Exception {
        super.tearDownTest();
        ATestBatch testBatch = getTestBatch();
        getTestBatch().setInitProject(getInitProject());
        getTestBatch().setOracleProject(getOracleProject());
    }

}

