/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.koTests;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCaseKO;

public class T1_KO_wrongBlockName extends AModelComparatorTestCaseKO {
    @Override
    public void initVariables() {
        setName("1. Change block name with wrong name");
        testCaseID = "T1_KO_wrongBlockName";
        testPackageName = "1. Change block name with wrong name";
    }

    @Override
    public void initOptions() {}

    @Override
    public void testAction() {
        Class myBlock = (Class) findTestedElementByName("NomInitial");
//        Class myBlock = (Class) findTestedElementByID("_2021x_2_500b0813_1704449272988_256237_2902");
        myBlock.setName("NouveauNom2");
    }

    @Override
    public void reInitEnvOptions() {}


}
