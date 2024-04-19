/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.List;

@MenuAction
@DiagramAction
@BrowserAction
@MDAction(actionName = "Propagate Port Name to Interface/Flow", category = "SysMLBasic")
public class SyncAllNameAction extends AUIAction {


    /**
     * Available only When a Typed Port is selected
     * @param selectedElements selected elements from the browser, or the diagram
     * @return true if the action is available
     */
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        boolean isPortSelected = selectedElements.size() == 1 && selectedElements.get(0) instanceof Port;
        boolean isPortWithInterface = isPortSelected && ((Port) selectedElements.get(0)).getType() != null;
        return isPortWithInterface;
    }


    /**
     * Will rename the Interface and the FlowProperty with the port name
     * @param selectedElements selected elements
     */
    public void actionToPerform(List<Element> selectedElements) {
        //OLD CODE
        Port port = (Port) selectedElements.get(0);

        if (port.getType() == null) return;

        port.getType().setName(port.getName());

        port.getType().getOwnedElement().stream()
                .filter(Profile._getSysml().flowProperty()::is)
                .map(Property.class::cast)
                .forEach(property -> property.setName(port.getName()));

    }
}
