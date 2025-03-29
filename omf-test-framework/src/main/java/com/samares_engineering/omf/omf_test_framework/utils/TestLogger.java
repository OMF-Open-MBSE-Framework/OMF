/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_test_framework.utils;

import org.apache.log4j.Logger;

public class TestLogger{
    private final TestLogBuilder logBuilder;
    private final Logger mdLogger;

    public TestLogger(Logger mdLogger){
        logBuilder = new TestLogBuilder();
        this.mdLogger = mdLogger;
    }

    public void log(String log){
        logBuilder.log(log);
        mdLogger.info(log);
    }
    

    public void warn(String log) {
        logBuilder.warn(log);
        mdLogger.warn(log);
    }


    public void status(String log) {
        logBuilder.status(log);
        mdLogger.debug(log);
    }


    public void success(String log) {
        logBuilder.success(log);
        mdLogger.info(log);
    }


    public void err(String log) {
        logBuilder.err(log);
        mdLogger.error(log);
    }

    public void pink(String log) {
        logBuilder.pink(log);
        mdLogger.debug(log);
    }

    public void fatal(String log) {
        logBuilder.err(log);
        mdLogger.fatal(log);
    }

    public TestLogBuilder getLogBuilder() {
        return logBuilder;
    }

    public Logger getMdLogger() {
        return mdLogger;
    }

    public void showLogs() {
        logBuilder.showLogs();
    }
}
