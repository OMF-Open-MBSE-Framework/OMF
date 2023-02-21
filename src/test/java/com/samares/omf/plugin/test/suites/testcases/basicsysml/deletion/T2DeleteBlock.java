/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.suites.testcases.basicsysml.deletion;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares.omf.core.factory.SysMLFactory;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;
import com.samares.omf.test.templates.AModelComparatorTestCase;

public class T2DeleteBlock extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("[B]  Port Deletion");
        testCaseID = "Delete1";
        testPackageName = "1 Delete block";
    }

    @Override
    public void initEnvOptions() {
        OMFPluginEnvOptionsGroup.getInstance().setAutomationsActivated(true);
    }

    @Override
    public void testAction() {
        // Action to test
        Class port = (Class) findTestedElementByID("_2021x_2_302b0611_1670957740478_263727_3439");
        SysMLFactory.getInstance().removeElement(port);
    }

    @Override
    public void reInitEnvOptions() {

    }
}

