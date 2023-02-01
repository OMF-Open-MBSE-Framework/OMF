/*
 * @copyright Copyright (c) 2021 Airbus SAS
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.1.0
 */

package com.samares.omf.core.actions.v2.actions;


import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.actions.v2.annotations.BrowserAction;
import com.samares.omf.core.actions.v2.annotations.DiagramAction;
import com.samares.omf.core.actions.v2.annotations.MDAction;
import com.samares.omf.core.actions.v2.annotations.MenuAction;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@MDAction(actionName = "Action Name", category = "Category Name")
public class AMDAction extends AGenericAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return true;
    }

    @Override
    public void actionToPerform(List<Element> l_selected) {

    }



}
