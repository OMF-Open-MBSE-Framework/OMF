/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.listeners.util;


import com.samares.omf.core.listeners.ruleEngineListener.ruleEngine.*;


public class ListenerConfig {

    private static ListenerConfig instance = null;

    private boolean hasPortCreatedFromConnection = false;

    public ListenerConfig() {
        super();
        //TODO MANAGE HERE ALL ENGINE REGISTERING

    }

    public static ListenerConfig getInstance() {
        if (null == instance)
            instance = new ListenerConfig();
        return instance;
    }


    public void init() {
        hasPortCreatedFromConnection = false;
    }

    public void setHasPortCreatedFromConnection(boolean hasPortCreatedFromConnection) {
        this.hasPortCreatedFromConnection = hasPortCreatedFromConnection;
    }

    public boolean hasPortCreatedFromConnection() {
        return hasPortCreatedFromConnection;
    }

}
