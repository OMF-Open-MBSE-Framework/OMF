package com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*
import org.apache.commons.collections4.CollectionUtils

@BrowserAction
@DiagramAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "HANDLING ERROR", category = "Example.Error")
class HandlingErrorUIAction : ElementUIAction(){

    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return isProjectOpened && !CollectionUtils.isEmpty(selectedElements)
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        failMethod()
    }

    private fun failMethod() {
        val block = SysMLFactory.getInstance().createBlock()
        block.name = "SHOULD NOT BE CREATED"
        throw OMFCriticalException("This is a critical exception")
    }


}