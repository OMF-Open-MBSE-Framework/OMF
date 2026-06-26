package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction

/**
 * Demonstrates: @MenuAction only, always enabled even without an open project.
 *
 * Key pattern: override checkMenuAvailability() directly to bypass the default which would try
 * to collect selected elements (impossible without an open project). For global tool actions
 * (e.g., "Open wizard", "About this plugin") that do not need selection or a project, this is
 * the correct approach.
 *
 * Note: checkAvailability() is the unified fallback used by all three context checks when not
 * overridden. Here it returns true because checkMenuAvailability() is fully overridden and
 * checkAvailability() is unreachable for the menu context.
 */
@MenuAction
@MDAction(actionName = "Showcase: Menu Only (Always Available)", category = "OMFShowcase")
class ShowcaseMenuOnlyAction : ElementUIAction() {

    // Override directly so the action is enabled even when no project is open.
    // The default checkMenuAvailability() calls getSelectedBrowserElements() which requires a project.
    override fun checkMenuAvailability(): Boolean = isActivated()

    // checkAvailability() is required by the interface but is not called for this action
    // because checkMenuAvailability() is fully overridden above.
    override fun checkAvailability(selectedElements: List<Element>): Boolean = true

    override fun actionToPerform(selectedElements: List<Element>) {
        val projectStatus = if (isProjectOpened) "Project open: ${project.name}" else "No project open"
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Menu-Only Action")
                .text(" triggered. $projectStatus")
        )
    }
}
