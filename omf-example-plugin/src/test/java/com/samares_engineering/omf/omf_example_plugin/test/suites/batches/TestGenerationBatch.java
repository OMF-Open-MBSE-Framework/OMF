package com.samares_engineering.omf.omf_example_plugin.test.suites.batches;

import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.testGeneration.TestPack;
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatchLocal;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        TestPack.class,
})
public class TestGenerationBatch extends ATestBatchLocal {

    @Override
    public void initVariable() {
        initZipProject = "init_testGeneration_Test.mdzip";
        oracleZipProject = "oracle_testGeneration_Test.mdzip";
    }

}

