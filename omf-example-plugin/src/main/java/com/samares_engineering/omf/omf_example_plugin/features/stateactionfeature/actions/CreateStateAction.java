package com.samares_engineering.omf.omf_example_plugin.features.stateactionfeature.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.stateactions.StateAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.List;

@DiagramAction
@MDAction(actionName = "State Action example", category = "")
public class CreateStateAction extends StateAction {
    boolean isChecked = false;

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return OMFUtils.isProjectOpened();
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        isChecked = true;

    }

    @Override
    public boolean checkState(List<Element> selectedElement) {
        return isChecked;
    }
}
