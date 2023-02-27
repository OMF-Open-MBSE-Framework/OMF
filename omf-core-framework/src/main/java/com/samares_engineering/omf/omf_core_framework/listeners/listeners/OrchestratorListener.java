/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners.listeners;

import com.nomagic.uml2.transaction.TransactionCommitListener;
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
    public Runnable transactionCommited(Collection<PropertyChangeEvent> collection) {
        return null;
    }

    @Override
    public void allTransactionsCommitted() {
        GarbageCollector.collectGarbage();
        AllCreatedElements.emptyAll();

        hasSessionBeenCanceled = false;

        ListenerManager.getInstance().activateAllListeners();
    }

    @Override
    public void addListener() {
        OMFUtils.currentProject.getRepository().getTransactionManager()
                .addTransactionCommitListener(this);
    }

    @Override
    public void removeListener() {
        final boolean isListenerRemovable = (null != OMFUtils.currentProject);
        if (isListenerRemovable) {
            try {
                OMFUtils.currentProject.getRepository().getTransactionManager().removeTransactionCommitListener(this);
            } catch (Exception e) {
                System.err.println("[RemoveListener]");
            }
        }
    }

    @Override
    public void manageAfterAutomation(Collection<PropertyChangeEvent> collection) {

    }
}
