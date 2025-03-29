package com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.uml.symbols.CompartmentID;
import com.nomagic.magicdraw.uml.symbols.CompartmentManager;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;

import java.util.List;

@MenuAction
@DiagramAction
@MDAction(actionName = "Test compartments", category = "OMF")
public class TestCompartmentsUIAction extends AUIAction {
    @Override
    public void actionToPerform(List<Element> selectedElements) {
        Element elem = selectedElements.get(0);
        PresentationElement propPE = Application.getInstance().getProject().getActiveDiagram().getPresentationElements().stream()
                .filter(presentationElement -> presentationElement.getElement().getID().equals(elem.getID()))
                .findFirst().get();


        String idOfStereotypeProperty = "_19_0_3_4240182_1566539720360_968709_13433";
        TaggedValue kindTaggedValue = elem.getTaggedValue().get(0);
        //CompartmentManager.showCompartmentElement(propPE, CompartmentID.TAGGED_VALUES_IN_COMPARTMENT, // WORKS
        //        (Element) Application.getInstance().getProject().getElementByID(idOfStereotypeProperty));
        //CompartmentManager.showCompartmentElement(propPE, CompartmentID.TAGGED_VALUES_IN_COMPARTMENT, // WORKS
        //        kindTaggedValue.getTagDefinition());
        //CompartmentManager.showCompartmentElement(propPE, CompartmentID.TAGGED_VALUES_IN_COMPARTMENT, // WORKS
        //        kindTaggedValue);
        CompartmentManager.showCompartmentElement(propPE, CompartmentID.ELEMENT_PROPERTIES, // DOESN'T WORK
                (Element) Application.getInstance().getProject().getElementByID(idOfStereotypeProperty));
        CompartmentManager.showCompartmentElement(propPE, CompartmentID.ELEMENT_PROPERTIES, // DOESN'T WORK
                kindTaggedValue.getTagDefinition());
        CompartmentManager.showCompartmentElement(propPE, CompartmentID.ELEMENT_PROPERTIES, // DOESN'T WORK
                kindTaggedValue);
    }

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return selectedElements.size() == 1;
    }

}
