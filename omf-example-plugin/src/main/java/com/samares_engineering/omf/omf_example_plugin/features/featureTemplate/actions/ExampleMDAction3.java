/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.featureTemplate.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Subcategory2", category = "OMF.ACTION CATEGORY NAME.Subcategory1")
public class ExampleMDAction3 extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return isProjectOpened() && selectedElements.size() == 1;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        SysMLFactory.getInstance().createBlock(selectedElements.get(0));
    }


}