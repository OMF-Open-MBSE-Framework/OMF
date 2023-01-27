/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.plugin.features.organizer.rules.type2instance;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.listeners.ruleEngineListener.rules.A_Rule;
import com.samares.omf.core.utils.errorManagement.OMFErrorHandler;
import com.samares.omf.core.utils.errorManagement.exceptions.OMFException;
import com.samares.omf.plugin.features.organizer.utils.OrganizerRuleUtils;
import com.samares.omf.plugin.options.OMFPluginEnvOptionsGroup;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;

public class InstancePropertyCreatedRule extends A_Rule {
    public final Class classOfType;
    public final Class classOfInstance;
    public final String stereoOfType;
    public final String stereoOfInstance;
    public final List<String> ownerValidStereotypes;

    public InstancePropertyCreatedRule(String id, Class classOfType, String stereoOfType, Class classOfInstance,
                                       String stereoOfInstance, String ownerValidStereotypes){
        this(id, classOfType, stereoOfType, classOfInstance, stereoOfInstance, Arrays.asList(ownerValidStereotypes.split("/")));
    }

    public InstancePropertyCreatedRule(String id, Class classOfType, String stereoOfType, Class classOfInstance,
                                       String stereoOfInstance, List<String> ownerValidStereotypes){
        super(id);
        this.stereoOfType = stereoOfType;
        this.classOfType = classOfType;
        this.stereoOfInstance = stereoOfInstance;
        this.classOfInstance = classOfInstance;
        this.ownerValidStereotypes = ownerValidStereotypes;
    }

    @Override
    public boolean eventMatches(PropertyChangeEvent evt) {
       if (!OMFPluginEnvOptionsGroup.getInstance().isT2IActivated()) {
            return false;
        }
        if (evt.getSource() == null) {
            return false;
        }
        if(!classOfInstance.isInstance(evt.getSource())) {
            return false;
        }
        Element srcElement = (Element) evt.getSource();
        boolean isTypeInstantiationPatternSatisfied = OrganizerRuleUtils.isTypeInstantiationPatternSatisfied(
                srcElement, classOfInstance, this.stereoOfType, this.classOfType);
        boolean ownerHasSpecifiedStereotype = OrganizerRuleUtils.ownerHasStereotype(srcElement, this.ownerValidStereotypes);
        if(isTypeInstantiationPatternSatisfied && ownerHasSpecifiedStereotype) {
            System.out.println("[Test]-Part: " + srcElement.getHumanName() + " TRUE" + "\n" + "ID : " + this.id);
            return true;
        }
        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        try {
            OrganizerRuleUtils.instantiationBehavior(e, this.stereoOfInstance);
        } catch (OMFException ex) {
            OMFErrorHandler.handleException(ex, false);
        }
        return e;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }

    @Override
    public void debug(Object o) {

    }
}
