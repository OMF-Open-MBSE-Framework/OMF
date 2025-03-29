/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.listeners;

import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

public interface IElementListener extends IListener {

    /**
     * Manage the event after the analysis
     * @param event the event
     * @return true if at least one liveAction matched
     */
    boolean manageAnalysis(PropertyChangeEvent event);

    /**
     * Manage the event after the creation
     * @param event the event
     * @return true if at least one liveAction matched
     */
    boolean manageCreation(PropertyChangeEvent event);

    /**
     * Manage the event after the update
     * @param event the event
     * @return true if at least one liveAction matched
     */
    boolean manageUpdate(PropertyChangeEvent event);

    /**
     * Manage the event after the deletion
     * @param event the event
     * @return true if at least one liveAction matched
     */
    boolean manageDeletion(PropertyChangeEvent event);
    /**
     * Manage the event after the automation
     * @param l_events list of events
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
            activate();
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
            deactivate();
        }catch (Exception e){
            LegacyErrorHandler.handleException(new LegacyOMFException("Error while unregistering listener", e, GenericException.ECriticality.ALERT));
        }
    }

}
