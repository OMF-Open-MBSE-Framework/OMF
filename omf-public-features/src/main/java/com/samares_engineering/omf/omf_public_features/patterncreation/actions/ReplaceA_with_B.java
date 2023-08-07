/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.patterncreation.actions;

import com.nomagic.magicdraw.uml.ConvertElementInfo;
import com.nomagic.magicdraw.uml.Refactoring;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.List;

@DiagramAction
@BrowserAction
@DeactivateListener
@MDAction(actionName = "Replace A with B", category = "***PATTERNS")
public class ReplaceA_with_B extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null) return false;
        if(selectedElements.size() != 2) return false;
       return true;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            Element elemA = selectedElements.get(0);
            Refactoring.Replacing.replace(elemA, selectedElements.get(1), new ConvertElementInfo(elemA.getClass()));
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, true);
        }
    }



}