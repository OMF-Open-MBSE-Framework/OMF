/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.listeners;

import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;

import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public interface IElementListener {

    /**
     * Declare how the listener should be registered.
     * Prefer using register() method instead of the addListener() method to register the listener,
     * as it encapsulates the addListener() method and boilerplate code.
     */
    void addingListener();

    /**
     * Declare how the listener should be unregistered.
     * Prefer using unregister() method instead of the removeListener() method to unregister the listener,
     * as it encapsulates the removeListener() method and boilerplate code.
     */
    void removingListener();

    boolean isActivated();
    /**
     * Setting the activated attribute to true, activation will not register the listener.
     * This help to activate/deactivate the listener without registering it.
     */
    void activate();

    /**
     * Setting the activated attribute to false, deactivation will not unregister the listener.
     * This help to activate/deactivate the listener without unregistering it.
     */

    void deactivate();
    /**
     * @return true if the listener is registered
     */

    boolean isRegistered();

    /**
     * @return true if the listener is not registered
     */
    boolean isNotRegistered();
    /**
     * Setting the isRegistered attribute
     */

    void setIsRegistered(boolean isRegistered);

    /**
     * @return the RuleEngine map
     */
    HashMap<String, List<LiveActionEngine>> getRuleEngineMap();

    /**
     * Setting the RuleEngine map
     */
    void setRuleEngineMap(HashMap<String, List<LiveActionEngine>> rulesEngines);

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
     * @return trigger
     */

    boolean manageAfterAutomation(Collection<PropertyChangeEvent> l_events);

    /**
     * Registering the listener, triggering the addListener() method and setting the isRegistered attribute to true
     * Prefer using register() method instead of the addListener() method
     */
    default void register() {
        if (isRegistered()) return;
        try {
            addingListener();
            setIsRegistered(true);
        }catch (Exception e){
            deactivate();
            LegacyErrorHandler.handleException(new LegacyOMFException("Error while registering listener", e, GenericException.ECriticality.ALERT), false);
        }
    }

    /**
     * Unregistering the listener, triggering the removeListener() method and setting the isRegistered attribute to false
     * Prefer using unregister() method instead of the removeListener() method
     */
    default void unregister(){
        if (!isRegistered()) return;
        try {
            removingListener();
            setIsRegistered(false);
        }catch (Exception e){
            LegacyErrorHandler.handleException(new LegacyOMFException("Error while unregistering listener", e, GenericException.ECriticality.ALERT));
        }
    }

}
