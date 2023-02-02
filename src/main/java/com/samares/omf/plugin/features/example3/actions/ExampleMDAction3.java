/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.example3.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.actions.v2.annotations.*;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.plugin.features.example2.ExampleFeature2;
import com.samares.omf.plugin.OpenMBSEFrameworkPlugin;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.feature.FeatureRegister;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "unregister Feature B", category = "Test Category")
public class ExampleMDAction3 extends AGenericAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return OMFUtils.currentProject != null;
    }


    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(selectedElements == null)
            return;

        FeatureRegister featureManager = OpenMBSEFrameworkPlugin.getInstance().getFeatureRegister();

        featureManager.getRegisteredFeatures().stream()
                .filter(ExampleFeature2.class::isInstance)
                .findFirst()
                .ifPresent(featureManager::unregisterFeature);

    }
}