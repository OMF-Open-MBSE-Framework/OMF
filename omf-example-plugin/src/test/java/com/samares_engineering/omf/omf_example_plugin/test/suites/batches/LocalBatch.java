/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.batches;

import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.blockCreation.T1_BasicSysML_MultiActionsBlockCreation;
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.blockCreation.T2_BasicSysML_SingleActionBlockCreation;
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.portcreation.T3CreatePort;
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.koTests.*;
import com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.stereotypes.*;
import com.samares_engineering.omf.omf_test_framework.templates.batches.ATestBatchLocal;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({
//        //SmokeTests.class,
//
//        //Creation
//        T3CreatePort.class,
//
//        //Deletion
//        T2DeleteBlock.class,

//        // BasicSysML
        T1_BasicSysML_MultiActionsBlockCreation.class,
        T2_BasicSysML_SingleActionBlockCreation.class,
        T3CreatePort.class,

        // Tests KOs
        T1_KO_wrongBlockName.class,
        T2_KO_noCreation.class,
        T3_KO_noDeletion.class,
        T4_KO_wrongInnerElements.class,
        T5_KO_recursivelyOwned.class,
        T6_KO_sameElementNames.class,
        T7_KO_differentsOwnedElements.class,

        // Stereotypes Instance
        T1_InstanceFunctionToFctPart_InBlock.class,
        T2_InstanceComponentToComponentPart_InBlock.class,
        T3_InstanceMetamorphtToFctPart_InFunction.class,
        T5_InstanceHWComponantToHWComponentPart_InBlock.class,
        T4_InstanceMetamorphtToComponentPart_InComponent.class
})
public class LocalBatch extends ATestBatchLocal {

    @Override
    public void initVariable() {
        initZipProject = "init_publicFeatures_Test.mdzip";
        oracleZipProject = "oracle_publicFeatures_Test.mdzip";
    }

}
