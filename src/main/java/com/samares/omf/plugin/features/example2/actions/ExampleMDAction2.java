/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.example2.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.samares.omf.core.feature.registrables.actions.actions.AUIAction;
import com.samares.omf.core.feature.registrables.actions.actions.annotations.*;
import com.samares.omf.core.utils.ColorPrinter;
import com.samares.omf.core.utils.OMFUtils;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "[B] print their name", category = "Feature")
public class ExampleMDAction2 extends AUIAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null)
            return false;
        return true;
    }


    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(selectedElements == null)
            return;
        selectedElements.stream()
                .filter(NamedElement.class::isInstance)
                .map(NamedElement.class::cast)
                .forEach(e -> ColorPrinter.status(e.getName()));

    }
}