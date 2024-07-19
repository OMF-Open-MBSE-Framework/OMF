package com.samares_engineering.omf.omf_example_plugin.features.ergodiagram.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils

@DiagramAction
@DeactivateListener
@MDAction(actionName = "Register Shortcut", category = "")
class registerShortcut : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        if (OMFUtils.getProject() == null) return false
        return true
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        IBDShortcutConfigurator().register()
    }
}
