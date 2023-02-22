/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.listeners.listeners;

import com.nomagic.magicdraw.copypaste.CopyPasteManager;
import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.nomagic.uml2.transaction.TransactionCommitListener;
import com.samares.omf.core.listeners.AElementListener;
import com.samares.omf.core.utils.OMFUtils;

import javax.annotation.CheckForNull;
import java.beans.PropertyChangeEvent;
import java.util.Collection;

public class TransactionElementListener extends AElementListener implements TransactionCommitListener {
    private boolean stopHandlingThisBatch;
    private Collection<PropertyChangeEvent> allTriggeredEventsInThisBatch;

    @CheckForNull
    @Override
    public Runnable transactionCommited(Collection<PropertyChangeEvent> allTriggeredEventsInThisBatch) {
        this.allTriggeredEventsInThisBatch = allTriggeredEventsInThisBatch;
        return this::runnable;
    }

    private void runnable() {
        if (!isActivated() || CopyPasteManager.isPasting()) return;

        stopHandlingThisBatch = false;

        allTriggeredEventsInThisBatch.forEach(this::manageAnalysis);

        for (PropertyChangeEvent evt : allTriggeredEventsInThisBatch) {
            if (isInstanceCreated(evt)) {
                stopHandlingThisBatch = manageCreation(evt);
            }
            else {
                stopHandlingThisBatch = manageUpdate(evt);
            }
            if (stopHandlingThisBatch) return;
        }
    }

    private boolean isInstanceCreated(PropertyChangeEvent evt) {
        return UML2MetamodelConstants.INSTANCE_CREATED.equals(evt.getPropertyName());
    }

    @Override
    public void allTransactionsCommitted() {
    }

    @Override
    public void addListener() {
        OMFUtils.currentProject.getRepository().getTransactionManager().addTransactionCommitListener(this);
    }

    @Override
    public void removeListener() {
        OMFUtils.currentProject.getRepository().getTransactionManager().removeTransactionCommitListener(this);
    }

    @Override
    public void manageAfterAutomation(Collection<PropertyChangeEvent> collection) {

    }
}
