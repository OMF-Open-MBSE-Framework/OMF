package com.samares_engineering.omf.omf_example_plugin.test.suites.batches;

import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.testGeneration.CreateProxyPort;
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatchLocal;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        CreateProxyPort.class,
})
public class TestGenerationBatch extends ATestBatchLocal {

    @Override
    public void initVariable() {
        initZipProject = "init_basicSysML_Test.mdzip";
        oracleZipProject = "oracle_basicSysML_Test.mdzip";
    }

}

