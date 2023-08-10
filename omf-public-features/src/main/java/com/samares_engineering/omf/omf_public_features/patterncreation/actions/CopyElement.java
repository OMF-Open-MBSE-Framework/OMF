/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.patterncreation.actions;

import com.nomagic.magicdraw.copypaste.CopyPasting;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "copy A element to B", category = "***PATTERNS")
public class CopyElement extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null) return false;
        if(selectedElements.size() != 2) return false;
       return true;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            Element A = selectedElements.get(0);
            Element B = selectedElements.get(1);
            Element element = CopyPasting.copyPasteElement(A, B);

            NamedElement tmp = SysMLFactory.getInstance().createPackage("tmp", OMFUtils.currentProject.getPrimaryModel());
            Element patternOwner = CopyPasting.copyPasteElement(A.getOwner(), tmp);

            System.out.println(element);
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, true);
        }
    }



}