/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.utils;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Suite;

import java.lang.reflect.Method;

public class    BasicTestRunner extends Runner {

    private Class suiteClass;

    public BasicTestRunner(Class testClass) {
        super();
        this.suiteClass = testClass;
    }

    @Override
    public Description getDescription() {
        return Description
                .createTestDescription(suiteClass, "My runner description");
    }

    @Override
    public void run(RunNotifier notifier) {
        System.out.println("running the tests from MyRunner: " + suiteClass);
        try {
            Object testObject = suiteClass.newInstance();

            Suite.SuiteClasses suiteClasses = (Suite.SuiteClasses) suiteClass.getAnnotation(Suite.SuiteClasses.class);
            for (Class testClass: suiteClasses.value()) {

                Object testInstance = testClass.newInstance();
                for (Method method : testClass.getMethods()) {
                    if (method.isAnnotationPresent(Test.class)) {
                        notifier.fireTestStarted(Description
                                .createTestDescription(testClass, method.getName()));
//                        method.invoke(((Batch1) testObject).getDependencies());
                        method.invoke(testInstance);
                        notifier.fireTestFinished(Description
                                .createTestDescription(testClass, method.getName()));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}