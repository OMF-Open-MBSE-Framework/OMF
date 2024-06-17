package com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Region;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.State;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.StateMachine;
import com.nomagic.uml2.ext.magicdraw.statemachines.mdbehaviorstatemachines.Transition;
import com.nomagic.uml2.impl.ElementsFactory;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;

import java.util.List;

@MenuAction
@BrowserAction
@DiagramAction
@MDAction(actionName = "Create STM", category = "OMF")
public class CreateStateMachineAction extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return selectedElements.size() == 1;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
       try {
            Element owner = selectedElements.get(0);
            ElementsFactory factory = SysMLFactory.getInstance().getMagicDrawFactory();
            StateMachine stm = factory.createStateMachineInstance();
            stm.setName("New State Machine");
            stm.setOwner(owner);

            Region region = stm.getRegion().iterator().next();

            State stateA = factory.createStateInstance();
            stateA.setName("State A");
            stateA.setOwner(region);

            State stateB = factory.createStateInstance();
            stateB.setName("State B");
            stateB.setOwner(region);

            Transition transition = factory.createTransitionInstance();
            transition.setName("a-b");
            transition.setOwner(region);
            transition.setSource(stateA);
            transition.setTarget(stateB);

           Diagram diagram = ModelElementsManager.getInstance().createDiagram("SysML State Machine Diagram", stm);
           diagram.setName("New State Machine Diagram");

       }catch (Exception e){
           throw new OMFCriticalException("Error while creating State Machine", e);
       }


    }
}
