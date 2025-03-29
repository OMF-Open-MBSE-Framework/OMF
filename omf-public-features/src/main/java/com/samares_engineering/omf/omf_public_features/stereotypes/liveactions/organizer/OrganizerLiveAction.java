/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes.liveactions.organizer;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesEnvOptionsHelper;
import com.samares_engineering.omf.omf_public_features.stereotypes.utils.StereotypesLiveActionsUtils;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;

public class OrganizerLiveAction extends ALiveAction {
    public String idOption;
    public String strInstance = "";
    public Class classInstance = null;
    public Element owner = null;
    public ArrayList<String> strOwner = null;

    public OrganizerLiveAction(String id, String idOption, String strInstance, Class classInstance, Element owner) {
        super(id);
        this.strInstance = strInstance;
        this.owner = owner;
        this.classInstance = classInstance;
        this.idOption = idOption;
        StereotypesEnvOptionsHelper.getInstance(getFeature()).addOwnerPropertyOption(idOption, owner);
    }

    @Override
    public boolean eventMatches(PropertyChangeEvent evt) {
        if (!StereotypesEnvOptionsHelper.getInstance(getFeature()).isOrganizerActivated()) {
            return false;
        }
        if (null == evt.getSource() || !classInstance.isInstance(evt.getSource()))
            return false;
        Element element = (Element) evt.getSource();

        if (element.getOwner() == null)   //still in creation
            return false;

        if (StereotypesLiveActionsUtils.hasStereotype(element, this.strInstance)) {
            System.out.println("[Test]-" + classInstance.getName() + ": "
                    + element.getHumanName() + " TRUE" + "\n" + "ID : " + this.id);
            return true;
        }

        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent evt) {
        owner = StereotypesEnvOptionsHelper.getInstance(getFeature()).getOwnerPropertyOption(idOption);
        if (owner == null) {
            throw new OMFCriticalException("Storage element for: \"" + idOption + "\" is not defined");
        }
        StereotypesLiveActionsUtils.organizeOwner(evt, owner);
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
