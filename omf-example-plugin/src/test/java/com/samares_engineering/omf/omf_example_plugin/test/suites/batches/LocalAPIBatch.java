/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.batches;

import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.apiserver.T1_OpeningProject;
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.apiserver.T2SelectElementInContainmentTree;
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatch;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
        //Creation
        T1_OpeningProject.class,

        //Deletion
        T2SelectElementInContainmentTree.class,
})
public class LocalAPIBatch extends ATestBatch {

    @Override
    public void initVariable() {
    }

    @Override
    public void startBatch() {
    }

    @Override
    public void endBatch(boolean shallSaveModel) {
    }
}
