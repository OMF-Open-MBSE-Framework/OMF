/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.listeners;

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
