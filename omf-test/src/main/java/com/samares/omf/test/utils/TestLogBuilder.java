/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.test.utils;

import com.samares.omf.core.utils.ColorPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        log(ColorPrinter.BLUE, message);
        return this;
    }
    public TestLogBuilder err(String message) {
        log(ColorPrinter.RED, message);
        return this;
    }
    public TestLogBuilder success(String message) {
        log(ColorPrinter.GREEN, message);
        return this;
    }
    public TestLogBuilder warn(String message) {
        log(ColorPrinter.YELLOW, message);
        return this;
    }
    public TestLogBuilder log(String message) {
        log("", message);
        return this;
    }

    public TestLogBuilder pink(String message) {
        log(ColorPrinter.PURPLE, message);
        return this;
    }

    public void showLogs() {
        logs.forEach(log -> ColorPrinter.print(log.message, log.color));
    }
}
