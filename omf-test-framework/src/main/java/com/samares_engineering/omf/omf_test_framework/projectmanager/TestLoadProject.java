/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.projectmanager;

import com.google.common.base.Strings;
import org.junit.Assert;

import java.io.File;

public class TestLoadProject extends AbstractTestProject {


    @Override
    public void initVariables() {
        setName("[F] Loading project Creation");
        testCaseID = "Loading Project";
    }

    @Override
    public void initOptions() {

    }

    @Override
    public void testAction() {
        getLoggerTest().log("- [START] Loading projects:");

        if(Strings.isNullOrEmpty(getInitZipProject()) || Strings.isNullOrEmpty(getOracleZipProject()))
            Assert.fail("");

        loadProjects();
        getTestBatch().setInitProject(getInitProject());
        getTestBatch().setOracleProject(getOracleProject());

        if(oracleNeeded)
            getLoggerTest().log("* " + getOracleZipProject());
        getLoggerTest().log("* " + getInitZipProject());

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
        assertTrue(!isInitProjectOpened() && isOracleProjectOpened());
    }

    public void loadProjects() {
        if(isOracleNeeded() && !Strings.isNullOrEmpty(getOracleZipProject()))
            setOracleProject(loadProject(new File(System.getProperty("tests.resources"), getOracleZipProject()).getAbsolutePath()));

        if(!Strings.isNullOrEmpty(getInitZipProject()))
            setInitProject(loadProject(new File(System.getProperty("tests.resources"), getInitZipProject()).getAbsolutePath()));
    }

    @Override
    public void tearDownTest() throws Exception {
        super.tearDownTest();
        getTestBatch().setInitProject(getInitProject());
        getTestBatch().setOracleProject(getOracleProject());
    }
}

