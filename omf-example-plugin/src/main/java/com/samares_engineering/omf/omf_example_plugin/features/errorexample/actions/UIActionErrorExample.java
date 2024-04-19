/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;

import java.util.List;

@BrowserAction
@DiagramAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "OMF ERROR EXAMPLE", category = "OMF.ERROR Example")
public class UIActionErrorExample extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return true;
    }


    @Override
    public void actionToPerform(List<Element> selectedElements) {
        Class block = SysMLFactory.getInstance().createBlock(selectedElements.get(0).getOwner());
        block.setName("SHALL BE ROLLED BACKED");
        throw new OMFCriticalException2(new OMFLog2()
                .bold("TESTING Framework ERROR")
                .text("By throwing this exception, the framework will rollback the session and display a message to the user.\n")
                .italic("Modifiers can be added to prevent rollback, or to make this exception silent."));

    }

    @Override
    protected void executeDiagramAction(List<Element> selectedElements) {
        super.executeDiagramAction(selectedElements);
    }


}