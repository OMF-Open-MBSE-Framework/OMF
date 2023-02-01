package com.samares.omf.plugin.test.feature.mdActions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.actions.v2.annotations.BrowserAction;
import com.samares.omf.core.actions.v2.annotations.DiagramAction;
import com.samares.omf.core.actions.v2.annotations.MDAction;
import com.samares.omf.core.utils.OMFUtils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.List;

@DiagramAction
@BrowserAction
@MDAction(actionName = "Copy Element ID", category = "",
        keyStroke = "control shift c")
public class CopyElementIDAction extends AGenericAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null) return false;
        return true;
    }



    @Override
    public void actionToPerform(List<Element> l_selected) {
        if(l_selected == null || l_selected.size() !=1) return;
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(l_selected.get(0).getLocalID());
        clip.setContents(tText, null);

    }



}