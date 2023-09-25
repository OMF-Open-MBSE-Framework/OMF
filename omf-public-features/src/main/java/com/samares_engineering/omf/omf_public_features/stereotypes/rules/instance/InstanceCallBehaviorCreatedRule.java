/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes.rules.instance;

import com.nomagic.uml2.ext.magicdraw.actions.mdbasicactions.Action;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule.ARule;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesEnvOptionsHelper;
import com.samares_engineering.omf.omf_public_features.stereotypes.utils.StereotypesRuleUtils;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class InstanceCallBehaviorCreatedRule extends ARule {
    public String strDefinition;
    public String strInstance;
    public ArrayList<String> strOwner;
    public Class classDefinition;
    public Class classInstance;

    public InstanceCallBehaviorCreatedRule(String id, Class classParent, String strParent, Class classInstance, String strInstance, String strOwner){
        this(id, classParent, strParent, classInstance, strInstance, new ArrayList<>(Arrays.asList(strOwner.split("/"))));
    }

    public InstanceCallBehaviorCreatedRule(String id, Class classDefinition, String strDefinition, Class classInstance, String strInstance, ArrayList<String> strOwner){
        super(id);
        this.classDefinition = classDefinition;
        this.classInstance   = classInstance;
        this.strDefinition   = strDefinition;
        this.strInstance     = strInstance;
        this.strOwner        = strOwner;
    }

    @Override
    public boolean eventMatches(PropertyChangeEvent evt) {
        if (!StereotypesEnvOptionsHelper.getInstance(getFeature()).isInstanceActivated()) {
            return false;
        }
        if (evt.getSource() instanceof Action) {
            Action action = (Action) evt.getSource();
            if (null != evt.getSource()) {
                if( StereotypesRuleUtils.isParentBehaviorInstantiationPatternSatisfied(action, this.strDefinition) &&
                        StereotypesRuleUtils.ownerHasStereotype(action, this.strOwner)) {
                    System.out.println("[Test]-Part: " + action.getHumanName() + " TRUE" + "\n" + "ID : " + this.id);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        try {
            StereotypesRuleUtils.instantiationBehavior(e, this.strInstance);
        } catch (OMFException ex) {
            OMFErrorHandler.handleException(ex, false);
        }
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
