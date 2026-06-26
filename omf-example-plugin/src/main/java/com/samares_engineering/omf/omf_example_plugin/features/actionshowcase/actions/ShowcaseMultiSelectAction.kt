package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction

/**
 * Demonstrates: multi-selection — the action only becomes available when 2 or more elements
 * are selected simultaneously.
 *
 * In MagicDraw, multi-selection works in both the browser (Ctrl+Click or Shift+Click on nodes)
 * and on diagrams (rubber-band selection or Ctrl+Click on shapes). The OMF framework collects
 * all selected elements into the list passed to checkAvailability() and actionToPerform().
 *
 * Note: in the browser, multi-selection across different parent nodes is allowed by MagicDraw
 * but may not make semantic sense for your action — validate explicitly when needed.
 */
@BrowserAction
@DiagramAction
@MDAction(actionName = "Showcase: Multi-Select (2+ elements)", category = "OMFShowcase")
class ShowcaseMultiSelectAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean =
        selectedElements.size >= 2

    override fun actionToPerform(selectedElements: List<Element>) {
        val log = OMFLog()
            .bold("Multi-Select Action")
            .text(" on ${selectedElements.size} elements:")

        selectedElements.forEachIndexed { i, element ->
            log.text("\n  [${i + 1}] ").linkElement(
                element.humanName ?: element.javaClass.simpleName,
                element
            )
        }

        OMFLogger2.toAll().log(log)
    }
}
