/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */

package com.samares.omf.plugin.test.suite.basicsysml_test.portcreation;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.utils.factory.SysMLFactory;
import com.samares.omf.test.templates.AbstractModelComparatorTestCase;

/**
 * Important write the <Wizard.class> to get the right wizard
 */
public class T3_CreatePort extends AbstractModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("[B] create proxy port");
        testCaseID = "Create1";
        testPackageName = "1 create proxy port";
    }

    @Override
    public void initEnvOptions() {
        setEnvironmentOptionValueByCategoryName("OMF", "Activate OMF Automations", true);

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

