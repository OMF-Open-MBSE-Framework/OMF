/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */

package com.samares.omf.plugin.test.suites.testcases.basicsysml.deletion;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares.omf.core.factory.SysMLFactory;
import com.samares.omf.test.templates.AbstractModelComparatorTestCase;

/**
 * Important write the <Wizard.class> to get the right wizard
 */
public class T2DeleteBlock extends AbstractModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("[B]  Port Deletion");
        testCaseID = "Delete1";
        testPackageName = "1 Delete block";
    }

    @Override
    public void initEnvOptions() {
        setEnvironmentOptionValueByCategoryName("OMF", "Activate OMF Automations", true);
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

