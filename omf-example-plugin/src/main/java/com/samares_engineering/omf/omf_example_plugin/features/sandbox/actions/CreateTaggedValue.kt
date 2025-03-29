package com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.builders.BetaFactory.magicDrawFactory
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.analysis.TaggedValueContextHelper
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils

@MenuAction
@BrowserAction
@DiagramAction
@MDAction(actionName = "Create TaggedValue", category = "OMF")
class CreateTaggedValue : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return selectedElements.size == 1
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        try {
            val element = selectedElements[0]
            val magicDrawFactory = SysMLFactory.getInstance().magicDrawFactory
            val tmpClassDefinition = magicDrawFactory.createClassInstance()
            tmpClassDefinition.owner = OMFUtils.getProject().primaryModel
            val tagProperty = magicDrawFactory.createPropertyInstance()
            tagProperty.owner = tmpClassDefinition
            val taggedValue =
                magicDrawFactory.createStringTaggedValueInstance()
            taggedValue.owner = element
            taggedValue.value.add("OMF")
            taggedValue.tagDefinition = tagProperty

        } catch (e: Exception) {
            throw OMFCriticalException("Error while creating State Machine", e)
        }
    }
}
