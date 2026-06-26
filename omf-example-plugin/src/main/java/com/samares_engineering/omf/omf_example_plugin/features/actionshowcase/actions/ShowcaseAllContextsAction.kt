package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction

/**
 * Demonstrates: all three context annotations with a single unified behavior.
 *
 * Appears in:
 *   - Main menu bar         → @MenuAction
 *   - Browser right-click   → @BrowserAction
 *   - Diagram right-click   → @DiagramAction
 *
 * When all three annotations are present and actionToPerform() is not overridden per-context,
 * the same code runs regardless of where the user triggered it.
 *
 * The category string "OMFShowcase" creates a top-level submenu in the main menu bar
 * and a "OMFShowcase" group in browser/diagram context menus.
 */
@MenuAction
@BrowserAction
@DiagramAction
@MDAction(actionName = "Showcase: All Contexts", category = "OMFShowcase")
class ShowcaseAllContextsAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean = isProjectOpened

    override fun actionToPerform(selectedElements: List<Element>) {
        val log = OMFLog()
            .bold("All-Contexts Action")
            .text(" triggered with ")
            .bold(selectedElements.size.toString())
            .text(" element(s) selected.")

        selectedElements.forEach { element ->
            log.text("\n  - ").linkElement(element.humanName ?: element.javaClass.simpleName, element)
        }

        OMFLogger2.toAll().log(log)
    }
}
