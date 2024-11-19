/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.deletion;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCase;

public class T2DeleteBlock extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("[B]  Port Deletion");
        testCaseID = "Delete1";
        testPackageName = "1 Delete block";
    }

    @Override
    public void initOptions() {
    }

    @Override
    public void testAction() {
        // Action to test
        Class port = (Class) findTestedElementByID("_2021x_2_302b0611_1670957740478_263727_3439");//http://localhost:9850/refmodel/?ID=_2021x_2_302b0611_1670957740478_263727_3439
        SysMLFactory.getInstance().removeElement(port);
    }

    @Override
    public void reInitEnvOptions() {

    }
}

