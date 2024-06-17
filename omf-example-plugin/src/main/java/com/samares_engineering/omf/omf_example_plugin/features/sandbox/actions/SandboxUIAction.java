package com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;

import java.util.List;

@MenuAction
@BrowserAction
@DiagramAction
@MDAction(actionName = "Sandbox UI Action", category = "OMF")
public class SandboxUIAction extends AUIAction {
    @Override
    public void actionToPerform(List<Element> selectedElements) {
        new OMFLog().text("Test error ").linkElement("link to element 1", selectedElements.get(0))
                .bold("bold").italic("italic").underline("underscore").strike("striketrough")
                .linkElement("link to element 2", selectedElements.get(1))
                .linkAction("Custom Action link", () -> new OMFLog().text("bonus log !").logToUiConsole(OMFLogLevel.WARNING, getFeature()))
                .link("lien vers mon crashlog", "file://C:/Users/HugoStinson/OneDrive - SAMARES ENGINEERING/Bureau/NOTES MENU OMF.txt")
                .link("lien bonus", "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                .logToUiConsole(OMFLogLevel.INFO);
    }

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return selectedElements.size() == 2;
    }
}
