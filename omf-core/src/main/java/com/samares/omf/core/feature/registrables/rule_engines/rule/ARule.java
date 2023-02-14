/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.feature.registrables.rule_engines.rule;

import java.beans.PropertyChangeEvent;

public abstract class ARule implements IRule<PropertyChangeEvent, PropertyChangeEvent> {

    public String id = "";
    public boolean isActivated = true;
    public ARule(){};

    public ARule(String id){
        this.id = id;
    }

    @Override
    public final boolean matches(PropertyChangeEvent evt) {
        return isActivated && eventMatches(evt);
    }

    protected abstract boolean eventMatches(PropertyChangeEvent evt);

    public abstract void debug(Object o);

    @Override
    public boolean isActivated() {
        return isActivated;
    }

    @Override
    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    @Override
    public String getId() {
        return id;
    }
}
