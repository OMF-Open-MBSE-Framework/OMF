package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.mdactions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCase;

public class Ex_Reverse_Direction_Solution extends AModelComparatorTestCase {
    @Override
    public void initVariables() {
        setName("Reverse direction of Proxy Port");
        testCaseID = "Reverse Direction";
        testPackageName = "1 Reverse Direction";
    }

    @Override
    public void initOptions() {

    }

    @Override
    public void testAction() {
        Element elementToTest = findTestedElementByID("_2021x_2_1dd704ae_1681205377498_546375_2818");//http://localhost:9850/refmodel/?ID=_2021x_2_1dd704ae_1681205377498_546375_2818
        triggerBrowserAction(elementToTest, "Reverse Direction", "Test Action");

    }

    @Override
    public void reInitEnvOptions() {

    }
}
