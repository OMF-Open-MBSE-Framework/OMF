package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction

/**
 * Demonstrates: @DeactivateListener — pauses the feature's live-action listeners while the
 * action runs, then re-enables them after execution completes (or fails).
 *
 * Use cases for @DeactivateListener:
 *   - Your action modifies the model and you have listeners that react to model changes.
 *     Without this, your listener fires during your own action, causing double-execution or
 *     infinite loops.
 *   - Batch operations: suppress intermediate listener firings for performance.
 *
 * @KeepListenerActivated (alternative):
 *   - The reverse: keep the listener active. Rarely needed, but useful when you *want* your
 *     listeners to respond to changes made by the action (e.g., to trigger a cascade).
 *   - Without either annotation: behaviour is determined by the `shallDeactivateListenerOnTrigger`
 *     boolean passed to the AUIAction constructor (default is `false`, so listeners stay active
 *     unless @DeactivateListener is present).
 *
 * Implementation detail:
 *   OMFBarrierExecutor.executeInSessionWithinBarrier() reads `isDeactivateListenerOnTrigger`
 *   (set from the annotation at construction time) and calls ListenerManager.deactivate() /
 *   reactivate() around the action body.
 */
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Showcase: Deactivate Listener", category = "OMFShowcase")
class ShowcaseDeactivateListenerAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean = isProjectOpened

    override fun actionToPerform(selectedElements: List<Element>) {
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Deactivate Listener Action")
                .text(" ran with listeners paused. Any live-action listeners on this feature")
                .text(" were suspended for the duration of this method.")
        )
    }
}
