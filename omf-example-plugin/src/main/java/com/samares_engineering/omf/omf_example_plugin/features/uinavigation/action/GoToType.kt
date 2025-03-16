package com.samares_engineering.omf.omf_example_plugin.features.uinavigation.action

import com.nomagic.magicdraw.core.Application
import com.nomagic.magicdraw.ui.browser.Node
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TypedElement
import com.nomagic.utils.Utilities
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import java.util.concurrent.atomic.AtomicReference
import javax.swing.tree.TreePath

@DiagramAction
@BrowserAction
@MDAction(actionName = "Go to Type", category = "")
class GoToType:ElementUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return isProjectOpened && selectedElements.isNotEmpty() && selectedElements.all { it is TypedElement }
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        selectedElements
            .map { it as TypedElement }
            .map { it.type }
            .forEach { OMFUtils.selectElementInContainmentTree(it) }
    }
}