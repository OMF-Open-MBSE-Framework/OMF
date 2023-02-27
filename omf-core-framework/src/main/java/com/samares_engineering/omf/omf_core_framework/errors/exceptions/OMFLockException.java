/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errors.exceptions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;

import java.util.ArrayList;
import java.util.Collection;

public class OMFLockException extends OMFException implements IException {

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
