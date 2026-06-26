package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class as MDClass
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package as MDPackage
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction

/**
 * Demonstrates: different availability conditions per context.
 *
 * Override checkBrowserAvailability(), checkDiagramAvailability(), and checkMenuAvailability()
 * independently. Each override replaces the default which would have called checkAvailability().
 *
 * This action appears in:
 *   - Browser context menu  → only when a Package is selected
 *   - Diagram context menu  → only when a Class is selected
 *   - Main menu bar         → always (whenever a project is open), regardless of selection
 *
 * Important: always call checkWithinOMFBarrier { } in overrides (the base class does it
 * automatically via the default, but when you override you take responsibility). Here we call
 * it via the base class checkBrowserAvailability() default pattern by calling super, then
 * apply our own check on top. Alternatively, call checkWithinOMFBarrier() directly.
 *
 * Since checkAvailability() is unreachable when all three are overridden, we return true as
 * a neutral placeholder.
 */
@BrowserAction
@DiagramAction
@MenuAction
@MDAction(actionName = "Showcase: Per-Context Availability", category = "OMFShowcase")
class ShowcasePerContextAvailabilityAction : ElementUIAction() {

    override fun checkBrowserAvailability(): Boolean =
        checkWithinOMFBarrier {
            isActivated() && getSelectedBrowserElements().size == 1
                    && getSelectedBrowserElements()[0] is MDPackage
        }

    override fun checkDiagramAvailability(): Boolean =
        checkWithinOMFBarrier {
            isActivated() && getSelectedDiagramElements().size == 1
                    && getSelectedDiagramElements()[0] is MDClass
        }

    override fun checkMenuAvailability(): Boolean =
        checkWithinOMFBarrier { isActivated() && isProjectOpened }

    // Never called directly because all three context checks are overridden above.
    override fun checkAvailability(selectedElements: List<Element>): Boolean = true

    override fun actionToPerform(selectedElements: List<Element>) {
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Per-Context Availability")
                .text(" triggered. Selected: ${selectedElements.size} element(s).")
        )
    }
}
