/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.example1.actions;

import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.ui.projectoptions.FeatureProjectOptionsConfigurator;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.*;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Remove Options", category = "Feature")
public class RemoveProjectOptions extends AUIAction {


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