package com.samares.omf.plugin.features.example1.actions;

import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.actions.v2.annotations.*;
import com.samares.omf.core.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares.omf.core.utils.OMFUtils;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Remove Options", category = "Feature")
public class RemoveProjectOptions extends AGenericAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null)
            return false;

        if(selectedElements.isEmpty()) return false;

        return selectedElements.stream().anyMatch(Class.class::isInstance);
    }



    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(selectedElements == null)
            return;
        ProjectOptions.removeConfigurator(FeatureProjectOptionsConfigurator.getInstance());
    }



}