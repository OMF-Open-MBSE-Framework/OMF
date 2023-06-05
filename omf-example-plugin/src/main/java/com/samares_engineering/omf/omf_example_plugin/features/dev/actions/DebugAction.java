/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.dev.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;

import java.util.List;

@BrowserAction
@MDAction(actionName = "Debug Action", category = "Dev")
public class DebugAction extends AUIAction {

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            selectedElements.stream()
                    .filter(NamedElement.class::isInstance)
                    .map(NamedElement.class::cast)
                    .forEach(namedElement -> namedElement.setName("RENAMED" + namedElement.getName()));
        }catch (Exception e){
            OMFErrorHandler.handleException(e);
        }
    }

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return true;
    }
}
