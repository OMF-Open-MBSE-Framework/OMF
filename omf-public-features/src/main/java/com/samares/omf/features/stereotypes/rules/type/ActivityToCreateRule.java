/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.features.stereotypes.rules.type;

import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.feature.registrables.rule_engines.rule.ARule;
import com.samares.omf.features.stereotypes.StereotypesEnvOptionsHelper;
import com.samares.omf.features.stereotypes.utils.StereotypesRuleUtils;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class ActivityToCreateRule extends ARule {
    public String strInstance   = "";
    public String strType = "";
    public Element owner = null;
    public ArrayList<String> strOwner   = null;

    public ActivityToCreateRule(String id, String strInstance, String strType, Element owner){
        super(id);
        this.strInstance  = strInstance;
        this.strType    = strType;
        this.owner      = owner;
    }

    @Override
    public boolean eventMatches(PropertyChangeEvent evt) {
        if (!StereotypesEnvOptionsHelper.getInstance(getFeature()).isTypeActivated()) {
            return false;
        }
        if (evt.getSource() instanceof Action) {
            Action action = (Action) evt.getSource();

            boolean isElementStillInCreation = action.getOwner() == null;
            if(isElementStillInCreation)
                return false;

            if (null != evt.getSource()) {
                return StereotypesRuleUtils.isInstanceActionWithStr(action, this.strInstance) &&
                        StereotypesRuleUtils.isCBATypeNull(action);
            }
        }
        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        StereotypesRuleUtils.createActivityTypeBehavior(e, this.strType);
        StereotypesRuleUtils.organizeType(e, owner);
        return e;
    }

    @Override
    public void debug(Object o) {

    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
