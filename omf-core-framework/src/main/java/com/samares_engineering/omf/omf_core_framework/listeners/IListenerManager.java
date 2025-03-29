/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.listeners;

import java.util.List;

public interface IListenerManager {

    void addListener(IElementListener listener);
    void addListeners(List<IElementListener> listeners);
    void removeListener(IElementListener listener);
    void removeListeners(List<IElementListener> listeners);

    void addCoreListener(IElementListener listener);

    void addCoreListeners(List<IElementListener> listeners);

    void removeCoreListener(IElementListener listener);

    void removeCoreListeners(List<IElementListener> listeners);

    void activateAllListeners();

    void deactivateAllListeners();

    void registerAllListeners();

    void removeAllListeners();

    IElementListener getAnalysisListener();

    IElementListener getHistoryListener();

    IElementListener getCreationListener();

    IElementListener getUpdateListener();

    IElementListener getAfterAutomationListener();

    IElementListener getDeletionListener();

    IElementListener getUndoRedoAnalysisListener();
    IElementListener getUndoRedoHistoryListener();
    IElementListener getUndoRedoCreationListener();
    IElementListener getUndoRedoUpdateListener();
    IElementListener getUndoRedoDeletionListener();

}
