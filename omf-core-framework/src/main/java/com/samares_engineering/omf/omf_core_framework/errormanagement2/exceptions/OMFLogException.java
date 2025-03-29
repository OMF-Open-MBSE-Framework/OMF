package com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;

public class OMFLogException extends RuntimeException {
    protected OMFLog omfLog;

    public OMFLogException() {
        super();
    }

    public OMFLogException(String message) {
        this.omfLog = new OMFLog().text(message);
    }

    public OMFLogException(String message, Throwable cause) {
        super(cause);
        this.omfLog = new OMFLog().text(message);
    }

    public OMFLogException(OMFLog message) {
        this.omfLog = message;
    }

    public OMFLogException(OMFLog message, Throwable cause) {
        super(cause);
        this.omfLog = message;
    }

    public OMFLogException(Throwable cause) {
        super(cause);
    }

    public OMFLog getLog() {
        return omfLog;
    }

    @Override
    public String getMessage() {
        return omfLog.toString();
    }
}
