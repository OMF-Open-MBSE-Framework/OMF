/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.errors.exceptions;

import com.google.common.base.Strings;
import com.nomagic.magicdraw.ui.notification.NotificationSeverity;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.utils.OMFConstants;

public class GenericException extends Exception implements IException {
    protected String message;
    protected Element createdElement;

    protected String debugMessage        = "";
    protected String userMessage         = "";
    protected ECriticality criticality   = null;
    protected Exception exception        = null;

    public GenericException(String message){
        super("[Uncaught exception - " + "] " + message);
        this.message = message;
    }


    public GenericException(String message, Element createdElement, String featureName){
        super("[Feature - " + featureName + "] " + message + (createdElement!=null?"\n on created element: " +createdElement.getHumanName() :""));
        this.message = message;
        this.createdElement = createdElement;
    }


    public GenericException(String errorMsg, ECriticality criticality){
        this(errorMsg, errorMsg, null,  criticality);
    }

    public GenericException(String errorMsg, Exception exception, ECriticality criticality){
        this(errorMsg, errorMsg, exception, criticality);
    }

    public GenericException(String debugMessage, String userMessage, ECriticality criticality){
        this(debugMessage, userMessage, null, criticality);
    }

    public GenericException(String debugMessage, String userMessage, Exception exception, ECriticality criticality) {
        this.debugMessage   = debugMessage;
        this.userMessage    = userMessage;
        this.criticality    = criticality;
        this.exception      = exception;
    }

    public void displayDevMessage(){
        System.err.println(debugMessage);
        if(null != exception)
            exception.printStackTrace();
        else
            super.printStackTrace();
    }

    public void displayUserMessage() {
        if(Strings.isNullOrEmpty(userMessage)) return;

        OMFLogLevel logLevel = OMFLogLevel.WARNING;
        if (criticality == ECriticality.CRITICAL)
            logLevel = OMFLogLevel.ERROR;

        OMFLogger.logToUIConsole(userMessage, logLevel);
        OMFLogger.logToNotification(getUserMessage(), logLevel);
    }

    @Override
    public void printStackTrace() {
        displayDevMessage();
        if(!isDebugModeActivated() && criticality != ECriticality.SILENT)
            displayUserMessage();
        super.printStackTrace();
    }

    @Override
    public String getMessage() {
        return Strings.isNullOrEmpty(message)? getUserMessage(): message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Element getCreatedElement() {
        return createdElement;
    }

    public void setCreatedElement(Element createdElement) {
        this.createdElement = createdElement;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(String userMessage) {
        this.userMessage = userMessage;
    }

    public ECriticality getCriticality() {
        return criticality;
    }

    public void setCriticality(ECriticality criticality) {
        this.criticality = criticality;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

    private boolean isDebugModeActivated() {
        return OMFConstants.DEBUG_MODE_ACTIVATED;
    }

    public enum ECriticality {
        SILENT,
        ALERT,
        CRITICAL
    }

    private static NotificationSeverity getNotificationSeverity(GenericException.ECriticality criticality) {
        switch (criticality){
            case CRITICAL:
                return NotificationSeverity.ERROR;
            case SILENT:
            case ALERT:
            default:
                return NotificationSeverity.WARNING;
        }
    }

}
