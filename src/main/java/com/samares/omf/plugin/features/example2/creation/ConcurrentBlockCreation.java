/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.example2.creation;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares.omf.core.feature.registrables.rule_engines.rule.ARule;
import com.samares.omf.plugin.features.stereotypes.utils.EventChecker;

import java.beans.PropertyChangeEvent;

public class ConcurrentBlockCreation extends ARule {
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
