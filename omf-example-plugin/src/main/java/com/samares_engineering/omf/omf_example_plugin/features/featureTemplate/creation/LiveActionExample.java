/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.featureTemplate.creation;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction;
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker;

import java.beans.PropertyChangeEvent;

public class LiveActionExample extends ALiveAction {
    @Override
    protected boolean eventMatches(PropertyChangeEvent evt) {
       return new EventChecker()
                .isElementCreated()
                .isBlock()
                .test(evt);
    }

    @Override
    public void debug(Object o) {

    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
//            Class block = (Class) e.getSource();
//            block.setName("succeed");
        return e;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
