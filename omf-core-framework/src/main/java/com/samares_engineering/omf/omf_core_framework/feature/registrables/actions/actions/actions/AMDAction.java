/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.actions;


import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.MenuAction;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@MDAction(actionName = "Action Name", category = "Category Name")
public class AMDAction extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return true;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {

    }



}
