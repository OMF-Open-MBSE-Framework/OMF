/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.batches;

import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.stereotypes.ExempleTestNouvelleVersionFramework;
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatchLocal;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//        //SmokeTests.class,
//
//        //Creation
//        T3CreatePort.class,
//
//        //Deletion
//        T2DeleteBlock.class,
        ExempleTestNouvelleVersionFramework.class
})
public class LocalBatch extends ATestBatchLocal {

    @Override
    public void initVariable() {
        initZipProject = "init_publicFeatures_Test.mdzip";
        oracleZipProject = "oracle_publicFeatures_Test.mdzip";
    }

}
