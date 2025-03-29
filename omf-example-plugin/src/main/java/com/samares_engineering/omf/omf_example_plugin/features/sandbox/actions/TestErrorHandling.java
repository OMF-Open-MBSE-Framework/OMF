package com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;

import java.io.FileNotFoundException;
import java.util.List;

import static com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier.DEACTIVATE_FEATURE;

@MenuAction
@BrowserAction
@DiagramAction
@MDAction(actionName = "Error Handling test", category = "OMF")
public class TestErrorHandling extends AUIAction {
    @Override
    public void actionToPerform(List<Element> selectedElements) {
        // Element created to test rollback
        Class createdClass = Application.getInstance().getProject().getElementsFactory().createClassInstance();
        createdClass.setOwner(selectedElements.get(0));

        // Open file
        String path = "bogus path";
        try {
            throw new FileNotFoundException();
        } catch (FileNotFoundException e) {
            throw new OMFCriticalException("hgkjhg", e, DEACTIVATE_FEATURE);
        }
    }

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return selectedElements.size() == 1;
    }

}
