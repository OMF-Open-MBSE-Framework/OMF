package com.samares.omf.plugin.features.example2.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.actions.v2.annotations.*;
import com.samares.omf.core.utils.ColorPrinter;
import com.samares.omf.core.utils.OMFUtils;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "[B] print their name", category = "Feature")
public class ExampleMDAction2 extends AGenericAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null)
            return false;
        return true;
    }


    @Override
    public void actionToPerform(List<Element> l_selected) {
        if(l_selected == null)
            return;
        l_selected.stream()
                .filter(NamedElement.class::isInstance)
                .map(NamedElement.class::cast)
                .forEach(e -> ColorPrinter.status(e.getName()));

    }
}