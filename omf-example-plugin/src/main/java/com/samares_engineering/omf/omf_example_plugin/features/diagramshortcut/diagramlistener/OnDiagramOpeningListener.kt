/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.diagramshortcut.diagramlistener

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException
import com.samares_engineering.omf.omf_core_framework.errors.cancelsession.UndoManager
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine
import com.samares_engineering.omf.omf_core_framework.listeners.AListener
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.DiagramListenerConstants
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import java.util.stream.Collectors

class OnDiagramOpeningListener : AListener(), PropertyChangeListener {
    override fun propertyChange(evt: PropertyChangeEvent) {
        if (!isActivated) return


        val isDiagramOpened = (DiagramListenerConstants.DIAGRAM_OPENED == evt.propertyName)
        if (isDiagramOpened) {
            try {
                manageOpening(evt)
            } catch (e: RollbackException) {
                UndoManager.getInstance().requestHardUndo()
            }
        }
    }


    private fun manageOpening(evt: PropertyChangeEvent): Boolean {
        val liveActionEngines = liveActionEngineMap[DiagramListenerConstants.DIAGRAM_OPENED] as List<LiveActionEngine<PropertyChangeEvent>>?
        return processAllMatchingLiveActions(liveActionEngines, evt)
    }


    /**
     * @return true if at least one liveAction matched
     */
    private fun processAllMatchingLiveActions(
        liveActionEngines: List<LiveActionEngine<PropertyChangeEvent>>?,
        event: PropertyChangeEvent
    ): Boolean {
        if (liveActionEngines == null) return false
        val hasLiveActionsBeenTriggered = liveActionEngines.stream()
            .map { liveActionEngine: LiveActionEngine<PropertyChangeEvent> -> liveActionEngine.processAllMatchingLiveActions(event) }
            .collect(Collectors.toList())
            .contains(true)
        return hasLiveActionsBeenTriggered
    }


    override fun addingListener() {
        //Handled by the DiagramListenerAdapter
    }

    override fun removingListener() {
        //Handled by the DiagramListenerAdapter
    }
}
