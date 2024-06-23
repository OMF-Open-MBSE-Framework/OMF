/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_public_features.stereotypes.liveactions.instance;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction;
import com.samares_engineering.omf.omf_public_features.stereotypes.StereotypesEnvOptionsHelper;
import com.samares_engineering.omf.omf_public_features.stereotypes.utils.StereotypesLiveActionsUtils;
import org.apache.logging.log4j.util.Strings;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InstancePropertyCreatedLiveAction extends ALiveAction {
    public final Class classOfType;
    public final Class classOfInstance;
    public final String stereoOfType;
    public final String stereoOfInstance;
    public final List<String> ownerValidStereotypes;

    public InstancePropertyCreatedLiveAction(String id, Class classOfType, String stereoOfType, Class classOfInstance,
                                             String stereoOfInstance, String ownerValidStereotypes) {
        this(id, classOfType, stereoOfType, classOfInstance, stereoOfInstance,
                Arrays.stream(ownerValidStereotypes.split("/"))
                        .filter(Strings::isNotEmpty)
                        .collect(Collectors.toList()));
    }

    public InstancePropertyCreatedLiveAction(String id, Class classOfType, String stereoOfType, Class classOfInstance,
                                             String stereoOfInstance, List<String> ownerValidStereotypes) {
        super(id);
        this.stereoOfType = stereoOfType;
        this.classOfType = classOfType;
        this.stereoOfInstance = stereoOfInstance;
        this.classOfInstance = classOfInstance;
        this.ownerValidStereotypes = ownerValidStereotypes;
    }

    @Override
    public boolean eventMatches(PropertyChangeEvent evt) {
        if (!StereotypesEnvOptionsHelper.getInstance(getFeature()).isInstanceActivated()) return false;
        if (evt.getSource() == null) return false;
        if (!classOfInstance.isInstance(evt.getSource())) return false;

        Element srcElement = (Element) evt.getSource();
        boolean isTypeInstantiationPatternSatisfied = StereotypesLiveActionsUtils.isTypeInstantiationPatternSatisfied(
                srcElement, classOfInstance, this.stereoOfType, this.classOfType);
        boolean ownerHasSpecifiedStereotype = StereotypesLiveActionsUtils.ownerHasStereotype(srcElement, this.ownerValidStereotypes);
        if (isTypeInstantiationPatternSatisfied && ownerHasSpecifiedStereotype) {
            System.out.println("[Test]-Part: " + srcElement.getHumanName() + " TRUE" + "\n" + "ID : " + this.id);
            return true;
        }
        return false;
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        StereotypesLiveActionsUtils.instantiationBehavior(e, this.stereoOfInstance);
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
