/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners;

import com.samares_engineering.omf.omf_core_framework.listeners.listeners.DeletionPropertyChangeElementListener;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.LockManagerListener;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.OrchestratorListener;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.TransactionElementListener;
import com.samares_engineering.omf.omf_core_framework.utils.ColorPrinter;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListenerManager implements IListenerManager {
    private boolean listenersActivated = false;
    private IElementListener orchestratorListener = new OrchestratorListener();
    private IElementListener lockManagerListener = new LockManagerListener();
    // TODO Once the listeners have been migrated to OMF, instantiate them here and make the field private
    public List<IElementListener> listenerList;
    public IElementListener deletionListener = new DeletionPropertyChangeElementListener();
    public IElementListener transactionElementListener = new TransactionElementListener();

    private static class ListenerManagerHolder {
        private static final ListenerManager instance = new ListenerManager();
    }

    private ListenerManager() {
        this.listenerList = new ArrayList<>(Arrays.asList(
                deletionListener,
                transactionElementListener
        ));
    }

    public static ListenerManager getInstance() {
        return ListenerManagerHolder.instance;
    }

    public void activateAllListeners() {
        if (listenersActivated || this.listenerList == null) return;
        this.listenerList.forEach(IElementListener::activate);
        listenersActivated = true;
        ColorPrinter.status("Listeners Activated");
    }

    public void deactivateAllListeners() {
        if (!listenersActivated || !thereAreDeclaredListeners()) return;
        this.listenerList.forEach(IElementListener::deactivate);
        ColorPrinter.status("Listeners Deactivated");
        listenersActivated = false;
    }

    public void registerAllListeners() {
        this.orchestratorListener.addListener();
        this.lockManagerListener.addListener();
        this.listenerList.stream().filter(Objects::nonNull).forEach(IElementListener::addListener);
        ColorPrinter.status("Listeners Registered");
    }

    public void removeAllListeners() {
        this.orchestratorListener.removeListener();
        this.lockManagerListener.removeListener();
        this.listenerList.forEach(IElementListener::removeListener);
        ColorPrinter.status("Listeners Removed");
    }

    @Override
    public IElementListener getAnalysisListener() {
        return transactionElementListener;
    }

    @Override
    public IElementListener getCreationListener() {
        return transactionElementListener;
    }

    @Override
    public IElementListener getUpdateListener() {
        return transactionElementListener;
    }

    @Override
    public IElementListener getAfterAutomationListener() {
        return transactionElementListener;
    }

    @Override
    public IElementListener getDeletionListener() {
        return deletionListener;
    }

    private boolean thereAreDeclaredListeners() {
        return CollectionUtils.isNotEmpty(this.listenerList);
    }

    public boolean isListenersActivated() {
        return listenersActivated;
    }
}
