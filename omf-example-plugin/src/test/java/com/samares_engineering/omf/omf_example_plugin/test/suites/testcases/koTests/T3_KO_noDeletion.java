/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.koTests;

import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCaseKO;

public class T3_KO_noDeletion extends AModelComparatorTestCaseKO {
@Override
public void initVariables() {
    setName("3. Don't delete port block");
    testCaseID = "T3_KO_noDeletion";
    testPackageName = "3. Don't delete port block";
}

    @Override
    public void initOptions() {}

    @Override
    public void testAction() {
        Port port = (Port) findTestedElementByID("_2021x_2_500b0813_1704450558241_12615_2904");
        SysMLFactory.getInstance().removeElement(port);
    }

    @Override
    public void reInitEnvOptions() {}

}
