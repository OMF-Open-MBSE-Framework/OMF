/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.clonefeature.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.clone.CloneManager;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.List;

@BrowserAction
@DiagramAction
@DeactivateListener
@MDAction(actionName = "Clone Property", category = "Clone")
public class CloneProperty extends AUIAction {

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return OMFUtils.currentProject != null
                && !selectedElements.isEmpty()
                && selectedElements.stream().allMatch(Property.class::isInstance);
    }


    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            CloneManager cloneManager = new CloneManager();
            selectedElements.stream()
                    .map(Property.class::cast)
                    .forEach(cloneManager::cloneProperty);
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, true);
        }
    }


}