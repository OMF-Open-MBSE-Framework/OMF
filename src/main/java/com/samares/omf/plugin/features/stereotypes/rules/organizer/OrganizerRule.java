/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.plugin.features.stereotypes.rules.organizer;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.feature.registrables.rule_engines.rule.ARule;
import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.exceptions.OMFException;
import com.samares.omf.plugin.features.stereotypes.utils.StereotypesRuleUtils;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class OrganizerRule extends ARule {
    public String idOption;
    public String strInstance = "";
    public Class classInstance = null;
    public Element owner = null;
    public ArrayList<String> strOwner = null;

    public OrganizerRule(String id, String idOption, String strInstance, Class classInstance, Element owner) {
        super(id);
        this.strInstance = strInstance;
        this.owner = owner;
        this.classInstance = classInstance;
        this.idOption = idOption;
        OMFPluginEnvOptionsGroup.getInstance().addOwnerPropertyOption(idOption, owner);
    }

    @Override
    public boolean eventMatches(PropertyChangeEvent evt) {
        if (!OMFPluginEnvOptionsGroup.getInstance().isOrganizerActivated()) {
            return false;
        }
        if (null == evt.getSource() || !classInstance.isInstance(evt.getSource()))
            return false;
        Element element = (Element) evt.getSource();

        if (element.getOwner() == null)   //still in creation
            return false;

        if (StereotypesRuleUtils.hasStereotype(element, this.strInstance)) {
            System.out.println("[Test]-" + classInstance.getName() + ": "
                    + element.getHumanName() + " TRUE" + "\n" + "ID : " + this.id);
            return true;
        }

        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent evt) {
        owner =
                ((OMFPluginEnvOptionsGroup) Application.getInstance().getEnvironmentOptions().getGroup(OMFPluginEnvOptionsGroup.ID))
                        .getOwnerPropertyOption(idOption);

        if (owner == null) {
            OMFErrorHandler.handleException(new OMFException("[Organizer] " +
                    "\n Storage element for: \"" + idOption + "\" is not defined", OMFException.ECriticality.ALERT), false);
            return evt;
        }

        StereotypesRuleUtils.organizeOwner(evt, owner);
        return evt;
    }

    public enum SearchMethod {
        FIND_FIRST_DOWNWARD,
        FIND_FIRST_UPWARD
    }

    @Override
    public void debug(Object o) {

    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
