/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.batches;

import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.portcreation.T3CreatePort;
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.deletion.T2DeleteBlock;
import com.samares_engineering.omf.omf_test_framework.templates.ATestBatchLocal;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
        SmokeTests.class,

        //MDActions
        //ExampleMDAActionsTest.class,

        //Creation
        T3CreatePort.class,


        //Deletion
        T2DeleteBlock.class,

})
public class LocalBatch extends ATestBatchLocal {

    @Override
    public void initVariable() {
        initZipProject = "init_basicSysML_Test.mdzip";
        oracleZipProject = "oracle_basicSysML_Test.mdzip";
    }

}
