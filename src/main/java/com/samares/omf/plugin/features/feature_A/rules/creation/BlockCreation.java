package com.samares.omf.plugin.features.feature_A.rules.creation;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares.omf.core.listeners.ruleEngineListener.rules.A_Rule;
import com.samares.omf.plugin.features.stereotypes.utils.EventChecker;

import java.beans.PropertyChangeEvent;

public class BlockCreation extends A_Rule {
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
        block.setName("succeed");
        return e;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
