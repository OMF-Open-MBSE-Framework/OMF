/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.example3.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.feature.registrables.actions.actions.annotations.*;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.feature.registrables.actions.actions.AUIAction;
import com.samares.omf.core.feature.FeatureRegisterer;
import com.samares.omf.plugin.features.example1.ExampleFeature1;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Remove FEATURE A", category = "Feature")
public class RemoveFeatureA extends AUIAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return OMFUtils.currentProject != null;
    }


    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(selectedElements == null)
            return;

        FeatureRegisterer featureManager = feature.getPlugin().getFeatureRegister();
        featureManager.getRegisteredFeatures().stream()
                .filter(ExampleFeature1.class::isInstance)
                .findFirst()
                .ifPresent(featureManager::unregisterFeature);

    }
}