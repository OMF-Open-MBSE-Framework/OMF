/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.koTests;

import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCaseKO;

public class T5_KO_recursivelyOwned  extends AModelComparatorTestCaseKO {

@Override
public void initVariables() {
    setName("5. Different recursively owned elements");
    testCaseID = "T5_KO_recursivelyOwned";
    testPackageName = "5. Different recursively owned elements";
}

    @Override
    public void initOptions() {}

    @Override
    public void testAction() {
    }

    @Override
    public void reInitEnvOptions() {}

}
