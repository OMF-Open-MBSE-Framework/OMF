package com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFWarningException
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import org.apache.commons.collections4.CollectionUtils

@BrowserAction
@DiagramAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "HANDLING ERROR", category = "Example.Error")
class HandlingErrorUIAction : AUIAction(){

    override fun checkAvailability(selectedElements: MutableList<Element>?): Boolean {
        return OMFUtils.getProject() != null && !CollectionUtils.isEmpty(selectedElements)
    }

    override fun actionToPerform(selectedElements: MutableList<Element>?) {
        try {
            failMethod()
        } catch (e: Exception) {
            OMFLogger2.warn(OMFWarningException("this error has been well handled", e))
        }
    }

    private fun failMethod() {
        val block = SysMLFactory.getInstance().createBlock();
        block.name = "SHOULD NOT BE CREATED";
        throw OMFCriticalException2("This is a critical exception")
    }


}