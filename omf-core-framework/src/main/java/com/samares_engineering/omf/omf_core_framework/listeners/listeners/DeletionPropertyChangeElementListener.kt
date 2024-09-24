/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.listeners.listeners

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException
import com.samares_engineering.omf.omf_core_framework.errors.cancelsession.UndoManager
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.events.CharacterizedEvent
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType
import com.samares_engineering.omf.omf_core_framework.listeners.AElementListener
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class DeletionPropertyChangeElementListener : AElementListener(), PropertyChangeListener {
    override fun propertyChange(evt: PropertyChangeEvent) {
        if (!isActivated) return

        val isInstanceDeleted = (UML2MetamodelConstants.BEFORE_DELETE == evt.propertyName)
        if (isInstanceDeleted) {
            try {
                manageDeletion(CharacterizedEvent(evt.source as Element, listOf(evt)))
                manageDeletion(evt)
            } catch (e: RollbackException) {
                UndoManager.getInstance().requestHardUndo()
            }
        }
    }

    private fun manageDeletion(history: CharacterizedEvent): Boolean{
        val liveActionEngines = liveActionEngineMap[LiveActionType.DELETE.toString()]
        return processAllMatchingLiveActions(liveActionEngines, history)
    }

    private fun processAllMatchingLiveActions(
        liveActionEngines: List<LiveActionEngine<*>>?,
        event: CharacterizedEvent
    ): Boolean {
        if (liveActionEngines == null) return false

        var hasLiveActionsBeenTriggered = liveActionEngines
            .filter { liveActionEngine -> liveActionEngine.checkLiveActionEngineType(CharacterizedEvent::class.java) }
            .map { liveActionEngine -> liveActionEngine as LiveActionEngine<CharacterizedEvent> }
            .map { liveActionEngine -> liveActionEngine.processAllMatchingLiveActions(event)}
            .toList()
            .contains(true)


        return hasLiveActionsBeenTriggered
    }




    override fun addingListener() {
        OMFUtils.getProject().repositoryListenerRegistry.addPropertyChangeListener(
            this,
            UML2MetamodelConstants.BEFORE_DELETE
        )
    }

    override fun removingListener() {
        OMFUtils.getProject().repositoryListenerRegistry.removePropertyChangeListener(
            this,
            UML2MetamodelConstants.BEFORE_DELETE
        )
    }
}
