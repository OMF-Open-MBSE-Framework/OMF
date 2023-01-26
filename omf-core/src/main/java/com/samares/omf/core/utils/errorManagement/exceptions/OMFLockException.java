/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */
package com.samares.omf.core.utils.errorManagement.exceptions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.utils.errorManagement.OMFLogLevel;
import com.samares.omf.core.utils.errorManagement.OMFLogger;

import java.util.ArrayList;
import java.util.Collection;

public class OMFLockException extends OMFException implements I_Exception {

    public Collection<Element> lockedElements;
    public Exception exception;
    public GenericException.ECriticality criticality;

    public OMFLockException(String msg){
        super(msg, GenericException.ECriticality.CRITICAL);
    }
    public OMFLockException(com.nomagic.esi.api.messages.exceptions.LockException e, Collection<Element> lockedElements) {
        super(e.getMessage(), GenericException.ECriticality.CRITICAL);
        exception = e;
        this.lockedElements = lockedElements;
    }
    public OMFLockException(com.nomagic.esi.api.messages.exceptions.LockException e, Element lockedElement) {
        super(e.getMessage(), GenericException.ECriticality.CRITICAL);
        exception = e;
        lockedElements = new ArrayList<>();
        lockedElements.add(lockedElement);
    }
    public OMFLockException(String message, Element lockedElement) {
        super(message, GenericException.ECriticality.CRITICAL);
        lockedElements = new ArrayList<>();
        lockedElements.add(lockedElement);
    }



    @Override
    public void displayUserMessage(){
        OMFLogLevel logLevel = OMFLogLevel.WARNING;

        if(criticality == GenericException.ECriticality.CRITICAL)
            logLevel = OMFLogLevel.ERROR;

        OMFLogLevel finalLogLevel = logLevel;
        lockedElements.forEach(element -> OMFLogger.getInstance().logWithOwner(getMessage(), element, finalLogLevel));
    }
}
