/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes.liveactions.type;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesEnvOptionsHelper;
import com.samares_engineering.omf.omf_public_features.stereotypes.utils.StereotypesLiveActionsUtils;

import java.beans.PropertyChangeEvent;

public class ClassToCreateLiveAction extends ALiveAction {
    public final String strInstance;
    public final String strType;
    public final Element owner;

    public ClassToCreateLiveAction(String id, String strInstance, String strType, Element owner){
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
        if (evt.getSource() instanceof Property) {
            Property part = (Property) evt.getSource();
            if(part.getOwner() == null)
                return false;
            if (null != evt.getSource() &&  StereotypesLiveActionsUtils.isInstancePropertyWithStr(part, this.strInstance) &&
                        StereotypesLiveActionsUtils.isTypeElementTypeNull(part)) {
                System.out.println("[Test]-Part: " + part.getHumanName() + " TRUE" + "\n" + "ID : " + this.id);
                return true;
            }
        }
        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent evt) {
        StereotypesLiveActionsUtils.createTypeBehavior(evt, this.strType);
        StereotypesLiveActionsUtils.organizeType(evt, owner);
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
