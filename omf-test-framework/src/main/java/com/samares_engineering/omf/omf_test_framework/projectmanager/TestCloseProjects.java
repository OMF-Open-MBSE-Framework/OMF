/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.projectmanager;

public class TestCloseProjects extends AbstractTestProject {

    @Override
    public void initVariables() {
        setName("[F] Closing project Creation");
        testCaseID = "Closing Project";
    }

    @Override
    public void initOptions() {

    }

    @Override
    public void testAction() {
        getLoggerTest().log("- [START] Loading projects:");
        if(oracleNeeded)
            getLoggerTest().log("* " + getOracleProject().getName());
        getLoggerTest().log("* " + getInitProject().getName());
        closeProjects();
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
        assertNull("initProject is not closed", getInitProject());
        assertNull("oracleProject is not closed", getOracleProject());
    }

    protected void closeProjects() {
        getLoggerTest().log("- [END] closingProjects");
        if(isOracleProjectOpened())
            closeProject(getOracleProject());
        if(isInitProjectOpened())
            closeProject(getInitProject());

    }



}

