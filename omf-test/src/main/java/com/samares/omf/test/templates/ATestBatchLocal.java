/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.templates;

import com.samares.omf.test.projectmanager.TestCloseProjects;
import com.samares.omf.test.projectmanager.TestLoadProject;
import com.samares.omf.test.projectmanager.TestSaveProject;

public abstract class ATestBatchLocal extends ATestBatch{

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
