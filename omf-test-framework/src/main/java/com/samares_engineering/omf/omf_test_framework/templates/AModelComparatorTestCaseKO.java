package com.samares_engineering.omf.omf_test_framework.templates;

import com.samares_engineering.omf.omf_test_framework.utils.TestHelper;

public abstract class AModelComparatorTestCaseKO extends AModelComparatorTestCase {
    @Override
    public void compareModel(){
        assertFalse("Model Comparator succeed, but it shouldn't as it's a KO test. ", TestHelper.compareTestProjects(this));
    }
}
