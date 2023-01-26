/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.projectmanager;

import com.samares.omf.test.templates.AbstractTestCase;

public abstract class AbstractTestProject extends AbstractTestCase {

    @Override
    public void test() {
        testAction();
        verifyResults();
    }
}
