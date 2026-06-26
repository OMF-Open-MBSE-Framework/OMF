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
 * Demonstrates: per-context execution — same action name and category, but different code
 * runs depending on whether the user triggered from the browser, diagram, or menu.
 *
 * Override the execute methods:
 *   executeBrowserAction(selectedElements)  → called when triggered from the containment tree
 *   executeDiagramAction(selectedElements)  → called when triggered from a diagram context menu
 *   executeMenuAction(selectedElements)     → called when triggered from the main menu bar
 *
 * Each override must wrap the body in executeAUIActionWithinBarrier { } to get the session
 * management, rollback support, and listener deactivation provided by the OMF barrier.
 * (The default implementations do this for you by calling actionToPerform(); if you override
 * them you take on this responsibility.)
 *
 * actionToPerform() becomes a dead-code fallback here, kept to satisfy the abstract contract.
 * In practice, you would throw an IllegalStateException or leave it empty.
 */
@BrowserAction
@DiagramAction
@MenuAction
@MDAction(actionName = "Showcase: Per-Context Behavior", category = "OMFShowcase")
class ShowcaseContextBehaviorAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean = isProjectOpened

    override fun executeBrowserAction(selectedElements: List<@JvmSuppressWildcards Element>) {
        executeAUIActionWithinBarrier { runBrowserBehavior(selectedElements) }
    }

    override fun executeDiagramAction(selectedElements: List<@JvmSuppressWildcards Element>) {
        executeAUIActionWithinBarrier { runDiagramBehavior(selectedElements) }
    }

    override fun executeMenuAction(selectedElements: List<@JvmSuppressWildcards Element>) {
        executeAUIActionWithinBarrier { runMenuBehavior(selectedElements) }
    }

    // actionToPerform is abstract in AUIAction; provide it as a fallback even though it is
    // unreachable when all three execute methods are overridden.
    override fun actionToPerform(selectedElements: List<Element>) = runMenuBehavior(selectedElements)

    private fun runBrowserBehavior(elements: List<Element>) {
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("[BROWSER]")
                .text(" Context Behavior: selected ${elements.size} element(s) in the containment tree.")
        )
    }

    private fun runDiagramBehavior(elements: List<Element>) {
        val pe = diagramSelectedPresentationElements.firstOrNull()
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("[DIAGRAM]")
                .text(" Context Behavior: selected ${elements.size} shape(s).")
                .text(" First shape bounds: ${pe?.bounds}")
        )
    }

    private fun runMenuBehavior(elements: List<Element>) {
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("[MENU]")
                .text(" Context Behavior: triggered from main menu with ${elements.size} element(s).")
        )
    }
}
