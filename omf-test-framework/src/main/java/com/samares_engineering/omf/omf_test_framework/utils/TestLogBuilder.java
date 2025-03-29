/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.utils;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFColors.*;

public class TestLogBuilder{

    public List<TestLog> logs;

    public TestLogBuilder() {
        this.logs = new ArrayList<>();
    }
    public TestLogBuilder(String color, String message) {
        this(new TestLog(color, message));
    }

    public TestLogBuilder(TestLog testLog) {
        this.logs = Arrays.asList(testLog);
    }

    public TestLogBuilder log(String color, String message) {
        logs.add(new TestLog(color, message));
        return this;
    }

    public TestLogBuilder status(String message) {
        log(BLUE, message);
        return this;
    }
    public TestLogBuilder err(String message) {
        log(RED, message);
        return this;
    }
    public TestLogBuilder success(String message) {
        log(GREEN, message);
        return this;
    }
    public TestLogBuilder warn(String message) {
        log(YELLOW, message);
        return this;
    }
    public TestLogBuilder log(String message) {
        log("", message);
        return this;
    }

    public TestLogBuilder pink(String message) {
        log(PURPLE, message);
        return this;
    }

    public void showLogs() {
        logs.forEach(log -> SysoutColorPrinter.print(log.message, log.color));
    }
}
