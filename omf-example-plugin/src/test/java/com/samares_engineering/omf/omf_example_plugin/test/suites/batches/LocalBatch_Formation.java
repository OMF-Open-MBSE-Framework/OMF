/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.batches;

import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.portcreation.Ex_Port_Creation_Solution;
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.mdactions.Ex_Reverse_Direction_Solution;
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatchLocal;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
        //SmokeTests.class,

        //Creation
        Ex_Port_Creation_Solution.class,

        //Update
        Ex_Reverse_Direction_Solution.class,
})
public class LocalBatch_Formation extends ATestBatchLocal {

    @Override
    public void initVariable() {
        initZipProject = "Ex_Test_Auto_Init.mdzip";
        oracleZipProject = "Ex_Test_Auto_Final.mdzip";
    }

}
