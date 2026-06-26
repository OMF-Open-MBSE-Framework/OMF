package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.actions.ActionsCategory
import com.nomagic.actions.ActionsManager
import com.nomagic.actions.NMAction
import com.nomagic.magicdraw.actions.MDActionsCategory
import com.nomagic.magicdraw.core.Application
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import javax.swing.Action
import javax.swing.KeyStroke

/**
 * Logs the full main-menu category/action tree at runtime.
 *
 * IMPORTANT: Application.getActionsManager() returns com.nomagic.magicdraw.actions.ActionsManager
 * which is a WRAPPER (not the base ActionsManager) — it does NOT have getCategories().
 * To get the main menu category tree, call:
 *   Application.getInstance().getActionsManager().getGeneralActionsManager()
 * This returns com.nomagic.actions.ActionsManager which has getCategories().
 *
 * com.nomagic.magicdraw.actions.ActionsManager wrapper methods:
 *   getGeneralActionsManager()  — com.nomagic.actions.ActionsManager with main menu categories
 *   getDiagramActionsManager()  — DiagramsActionsManager for all diagram types
 *   getActionsExecuter()        — ActionsExecuter for triggering layout etc.
 *
 * For shortcut debugging:
 *   ActionsManager.getActionFor(KeyStroke) — returns the NMAction registered for a keystroke,
 *   or null if no action is bound to it. Use this to verify a keyStroke was registered.
 *   Also logs com.nomagic.actions.ActionsManager.getAllActions() size as a sanity check.
 */
@MenuAction
@MDAction(actionName = "Showcase: Explore Main Menu Structure", category = "OMFShowcase")
class ShowcaseExploreGroupsAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean = true

    override fun actionToPerform(selectedElements: List<Element>) {
        // Both getActionsManager() and getGeneralActionsManager() are @Deprecated in MagicDraw 2026x.
        // No public replacement has been documented. Using suppress until a newer API is found.
        @Suppress("DEPRECATION")
        val generalAM: ActionsManager = Application.getInstance().getActionsManager().getGeneralActionsManager()
        val log = OMFLog().bold("Main Menu Categories & Actions").text("\n")

        for (cat in generalAM.getCategories().filterIsInstance<MDActionsCategory>().sortedBy { it.name }) {
            appendCategory(log, cat, indent = "")
        }

        // Shortcut debugging — check if Ctrl+Shift+F11 is registered
        val ks = KeyStroke.getKeyStroke("ctrl shift F11")
        val found = generalAM.getActionFor(ks)
        log.text("\n\nCtrl+Shift+F11 registered in general AM: ").bold(
            if (found != null) "YES — ${found.getValue(Action.NAME)}" else "NO"
        )
        log.text("\nTotal actions in general AM: ").bold(generalAM.getAllActions().size.toString())
        log.text("\n(NMAction.getGroup() is package-private — group values not readable from plugins.)")
        OMFLogger2.toAll().log(log)
    }

    private fun appendCategory(log: OMFLog, cat: ActionsCategory, indent: String) {
        log.text("\n${indent}[").bold(cat.name ?: "<unnamed>").text("]")
        for (action in cat.getActions()) {
            if (action is ActionsCategory) {
                appendCategory(log, action, "$indent  ")
            } else {
                appendAction(log, action, "$indent  ")
            }
        }
    }

    private fun appendAction(log: OMFLog, action: NMAction, indent: String) {
        val name  = action.getValue(Action.NAME)?.toString() ?: "<unnamed>"
        val clazz = action.javaClass.simpleName
        log.text("\n$indent- ").bold(name).text("  [").text(clazz).text("]")
    }
}
