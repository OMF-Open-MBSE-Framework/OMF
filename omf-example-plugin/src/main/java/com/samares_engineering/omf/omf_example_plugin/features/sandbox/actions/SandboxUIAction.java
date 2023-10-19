package com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;
import com.samares_engineering.omf.omf_core_framework.logger2.UIMessage;

import java.util.List;

@MenuAction
@BrowserAction
@DiagramAction
@MDAction(actionName = "Sandbox UI Action", category = "OMF")
public class SandboxUIAction extends AUIAction {
    @Override
    public void actionToPerform(List<Element> selectedElements) {
        UIMessage.info().text("Test error ").link("link to element 1", selectedElements.get(0))
                .bold("bold").italic("italic").underline("underscore").strike("striketrough")
                .link("link to element 2", selectedElements.get(1))
                .linkAction("Custom Action link", () -> UIMessage.info().text("bonus log !").logToConsole())
                .text("<A href=\"file://C:/Users/HugoStinson/OneDrive - SAMARES ENGINEERING/Bureau/NOTES MENU OMF.txt\"> lien vers mon crashlog> </A>")
                .text("<A href=\"https://www.youtube.com/watch?v=dQw4w9WgXcQ\"> lien bonus> </A>")
                .logToConsole();
    }

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return selectedElements.size() == 2;
    }
}
