package com.samares_engineering.omf.omf_core_framework.listeners;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;

import java.util.HashMap;
import java.util.List;

public interface IListener {
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
     * @param isRegistered true if the listener is registered
     */

    void setIsRegistered(boolean isRegistered);

    /**
     * @return the LiveActionEngine map
     */
    HashMap<String, List<LiveActionEngine>> getLiveActionEngineMap();

    /**
     * Setting the LiveActionEngine map
     * @param liveActionsEngines the LiveActionEngine map
     * key: the live action name <br>
     * value: the list of LiveActionEngine <br>
     */
    void setLiveActionEngineMap(HashMap<String, List<LiveActionEngine>> liveActionsEngines);
}
