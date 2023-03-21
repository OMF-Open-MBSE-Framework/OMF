/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.batches;

import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.apiserver.T1OpeningProject;
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.apiserver.T2SelectElementInContainmentTree;
import com.samares_engineering.omf.omf_test_framework.templates.ATestBatchLocal;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
        //MDActions
        //ExampleMDAActionsTest.class,

        //Creation
        T1OpeningProject.class,

        //Deletion
        T2SelectElementInContainmentTree.class,
})

public class LocalAPIBatch extends ATestBatchLocal {
    @Override
    public void initVariable() {
        initZipProject = "init_basicSysML_Test.mdzip";
        oracleZipProject = "oracle_basicSysML_Test.mdzip";
    }

    @Override
    public void startBatch() {
        //PROJECT OPENED BY THE FIRST TEST
    }
}
