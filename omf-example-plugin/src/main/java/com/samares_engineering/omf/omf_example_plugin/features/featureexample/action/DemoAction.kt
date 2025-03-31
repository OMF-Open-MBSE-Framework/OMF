package com.samares_engineering.omf.omf_example_plugin.features.featureexample.action

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction


@BrowserAction // This action will be available in the browser
@DiagramAction // This action will be available in the diagram
@MenuAction // This action will be available in the menu
@DeactivateListener //Deactivates the listeners when triggered
//Action will be available under "DEMO.Demo Action" in the menu
@MDAction(actionName = "Demo Action", category = "DEMO")
class DemoAction: AUIAction() {

    //Check when the action is available (displayed when right click on the element)
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return isProjectOpened
    }

    //This method is called when the action is triggered
    //Actions is performed inside a session, and inside OMFBarrier
    override fun actionToPerform(selectedElements: List<Element>) {
        OMFLogger2.toNotification().success("Demo Action performed")
    }
}