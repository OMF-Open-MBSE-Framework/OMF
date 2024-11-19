/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.projectmanager;

import com.samares_engineering.omf.omf_test_framework.templates.AbstractTestCase;

public abstract class AbstractTestProject extends AbstractTestCase {

    @Override
    public void test() {
        testAction();
        verifyResults();
    }
}
