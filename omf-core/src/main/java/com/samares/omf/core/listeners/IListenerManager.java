package com.samares.omf.core.listeners;

public interface IListenerManager {
    void activateAllListeners();

    void deactivateAllListeners();

    void registerAllListeners();

    void removeAllListeners();

    IElementListener getAnalysisListener();

    IElementListener getCreationListener();

    IElementListener getUpdateListener();

    IElementListener getAfterAutomationListener();

    IElementListener getDeletionListener();
}
