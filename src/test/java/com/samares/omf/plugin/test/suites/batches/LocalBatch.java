/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */

package com.samares.omf.plugin.test.suites.batches;

import com.samares.omf.plugin.test.suites.testcases.basicsysml.portcreation.T3_CreatePort;
import com.samares.omf.plugin.test.suites.testcases.basicsysml.deletion.T_2_Delete_block;
import com.samares.omf.test.templates.ATestBatchLocal;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
        SmokeTests.class,

        //MDActions
        //ExampleMDAActionsTest.class,

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
