/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.templates.batches;

import com.samares_engineering.omf.omf_test_framework.projectmanager.TestCloseProjects;
import com.samares_engineering.omf.omf_test_framework.projectmanager.TestLoadProject;
import com.samares_engineering.omf.omf_test_framework.projectmanager.TestSaveProject;

public abstract class ATestBatchLocal extends ATestBatch {

    @Override
    public void startBatch() {
        new TestLoadProject().testAction();
    }

    @Override
    public void endBatch(boolean shallSaveModel) {
        if(shallSaveModel)
            new TestSaveProject().testAction();
        new TestCloseProjects().testAction();
    }
}
