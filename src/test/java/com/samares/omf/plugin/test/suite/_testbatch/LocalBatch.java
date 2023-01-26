/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */

package com.samares.omf.plugin.test.suite._testbatch;

import com.samares.omf.plugin.test.suite.SmokeTests;
import com.samares.omf.plugin.test.suite.basicsysml_test.deletion.T_2_Delete_block;
import com.samares.omf.plugin.test.suite.basicsysml_test.portcreation.T3_CreatePort;
import com.samares.omf.plugin.test.suite.md_actions.ExampleMDAActionsTest;
import com.samares.omf.test.templates.ATestBatchLocal;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
        SmokeTests.class,

        //MDActions
        ExampleMDAActionsTest.class,

        //Creation
        T3_CreatePort.class,


        //Deletion
        T_2_Delete_block.class,

})
public class LocalBatch extends ATestBatchLocal {

    @Override
    public void initVariable() {
        initZipProject = "init_basicSysML_Test.mdzip";
        oracleZipProject = "oracle_basicSysML_Test.mdzip";
    }

}
