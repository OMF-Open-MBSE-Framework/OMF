package com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

import static com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFExceptionModifier.DEACTIVATE_FEATURE;
import static com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFExceptionModifier.ROLLBACK_CHANGES;

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
            // OMFErrorHandler.handleException(e, true);
            throw new OMFException2(new OMFLog().text("Can't find file").bold(path), e, ROLLBACK_CHANGES);
        }
    }

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return selectedElements.size() == 1;
    }

}
