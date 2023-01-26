package com.samares.omf.plugin.features.feature_A.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares.omf.core.actions.v2.annotations.*;
import com.samares.omf.plugin.features.feature_B.Feature_B;
import com.samares.omf.plugin.OpenMBSEFrameworkPlugin;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.utils.OMFUtils;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Register feature B", category = "Feature")
public class F_A_MDAction extends AGenericAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null)
            return false;
        if(selectedElements.isEmpty()) return false;

        if(selectedElements.stream().anyMatch(Port.class::isInstance)) return true;

        return false;
    }



    @Override
    public void actionToPerform(List<Element> l_selected) {
        if(l_selected == null)
            return;
        OpenMBSEFrameworkPlugin.getInstance().getFeatureRegister().registerFeature(new Feature_B());

        
//        new OpenProject("C:\\workspace\\DEV\\SAMARES\\OMF\\src\\main\\resources\\OMF_DEVELOPING.mdzip").testAction();

    }



}