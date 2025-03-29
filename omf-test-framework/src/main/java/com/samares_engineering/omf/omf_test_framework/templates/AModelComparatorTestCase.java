/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.templates;


import com.samares_engineering.omf.omf_test_framework.utils.TestHelper;

public abstract class AModelComparatorTestCase extends AbstractTestCase{

    @Override
    public void setUpTest() throws Exception {
        super.setUpTest();
        this.oracleNeeded = true;
    }

    @Override
    protected void checkPrecondition() {
        assertNotNull("initProject is null", getInitProject());
        assertTrue("oracleProject is null", !isOracleNeeded() || isOracleProjectOpened());
    }

    @Override
    public void verifyResults() {
        compareModel();
    }

    public void compareModel(){
        assertTrue("Model Comparator failed, see System Out for more details: ", TestHelper.compareTestProjects(this));
    }

}

