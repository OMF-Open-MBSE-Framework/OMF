/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.suites.testcases.basicsysml.portcreation;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.factory.SysMLFactory;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;
import com.samares.omf.test.templates.AModelComparatorTestCase;

public class T3CreatePort extends AModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("[B] create proxy port");
        testCaseID = "Create1";
        testPackageName = "1 create proxy port";
    }

    @Override
    public void initEnvOptions() {
        OMFPluginEnvOptionsGroup.getInstance().setAutomationsActivated(true);
    }

    @Override
    public void testAction() {
        // Action to test
        Element owner = findTestedElementByID("_2021x_2_302b0611_1670957597348_883331_3236");
        openDiagram("_2021x_2_302b0611_1670957709771_148945_3383");
        SysMLFactory.getInstance().createProxyPort(owner);
    }


    @Override
    public void reInitEnvOptions() {
    }


}

