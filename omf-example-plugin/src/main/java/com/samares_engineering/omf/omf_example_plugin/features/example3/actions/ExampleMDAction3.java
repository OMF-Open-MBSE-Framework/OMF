/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.example3.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_example_plugin.features.example2.ExampleFeature2;


import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "unregister Feature B", category = "Feature")
public class ExampleMDAction3 extends AUIAction {
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
                .filter(ExampleFeature2.class::isInstance)
                .findFirst()
                .ifPresent(featureManager::unregisterFeature);

    }
}