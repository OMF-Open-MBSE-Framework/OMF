/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.batches;

import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.SmokeTests;
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.deletion.T2DeleteBlock;
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatchTWC;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        SmokeTests.class,

        //Creation
//        T3CreatePort.class, //NOT WORKING

        //Deletion
        T2DeleteBlock.class,

//        ExampleStereotypesTest.class //NOT WORKING
})

public class TWCBatch extends ATestBatchTWC {
    @Override
    public void initVariable() {
        // Connection to TWC server
        serverAddress   = System.getProperty("serverIp");
        serverUser      = System.getProperty("userName");
        userPassword    = System.getProperty("userPwd");
        projectInitName   = "init_basicSysML_Test";
        projectOracleName = "oracle_basicSysML_Test";

        super.initVariable();
    }
}