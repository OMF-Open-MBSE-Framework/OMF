/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners.listeners;

import com.nomagic.uml2.transaction.TransactionCommitListener;
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager;
import com.samares_engineering.omf.omf_core_framework.listeners.AElementListener;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.utils.AllCreatedElements;
import com.samares_engineering.omf.omf_core_framework.utils.GarbageCollector;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import javax.annotation.CheckForNull;
import java.beans.PropertyChangeEvent;
import java.util.Collection;

public class OrchestratorListener extends AElementListener implements TransactionCommitListener {
    public static boolean hasSessionBeenCanceled = false;

    @CheckForNull
    @Override
    public Runnable transactionCommited(Collection<PropertyChangeEvent> sessionHistory) {
        return null;
    }

    @Override
    public void allTransactionsCommitted() {
        GarbageCollector.collectGarbage();
        AllCreatedElements.emptyAll();
        OMFAutomationManager.getInstance().resetAutomationTriggered();
        hasSessionBeenCanceled = false;

        ListenerManager.getInstance().activateAllListeners();
    }

    @Override
    public void addingListener() {
        OMFUtils.getProject().getRepository().getTransactionManager()
                .addTransactionCommitListenerIncludingUndoAndRedo(this);
    }

    @Override
    public void removingListener() {
        final boolean isListenerRemovable = (OMFUtils.isProjectOpened());
        if (isListenerRemovable) {
            try {
                OMFUtils.getProject().getRepository().getTransactionManager().removeTransactionCommitListener(this);
            } catch (Exception e) {
                System.err.println("[RemoveListener]");
            }
        }
    }

    @Override
    public boolean manageAfterAutomation(Collection<PropertyChangeEvent> collection) {
        return false;
    }
}
