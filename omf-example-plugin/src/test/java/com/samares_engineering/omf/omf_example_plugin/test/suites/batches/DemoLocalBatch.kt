/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.test.suites.batches

import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.blockCreation.T1_BasicSysML_MultiActionsBlockCreation
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.portcreation.T3CreatePort
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.koTests.T1_KO_wrongBlockName
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.koTests.T2_KO_noCreation
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.koTests.T3_KO_noDeletion
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCase
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatchLocal
import org.junit.runner.RunWith
import org.junit.runners.Suite

// Test suite for the demo local batch
@RunWith(Suite::class)
@Suite.SuiteClasses( // BasicSysML
    T0_BasicSysML_SimpleBlockCreation::class,
    T1_BasicSysML_MultiActionsBlockCreation::class,
    T3CreatePort::class // Tests KOs

    ,
    T1_KO_wrongBlockName::class,
    T2_KO_noCreation::class,
    T3_KO_noDeletion::class

)
// Emplacement of the init and oracle projects
class DemoLocalBatch : ATestBatchLocal() {
    override fun initVariable() {
        initZipProject = "init_publicFeatures_Test.mdzip"
        oracleZipProject = "oracle_publicFeatures_Test.mdzip"
    }
}

// Simulate Block creation by user, in test package
class T0_BasicSysML_SimpleBlockCreation : AModelComparatorTestCase() {
    override fun initVariables() {
        // Test case name
        // Test case ID
        // Test package where the test is conducted
        name =            "2.Create a block in a single user action"
        testCaseID =      "T2_BasicSysML_SingleActionBlockCreation"
        testPackageName = "2.Create a block in a single user action"
    }

    // Which Magicdraw options should be set to run the test
    override fun initOptions() {}

    // Simulate the action to be tested (in this case, a block creation)
    // Could be a UI action call, an element creation to activate listener, etc.
    override fun testAction() {
        val owner = findTestedElementByID("_2021x_2_da1032a_1686310758915_84973_2922") // test package
        val myBlock = SysMLFactory.getInstance().createBlock(owner)
        myBlock.name = "MyBlock"
    }

    // MagicDraw options to be set after the test
    override fun reInitEnvOptions() {}
}