/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.test.suites.testcases.stereotypes;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares.omf.core.factory.SysMLFactory;
import com.samares.omf.test.templates.AbstractModelComparatorTestCase;

/**
 * Important write the <Wizard.class> to get the right wizard
 */
public class ExampleStereotypesTest extends AbstractModelComparatorTestCase {

    @Override
    public void initVariables() {
        setName("Example stereotypes test");
        testCaseID = "Stereo1";
        testPackageName = "Stereo Example";
    }

    @Override
    public void initEnvOptions() {
        setEnvironmentOptionValueByCategoryName("Stereotype", "Activate OMF Automations", true);
    }

    @Override
    public void testAction() {
        // Action to test
        Class port = (Class) findTestedElementByID("_2021x_2_302b0611_1670957740478_263727_3439");
        SysMLFactory.getInstance().removeElement(port);
    }

    @Override
    public void reInitEnvOptions() {

    }


}

