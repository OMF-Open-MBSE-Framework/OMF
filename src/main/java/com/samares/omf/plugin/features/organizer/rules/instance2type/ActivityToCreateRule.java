/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.plugin.features.organizer.rules.instance2type;

import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.listeners.ruleEngineListener.rules.A_Rule;
import com.samares.omf.plugin.features.organizer.utils.OrganizerRuleUtils;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class ActivityToCreateRule extends A_Rule {
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
        if (!OMFPluginEnvOptionsGroup.getInstance().isI2TActivated()) {
            return false;
        }
        if (evt.getSource() instanceof Action) {
            Action action = (Action) evt.getSource();

            boolean isElementStillInCreation = action.getOwner() == null;
            if(isElementStillInCreation)
                return false;

            if (null != evt.getSource()) {
                return OrganizerRuleUtils.isInstanceActionWithStr(action, this.strInstance) &&
                        OrganizerRuleUtils.isCBATypeNull(action);
            }
        }
        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        OrganizerRuleUtils.createActivityTypeBehavior(e, this.strType);
        OrganizerRuleUtils.organizeType(e, owner);
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
