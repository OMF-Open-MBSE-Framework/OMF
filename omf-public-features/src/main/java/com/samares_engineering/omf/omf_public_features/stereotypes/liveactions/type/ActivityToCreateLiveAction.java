/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes.liveactions.type;

import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesEnvOptionsHelper;
import com.samares_engineering.omf.omf_public_features.stereotypes.utils.StereotypesLiveActionsUtils;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class ActivityToCreateLiveAction extends ALiveAction {
    public String strInstance   = "";
    public String strType = "";
    public Element owner = null;
    public ArrayList<String> strOwner   = null;

    public ActivityToCreateLiveAction(String id, String strInstance, String strType, Element owner){
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
                return StereotypesLiveActionsUtils.isInstanceActionWithStr(action, this.strInstance) &&
                        StereotypesLiveActionsUtils.isCBATypeNull(action);
            }
        }
        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        StereotypesLiveActionsUtils.createActivityTypeBehavior(e, this.strType);
        StereotypesLiveActionsUtils.organizeType(e, owner);
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
