/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.projectmanager;

public class TestCloseProjects extends AbstractTestProject {

    @Override
    public void initVariables() {
        setName("[F] Closing project Creation");
        testCaseID = "Closing Project";
    }

    @Override
    public void initEnvOptions() {

    }

    @Override
    public void testAction() {
        loggerTest.log("- [START] Loading projects:");
        if(oracleNeeded)
            loggerTest.log("* " + oracleProject.getName());
        loggerTest.log("* " + initProject.getName());
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
        assertNull("initProject is not closed", initProject);
        assertNull("oracleProject is not closed", oracleProject);
    }

    protected void closeProjects() {
        loggerTest.log("- [END] closingProjects");
        if(oracleProject != null)
            closeProject(oracleProject);
        if(initProject != null)
            closeProject(initProject);
    }

}

