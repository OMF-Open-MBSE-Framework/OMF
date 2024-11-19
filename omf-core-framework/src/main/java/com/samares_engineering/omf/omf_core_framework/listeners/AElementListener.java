/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AElementListener extends AListener implements IElementListener  {



    @Override
    public boolean manageAnalysis(PropertyChangeEvent event) {
        List<LiveActionEngine> liveActionEngines = getLiveActionEngineMap().get(LiveActionType.ANALYSE.toString());
        return processAllMatchingLiveActions(liveActionEngines, event);
    }

    @Override
    public boolean manageCreation(PropertyChangeEvent event) {
        List<LiveActionEngine> liveActionEngines = getLiveActionEngineMap().get(LiveActionType.CREATE.toString());
        return processAllMatchingLiveActions(liveActionEngines, event);
    }

    @Override
    public boolean manageUpdate(PropertyChangeEvent event) {
        List<LiveActionEngine> liveActionEngines = getLiveActionEngineMap().get(LiveActionType.UPDATE.toString());
        return processAllMatchingLiveActions(liveActionEngines, event);
    }

    @Override
    public boolean manageDeletion(PropertyChangeEvent event) {
        List<LiveActionEngine> liveActionEngines = getLiveActionEngineMap().get(LiveActionType.DELETE.toString());
        return processAllMatchingLiveActions(liveActionEngines, event);
    }

    @Override
    public boolean manageAfterAutomation(Collection<PropertyChangeEvent> l_events) {
        List<LiveActionEngine> liveActionEngines = getLiveActionEngineMap().get(LiveActionType.AFTER_AUTOMATION.toString());
        return l_events.stream().anyMatch(event -> processAllMatchingLiveActions(liveActionEngines, event));
    }

    /**
     * @return true if at least one liveAction matched
     */
    private boolean processAllMatchingLiveActions(List<LiveActionEngine> liveActionEngines, PropertyChangeEvent event) {
        if(liveActionEngines == null) return false;
        boolean hasLiveActionsBeenTriggered = liveActionEngines.stream()
                .filter(liveActionEngine -> liveActionEngine.checkLiveActionEngineType(event.getClass()))
                .map(liveActionEngine -> liveActionEngine.processAllMatchingLiveActions(event))
                .collect(Collectors.toList())
                .contains(true);
        return hasLiveActionsBeenTriggered;
    }


}
