/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.DeletionPropertyChangeElementListener;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.OrchestratorListener;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.TransactionElementListener;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.UndoRedoTransactionElementListener;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ListenerManager implements IListenerManager {
    private boolean listenersActivated = false;
    private IElementListener orchestratorListener = new OrchestratorListener();
    // TODO Once the listeners have been migrated to OMF, instantiate them here and make the field private
    public List<IElementListener> featureListeners;
    public List<IElementListener> coreListeners;
    public IElementListener deletionListener = new DeletionPropertyChangeElementListener();
    public IElementListener transactionElementListener = new TransactionElementListener();
    public IElementListener undoRedoListener = new UndoRedoTransactionElementListener();

    private static class ListenerManagerHolder {
        private static final ListenerManager instance = new ListenerManager();
    }

    private ListenerManager() {
        this.coreListeners = new ArrayList<>(Arrays.asList(orchestratorListener));
        this.featureListeners = new ArrayList<>(Arrays.asList(
                deletionListener,
                transactionElementListener,
                undoRedoListener
        ));
    }

    public static ListenerManager getInstance() {
        return ListenerManagerHolder.instance;
    }

    @Override
    public void addListener(IElementListener listener) {
        if (listener == null) return;
        this.featureListeners.add(listener);
        listener.register();
    }

    @Override
    public void addListeners(List<IElementListener> listeners) {
        if (CollectionUtils.isEmpty(listeners)) return;
        listeners.forEach(this::addListener);
    }

    @Override
    public void removeListener(IElementListener listener) {
        if (listener == null) return;
        this.featureListeners.remove(listener);
        listener.unregister();
    }

    @Override
    public void removeListeners(List<IElementListener> listeners) {
        if (CollectionUtils.isEmpty(listeners)) return;
        listeners.forEach(this::removeListener);
    }

    @Override
    public void addCoreListener(IElementListener listener) {
        if (listener == null) return;
        this.coreListeners.add(listener);
        listener.register();
    }

    @Override
    public void addCoreListeners(List<IElementListener> listeners) {
        if (CollectionUtils.isEmpty(listeners)) return;
        listeners.forEach(this::addCoreListener);
    }

    @Override
    public void removeCoreListener(IElementListener listener) {
        if (listener == null) return;
        this.coreListeners.remove(listener);
        listener.unregister();
    }

    @Override
    public void removeCoreListeners(List<IElementListener> listeners) {
        if (CollectionUtils.isEmpty(listeners)) return;
        listeners.forEach(this::removeCoreListener);
    }

    public void activateAllListeners() {
        if (listenersActivated || this.featureListeners == null) return;
        this.featureListeners.forEach(IElementListener::activate);
        listenersActivated = true;
        SysoutColorPrinter.status("Listeners Activated");
    }

    public void deactivateAllListeners() {
        if (!listenersActivated || !thereAreDeclaredListeners()) return;
        this.featureListeners.forEach(IElementListener::deactivate);
        SysoutColorPrinter.status("Listeners Deactivated");
        listenersActivated = false;
    }

    public void registerAllListeners() {
        this.coreListeners.stream()
                .filter(Objects::nonNull)
                .filter(IElementListener::isNotRegistered)
                .forEach(IElementListener::register);
        this.featureListeners.stream()
                .filter(Objects::nonNull)
                .filter(IElementListener::isNotRegistered)
                .forEach(IElementListener::register);
        SysoutColorPrinter.status("Listeners Registered");
    }

    public void removeAllListeners() {
       this.coreListeners
                .stream()
                .filter(Objects::nonNull)
                .filter(IElementListener::isRegistered)
                .forEach(IElementListener::unregister);
        this.featureListeners
                .stream()
                .filter(Objects::nonNull)
                .filter(IElementListener::isRegistered)
                .forEach(IElementListener::unregister);
        SysoutColorPrinter.status("Listeners Removed");
    }

    //Standard
    @Override
    public IElementListener getAnalysisListener() {
        return transactionElementListener;
    }

    @Override
    public IElementListener getHistoryListener() {return transactionElementListener;}

    @Override
    public IElementListener getCreationListener() {
        return transactionElementListener;
    }

    @Override
    public IElementListener getUpdateListener() {
        return transactionElementListener;
    }

    //UndoRedo
    @Override
    public IElementListener getUndoRedoAnalysisListener() {
        return undoRedoListener;
    }

    @Override
    public IElementListener getUndoRedoHistoryListener() {
        return undoRedoListener;
    }

    @Override
    public IElementListener getUndoRedoCreationListener() {
        return undoRedoListener;
    }
    @Override
    public IElementListener getUndoRedoUpdateListener() {
        return undoRedoListener;
    }
    @Override
    public IElementListener getUndoRedoDeletionListener() {
        return undoRedoListener;
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
        return CollectionUtils.isNotEmpty(this.featureListeners);
    }

    public boolean isListenersActivated() {
        return listenersActivated;
    }
}
