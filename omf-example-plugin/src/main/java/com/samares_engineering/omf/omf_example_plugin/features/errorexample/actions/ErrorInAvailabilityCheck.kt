package com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*

@BrowserAction
@DiagramAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Error In Availability Check", category = "Example.Error")
class ErrorInAvailabilityCheck : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        throw RuntimeException("Availability check failed: testing the error handling mechanism")
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        failMethod()
    }

    private fun failMethod() {
        val block = SysMLFactory.getInstance().createBlock();
        block.name = "SHOULD NOT BE CREATED";
        throw OMFCriticalException("This is a critical exception")
    }


}