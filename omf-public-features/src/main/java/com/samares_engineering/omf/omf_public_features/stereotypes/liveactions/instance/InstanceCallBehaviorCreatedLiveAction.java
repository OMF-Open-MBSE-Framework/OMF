/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes.liveactions.instance;

import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesEnvOptionsHelper;
import com.samares_engineering.omf.omf_public_features.stereotypes.utils.StereotypesLiveActionsUtils;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class InstanceCallBehaviorCreatedLiveAction extends ALiveAction {
    public String strDefinition;
    public String strInstance;
    public ArrayList<String> strOwner;
    public Class classDefinition;
    public Class classInstance;

    public InstanceCallBehaviorCreatedLiveAction(String id, Class classParent, String strParent, Class classInstance, String strInstance, String strOwner) {
        this(id, classParent, strParent, classInstance, strInstance, new ArrayList<>(Arrays.asList(strOwner.split("/"))));
    }

    public InstanceCallBehaviorCreatedLiveAction(String id, Class classDefinition, String strDefinition, Class classInstance, String strInstance, ArrayList<String> strOwner) {
        super(id);
        this.classDefinition = classDefinition;
        this.classInstance = classInstance;
        this.strDefinition = strDefinition;
        this.strInstance = strInstance;
        this.strOwner = strOwner;
    }

    @Override
    public boolean eventMatches(PropertyChangeEvent evt) {
        if (!StereotypesEnvOptionsHelper.getInstance(getFeature()).isInstanceActivated()) {
            return false;
        }
        if (evt.getSource() instanceof Action) {
            Action action = (Action) evt.getSource();
            if (null != evt.getSource()) {
                if (StereotypesLiveActionsUtils.isParentBehaviorInstantiationPatternSatisfied(action, this.strDefinition) &&
                        StereotypesLiveActionsUtils.ownerHasStereotype(action, this.strOwner)) {
                    System.out.println("[Test]-Part: " + action.getHumanName() + " TRUE" + "\n" + "ID : " + this.id);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        StereotypesLiveActionsUtils.instantiationBehavior(e, this.strInstance);
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
