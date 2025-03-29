/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.errors.exceptions.general;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;

import java.util.ArrayList;
import java.util.Collection;

public class LockException extends LegacyOMFException {
    public Collection<Element> lockedElements;
    public Exception exception;
    public GenericException.ECriticality criticality;

    public LockException(String msg){
        super(msg, GenericException.ECriticality.CRITICAL);
    }
    public LockException(com.nomagic.esi.api.messages.exceptions.LockException e, Collection<Element> lockedElements) {
        super(e.getMessage(), GenericException.ECriticality.CRITICAL);
        exception = e;
        this.lockedElements = lockedElements;
    }
    public LockException(com.nomagic.esi.api.messages.exceptions.LockException e, Element lockedElement) {
        super(e.getMessage(), GenericException.ECriticality.CRITICAL);
        exception = e;
        lockedElements = new ArrayList<>();
        lockedElements.add(lockedElement);
    }
    public LockException(String message, Element lockedElement) {
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
        lockedElements.forEach(element ->
                new OMFLog().text(getMessage(), finalLogLevel).linkElementAndParent(element).logToUiConsole(finalLogLevel));
    }
}
