/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.plugin.features.stereotypes.rules.type;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.samares.omf.core.listeners.ruleEngineListener.rules.ARule;
import com.samares.omf.plugin.features.stereotypes.utils.StereotypesRuleUtils;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

import java.beans.PropertyChangeEvent;

public class ClassToCreateRule extends ARule {
    public final String strInstance;
    public final String strType;
    public final Element owner;

    public ClassToCreateRule(String id, String strInstance, String strType, Element owner){
        super(id);
        this.strInstance  = strInstance;
        this.strType    = strType;
        this.owner      = owner;
    }

    @Override
    public boolean eventMatches(PropertyChangeEvent evt) {
        if (!OMFPluginEnvOptionsGroup.getInstance().isTypeActivated()) {
            return false;
        }
        if (evt.getSource() instanceof Property) {
            Property part = (Property) evt.getSource();
            if(part.getOwner() == null)
                return false;
            if (null != evt.getSource() &&  StereotypesRuleUtils.isInstancePropertyWithStr(part, this.strInstance) &&
                        StereotypesRuleUtils.isTypeElementTypeNull(part)) {
                System.out.println("[Test]-Part: " + part.getHumanName() + " TRUE" + "\n" + "ID : " + this.id);
                return true;
            }
        }
        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent evt) {
        StereotypesRuleUtils.createTypeBehavior(evt, this.strType);
        StereotypesRuleUtils.organizeType(evt, owner);
        return evt;
    }

    @Override
    public void debug(Object o) {

    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
