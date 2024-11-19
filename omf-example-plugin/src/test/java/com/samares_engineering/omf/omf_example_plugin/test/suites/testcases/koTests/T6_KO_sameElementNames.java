/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.koTests;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCaseKO;

public class T6_KO_sameElementNames extends AModelComparatorTestCaseKO {
@Override
public void initVariables() {
    setName("6. Get byName when several elements have the same name");
    testCaseID = "T6_KO_sameElementNames";
    testPackageName = "6. Get byName when several elements have the same name";
}

    @Override
    public void initOptions() {}

    @Override
    public void testAction() {
        Class bloc = (Class) findTestedElementByName("name");
        bloc.setName("newName");
    }

    @Override
    public void reInitEnvOptions() {}

}