/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.projectmanager;

import com.google.common.base.Strings;
import com.nomagic.magicdraw.tests.common.TestEnvironment;
import org.junit.Assert;

import java.io.File;

public class TestLoadProject extends AbstractTestProject {


    @Override
    public void initVariables() {
        setName("[F] Loading project Creation");
        testCaseID = "Loading Project";
    }

    @Override
    public void initEnvOptions() {

    }

    @Override
    public void testAction() {
        loggerTest.log("- [START] Loading projects:");

        if(Strings.isNullOrEmpty(initZipProject) || Strings.isNullOrEmpty(oracleZipProject))
            Assert.fail("");

        loadProjects();
        testBatch.setInitProject(initProject);
        testBatch.setOracleProject(oracleProject);

        if(oracleNeeded)
            loggerTest.log("* " + oracleZipProject);
        loggerTest.log("* " + initZipProject);

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

    public void loadProjects() {
        if(oracleNeeded && !Strings.isNullOrEmpty(oracleZipProject))
            oracleProject = loadProject(new File(System.getProperty("tests.resources"), oracleZipProject).getAbsolutePath());

        if(!Strings.isNullOrEmpty(initZipProject))
            initProject = loadProject(new File(System.getProperty("tests.resources"), initZipProject).getAbsolutePath());
    }

    @Override
    public void tearDownTest() throws Exception {
        super.tearDownTest();
        testBatch.setInitProject(initProject);
        testBatch.setOracleProject(oracleProject);
    }
}

