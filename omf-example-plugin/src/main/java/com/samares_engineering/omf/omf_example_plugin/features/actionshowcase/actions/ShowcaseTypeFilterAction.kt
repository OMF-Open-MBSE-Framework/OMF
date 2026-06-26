package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package as MDPackage
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction

/**
 * Demonstrates: availability gated on selection type — only visible when exactly one Package is selected.
 *
 * checkAvailability() is the unified guard for all contexts. The OMF framework calls it from
 * checkBrowserAvailability() and checkDiagramAvailability() automatically. This means:
 *   - In the browser:  only appears when one Package node is selected
 *   - On a diagram:    only appears when one Package shape is selected
 *
 * Pattern notes:
 *   - Use `is MDPackage` (Kotlin smart-cast) rather than instanceof + cast separately.
 *   - Kotlin imports `Package` as `MDPackage` to avoid clash with kotlin.reflect.KPackage.
 *   - Similarly, import `com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class as MDClass`
 *     when filtering for UML Class elements.
 */
@BrowserAction
@DiagramAction
@MDAction(actionName = "Showcase: Package Filter", category = "OMFShowcase")
class ShowcaseTypeFilterAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean =
        selectedElements.size == 1 && selectedElements[0] is MDPackage

    override fun actionToPerform(selectedElements: List<Element>) {
        val pkg = selectedElements[0] as MDPackage
        val childCount = pkg.ownedElement.size

        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Package Filter Action")
                .text(" on package ")
                .linkElement(pkg.name ?: "unnamed", pkg)
                .text(" which owns $childCount element(s).")
        )
    }
}
