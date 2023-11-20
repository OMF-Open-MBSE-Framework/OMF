/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners.listeners;

import com.nomagic.magicdraw.copypaste.CopyPasteManager;
import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants;
import com.nomagic.uml2.transaction.TransactionCommitListener;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.cancelsession.UndoManager;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFRollBackException;
import com.samares_engineering.omf.omf_core_framework.listeners.AElementListener;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

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
        try {
            if (!isActivated() || CopyPasteManager.isPasting()) return;

            stopHandlingThisBatch = false;

            allTriggeredEventsInThisBatch.forEach(this::manageAnalysis);

            for (PropertyChangeEvent evt : allTriggeredEventsInThisBatch) {
                if (isInstanceCreated(evt)) {
                    stopHandlingThisBatch = manageCreation(evt);
                } else {
                    stopHandlingThisBatch = manageUpdate(evt);
                }
                if (stopHandlingThisBatch) return;
            }
        }
        catch (OMFRollBackException rollBackException){
            UndoManager.getInstance().requestHardUndo();
        }
        catch (Exception e){
            OMFErrorHandler.handleException(e, false);
        }
    }

    private boolean isInstanceCreated(PropertyChangeEvent evt) {
        return UML2MetamodelConstants.INSTANCE_CREATED.equals(evt.getPropertyName());
    }

    @Override
    public void allTransactionsCommitted() {
    }

    @Override
    public void addingListener() {
        OMFUtils.getProject().getRepository().getTransactionManager().addTransactionCommitListener(this);
    }

    @Override
    public void removingListener() {
        OMFUtils.getProject().getRepository().getTransactionManager().removeTransactionCommitListener(this);
    }
    }
