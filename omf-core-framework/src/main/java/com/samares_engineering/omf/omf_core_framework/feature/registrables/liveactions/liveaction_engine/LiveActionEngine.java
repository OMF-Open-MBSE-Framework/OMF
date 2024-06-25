/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine;


import com.nomagic.magicdraw.utils.PriorityProvider;
import com.samares_engineering.omf.omf_core_framework.feature.RegistrableFeatureItem;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.LiveAction;

import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Optional;

public interface LiveActionEngine extends PriorityProvider, RegistrableFeatureItem {
    Optional<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> getMatchingLiveAction(PropertyChangeEvent evt);

    List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> getAllMatchingLiveActions(PropertyChangeEvent evt);

    boolean processAllMatchingLiveActions(PropertyChangeEvent evt);

    boolean skipLiveActions(PropertyChangeEvent evt);

    void addLiveAction(LiveAction<PropertyChangeEvent, PropertyChangeEvent> liveAction);

    void addAllLiveActions(List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> liveActions);

    List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> getLiveActions();

    void removeLiveAction(LiveAction<PropertyChangeEvent, PropertyChangeEvent> liveAction);

    void removeLiveActions(List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> liveActions);

    void removeAllLiveActions();

    void setPriority(int priority);

    String getType();
    void setType(String category);
}
