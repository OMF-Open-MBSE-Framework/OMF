/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.suites.testcases.basicsysml.portcreation;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_test_framework.templates.AModelComparatorTestCase;

public class T3CreatePort extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("[B] create proxy port - for auto interface creation");
        testCaseID = "Create1";
        testPackageName = "1 create proxy port";
    }

    @Override
    public void initOptions() {
        setEnvironmentOptionValueByID("OMF Example Plugin", "Activate auto InterfaceBlock creation when port created :", true);
    }

    @Override
    public void testAction() {
        // Action to test
        Element owner = findTestedElementByID("_2021x_2_302b0611_1670957597348_883331_3236");//http://localhost:9850/refmodel/?ID=_2021x_2_302b0611_1670957597348_883331_3236
        openDiagram("_2021x_2_302b0611_1670957709771_148945_3383");//http://localhost:9850/refmodel/?ID=_2021x_2_302b0611_1670957709771_148945_3383
        Port port = SysMLFactory.getInstance().createProxyPort(owner);
        port.setName("p1");
    }


    @Override
    public void reInitEnvOptions() {
    }


}

