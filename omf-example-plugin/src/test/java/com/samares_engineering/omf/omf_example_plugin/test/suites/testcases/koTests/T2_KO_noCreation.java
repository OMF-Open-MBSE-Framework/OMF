/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.koTests;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCaseKO;

public class T2_KO_noCreation extends AModelComparatorTestCaseKO {
@Override
public void initVariables() {
    setName("2. Don't create port block");
    testCaseID = "T2_KO_noCreation";
    testPackageName = "2. Don't create port block";
}

    @Override
    public void initOptions() {}

    @Override
    public void testAction() {
        Class blockOwner = (Class) findTestedElementByID("_2021x_2_500b0813_1704450434048_389862_2902");
        Port myPort = SysMLFactory.getInstance().createProxyPort(blockOwner);
        myPort.setName("myPort");
    }

    @Override
    public void reInitEnvOptions() {}


}
