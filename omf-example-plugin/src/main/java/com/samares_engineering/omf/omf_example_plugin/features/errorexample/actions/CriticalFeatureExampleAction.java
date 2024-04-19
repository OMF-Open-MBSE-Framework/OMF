/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;

import java.util.List;

import static com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFExceptionModifier2.*;

@BrowserAction
@DiagramAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "OMF CRITICAL ERROR EXAMPLE", category = "OMF.ACTION CATEGORY NAME")
public class CriticalFeatureExampleAction extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return true;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        throw new OMFCriticalException2("TESTING Framework CRITICAL FEATURE ERROR", NO_ROLLBACK, SILENT, DEACTIVATE_FEATURE);
    }

    @Override
    protected void executeDiagramAction(List<Element> selectedElements) {
        super.executeDiagramAction(selectedElements);
    }
}