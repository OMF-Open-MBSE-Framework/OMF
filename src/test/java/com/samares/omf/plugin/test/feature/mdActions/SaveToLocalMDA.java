package com.samares.omf.plugin.test.feature.mdActions;

import com.nomagic.magicdraw.esi.EsiUtils;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.actions.v2.annotations.BrowserAction;
import com.samares.omf.core.actions.v2.annotations.DiagramAction;
import com.samares.omf.core.actions.v2.annotations.MDAction;
import com.samares.omf.core.utils.OMFUtils;

import java.io.File;
import java.util.List;

@DiagramAction
@BrowserAction
@MDAction(actionName = "SAVE TO LOCAL", category = "",
        keyStroke = "control shift c")
public class SaveToLocalMDA extends AGenericAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null)
            return false;
        return true;
    }



    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(selectedElements == null || selectedElements.size() !=1) return;

        File save = new File("c:/TMP/"+ OMFUtils.currentProject.getName() + ".mdzip");
        EsiUtils.convertToLocal(OMFUtils.currentProject, save);

    }



}