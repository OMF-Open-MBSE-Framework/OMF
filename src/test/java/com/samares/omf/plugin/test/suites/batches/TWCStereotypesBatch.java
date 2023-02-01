/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.suites.batches;

import com.samares.omf.test.templates.TestBatchTWC;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
})

public class TWCStereotypesBatch extends TestBatchTWC {
    @Override
    public void initVariable() {
        // Connection to TWC server
        serverAddress   = System.getProperty("serverIp");
        serverUser      = System.getProperty("userName");
        userPassword    = System.getProperty("userPwd");
        projectInitName   = "init_basicSysML_Test";
        projectOracleName = "oracle_basicSysML_Test";

        super.initVariable();
    }

}