/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.koTests;

import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCaseKO;

public class T4_KO_wrongInnerElements extends AModelComparatorTestCaseKO {

@Override
public void initVariables() {
    setName("4. Different inner elements");
    testCaseID = "T4_KO_wrongInnerElements";
    testPackageName = "4. Different inner elements";
}

    @Override
    public void initOptions() {}

    @Override
    public void testAction() {
    }

    @Override
    public void reInitEnvOptions() {}

}
