package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.stateactions.StateAction

/**
 * Demonstrates: stateful checkbox action — appears with a checkmark in context menus and menus.
 *
 * Extend StateAction instead of ElementUIAction. The additional contract:
 *   - checkState(selectedElements): Boolean  → current visual check state (true = checked)
 *   - actionToPerform(selectedElements)      → called when the user clicks; toggle the state here
 *
 * Under the hood:
 *   StateAction replaces the three NMAction objects with MDStateAction subclasses
 *   (StateBrowserAction, StateDiagramAction, StateMenuAction). MDStateAction.updateState()
 *   calls getState() which is wired to checkState().
 *
 * Scope note:
 *   The `isChecked` field here is instance-level. Because actions are singletons registered
 *   once per plugin session, the state persists for the lifetime of the MagicDraw session.
 *   If you need per-project state, store it in a project option or a tagged value instead.
 */
@BrowserAction
@DiagramAction
@MenuAction
@MDAction(actionName = "Showcase: Stateful Checkbox", category = "OMFShowcase")
class ShowcaseStatefulCheckboxAction : StateAction() {

    private var isChecked = false

    override fun checkAvailability(selectedElements: List<Element>): Boolean = isProjectOpened

    override fun checkState(selectedElement: List<Element>): Boolean = isChecked

    override fun actionToPerform(selectedElements: List<Element>) {
        isChecked = !isChecked
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Checkbox Action")
                .text(" toggled → now ")
                .bold(if (isChecked) "CHECKED ✓" else "UNCHECKED")
        )
    }
}
