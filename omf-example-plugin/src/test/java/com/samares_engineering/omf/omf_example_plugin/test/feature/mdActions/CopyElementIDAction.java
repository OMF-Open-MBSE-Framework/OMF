/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.test.feature.mdActions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;

@DiagramAction
@BrowserAction
@MDAction(actionName = "Copy Element ID",
        category = "OMF Test",
        keyStroke = "control shift c")
public class CopyElementIDAction extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return isProjectOpened();
    }
    
    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(selectedElements == null || selectedElements.size() !=1) return;
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(selectedElements.get(0).getLocalID());
        clip.setContents(tText, null);

    }
}