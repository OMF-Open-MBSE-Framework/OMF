/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.formatter;

import junit.framework.Test;
import junit.framework.TestResult;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.junit.runner.Description;

/**
 * Wraps {@link Description} into {@link Test} enough to fake {@link JUnitResultFormatter}.
 */
public class DescriptionAsTest implements Test {
    private final Description description;

    public DescriptionAsTest(Description description) {
        this.description = description;
    }

    public int countTestCases() {
        return 1;
    }

    public void run(TestResult result) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@link JUnitResultFormatter} determines the test name by reflection.
     */
    public String getName() {
        return description.getDisplayName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DescriptionAsTest that = (DescriptionAsTest) o;

        return description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }
}