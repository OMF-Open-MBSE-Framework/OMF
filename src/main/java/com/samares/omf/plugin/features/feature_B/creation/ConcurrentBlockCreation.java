package com.samares.omf.plugin.features.feature_B.creation;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares.omf.core.listeners.ruleEngineListener.rules.A_Rule;
import com.samares.omf.plugin.features.organizer.utils.EventChecker;

import java.beans.PropertyChangeEvent;

public class ConcurrentBlockCreation extends A_Rule {
    @Override
    protected boolean eventMatches(PropertyChangeEvent evt) {
       return new EventChecker()
                .isInstanceCreated()
                .isBlock()
                .test(evt);
    }

    @Override
    public void debug(Object o) {

    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        Class block = (Class) e.getSource();
        block.setName("B shall prevail");
        return e;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
