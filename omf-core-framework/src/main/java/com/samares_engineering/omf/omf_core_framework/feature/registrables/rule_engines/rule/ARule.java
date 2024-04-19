/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule;

import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.ILiveAction;

import java.beans.PropertyChangeEvent;

public abstract class ARule implements IRule<PropertyChangeEvent, PropertyChangeEvent> {
    protected ILiveAction ruleEngine;
    public String id = "";
    public boolean isActivated = true;

    public ARule(){}

    public ARule(String id){
        this.id = id;
    }

    @Override
    public final boolean matches(PropertyChangeEvent evt) {
        return isActivated && eventMatches(evt);
    }

    protected abstract boolean eventMatches(PropertyChangeEvent evt);

    @Deprecated
    public void debug(Object o){}

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

    public void setRuleEngine(ILiveAction ruleEngine) {
        this.ruleEngine = ruleEngine;
    }

    public MDFeature getFeature() {
        return ruleEngine.getFeature();
    }
}
