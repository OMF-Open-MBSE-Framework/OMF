/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.templates;


import com.samares.omf.test.utils.TestHelper;

public abstract class AbstractModelComparatorTestCase extends AbstractTestCase{

    @Override
    public void setUpTest() throws Exception {
        super.setUpTest();
        this.oracleNeeded = true;
    }

    @Override
    protected void checkPrecondition() {
        assertNotNull("initProject is null", initProject);
        assertTrue("oracleProject is null", oracleNeeded && oracleProject != null);
    }

    @Override
    public void verifyResults() {
        compareModel();
    }

    public void compareModel(){
        assertTrue("Model Comparator failed, see System Out for more details: ", TestHelper.compareTestProjects(this));
    }

}

