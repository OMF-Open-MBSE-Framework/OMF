/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.example1.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares.omf.core.feature.registrables.actions.actions.annotations.*;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.plugin.features.example2.ExampleFeature2;
import com.samares.omf.core.feature.registrables.actions.actions.AUIAction;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Register feature B", category = "OMF TEST")
public class ExampleMDAction1 extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null)
            return false;
        if(selectedElements.isEmpty()) return false;

        return selectedElements.stream().anyMatch(Port.class::isInstance);
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(selectedElements == null)
            return;
        getFeature().getPlugin().getFeatureRegister().registerFeature(new ExampleFeature2());

        
//        new OpenProject("C:\\workspace\\DEV\\SAMARES\\OMF\\src\\main\\resources\\OMF_DEVELOPING.mdzip").testAction();

    }



}