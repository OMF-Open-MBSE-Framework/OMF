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
    Optional<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> getMatchingRule(PropertyChangeEvent evt);

    List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> getAllMatchingRules(PropertyChangeEvent evt);

    boolean processAllMatchingRule(PropertyChangeEvent evt);

    boolean skipRules(PropertyChangeEvent evt);

    void addRule(LiveAction<PropertyChangeEvent, PropertyChangeEvent> rule);

    void addAllRules(List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> lRules);

    List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> getRules();

    void removeRule(LiveAction<PropertyChangeEvent, PropertyChangeEvent> rule);

    void removeRules(List<LiveAction<PropertyChangeEvent, PropertyChangeEvent>> lRules);

    void removeAllRules();

    void setPriority(int priority);

    String getCategory();
    void setCategory(String category);
}
