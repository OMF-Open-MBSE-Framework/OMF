/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.CriticalFeatureException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.List;

@BrowserAction
@DiagramAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "OMF CRITICAL ERROR EXAMPLE", category = "OMF.ACTION CATEGORY NAME")
public class CriticalFeatureExampleAction extends AUIAction {
    Runnable runnable;
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return OMFUtils.currentProject != null;
    }


    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            throw new RuntimeException("TESTING Framework CRITICAL FEATURE ERROR");
        } catch (Exception criticalError) {
            OMFErrorHandler.handleException(new CriticalFeatureException(
                    "Example Error Feature has crashed as expected." +
                    "\n the whole feature will be removed, and wont be available." +
                            "If you want to reactivate it please go in the environment option and reactivate it.",
                    getFeature(), criticalError, GenericException.ECriticality.CRITICAL), true);
        }
    }

    @Override
    protected void executeDiagramAction(List<Element> selectedElements) {
        super.executeDiagramAction(selectedElements);
    }


}