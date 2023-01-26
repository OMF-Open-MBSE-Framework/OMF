package com.samares.omf.core.listeners;

import com.samares.omf.core.listeners.ruleEngineListener.ruleEngine.IRuleEngine;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface IElementListener {
    void addListener();

    void removeListener();

    boolean isActivated();

    void activate();

    void deactivate();

    HashMap<String, List<IRuleEngine>> getRuleEngineMap();

    void setRuleEngineMap(HashMap<String, List<IRuleEngine>> m_event_ruleEngine);

    /**
     * @return true if at least one rule matched
     */
    boolean manageAnalysis(PropertyChangeEvent event);

    /**
     * @return true if at least one rule matched
     */
    boolean manageCreation(PropertyChangeEvent event);

    /**
     * @return true if at least one rule matched
     */
    boolean manageUpdate(PropertyChangeEvent event);

    /**
     * @return true if at least one rule matched
     */
    boolean manageDeletion(PropertyChangeEvent event);

    /**
     * @return true if at least one rule matched
     */
    void manageAfterAutomation(Collection<PropertyChangeEvent> l_events);
}
