/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.listeners.listeners

import com.nomagic.magicdraw.copypaste.CopyPasteManager
import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.transaction.TransactionCommitListener
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.RollbackException
import com.samares_engineering.omf.omf_core_framework.errors.cancelsession.UndoManager
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.events.CharacterizedEvent
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.events.SessionHistory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType
import com.samares_engineering.omf.omf_core_framework.listeners.AElementListener
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.analysis.EventAnalyzer
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import java.beans.PropertyChangeEvent
import java.util.function.Consumer

class TransactionElementListener : AElementListener(), TransactionCommitListener {
    private var stopHandlingThisBatch = false
    private var allTriggeredEventsInThisBatch: List<PropertyChangeEvent>? = null
    var shallListenToUndoEvent = false

    override fun transactionCommited(allTriggeredEventsInThisBatch: Collection<PropertyChangeEvent>): Runnable? {
        this.allTriggeredEventsInThisBatch = allTriggeredEventsInThisBatch.toList()
        return Runnable { this.runnable() }
    }

    private fun runnable() {
        try {
            if (!isActivated || CopyPasteManager.isPasting()) return

            allTriggeredEventsInThisBatch!!.forEach(Consumer { event: PropertyChangeEvent? -> this.manageAnalysis(event) })

            if(handleSessionHistoryLiveEngine()) return

            //If the characterized live engine has been executed, we stop the process.
            // Whatever the old liveAction are matching, we don't want to execute them.
            // New liveActions and Old one are not compatible when both are matching.
            if(handleCharacterizedLiveEngine()) return


            //new solution: eventAnalyzer.createdEvents.keys.forEach {manageCreation(it) }
//            eventAnalyzer.updatedEvents.forEach { manageUpdate(it) }
            if (handlePropertyChangeEventLiveEngine()) return
        } catch (e: RollbackException) {
            UndoManager.getInstance().requestHardUndo()
        }
    }

    private fun handleSessionHistoryLiveEngine(): Boolean {
        val sessionHistory = SessionHistory(allTriggeredEventsInThisBatch!!)
        if(manageHistory(sessionHistory)) return true
        return false
    }

    private fun handleCharacterizedLiveEngine(): Boolean {
        val eventAnalyzer = EventAnalyzer().analyzeSessionBatch(allTriggeredEventsInThisBatch!!)
        val creationTriggered = eventAnalyzer.createdEvents
            .map { (element, relatedEvents) -> CharacterizedEvent(element, relatedEvents) }
            .map { elementCreated -> manageHistory(elementCreated) }
            .toList()
            .contains(true)

        val updateTriggered =  eventAnalyzer.updatedEvents
            .map { (element, relatedEvents) -> CharacterizedEvent(element, relatedEvents) }
            .map() { elementUpdated -> manageUpdate(elementUpdated) }
            .toList()
            .contains(true)

        val deletionTriggered = eventAnalyzer.deletedEvents
            .map { (element, relatedEvents) -> CharacterizedEvent(element, relatedEvents) }
            .map() { elementDeleted -> manageDeletion(elementDeleted) }
            .toList()
            .contains(true)
        return creationTriggered || updateTriggered || deletionTriggered
    }


    private fun handlePropertyChangeEventLiveEngine(): Boolean {
        for (evt: PropertyChangeEvent in allTriggeredEventsInThisBatch!!) {
            if (isInstanceCreated(evt)) {
                stopHandlingThisBatch = manageCreation(evt);
            } else {
                stopHandlingThisBatch = manageUpdate(evt);
            }
            if (stopHandlingThisBatch) return true;
        }
        return false
    }

    private fun getInstanceCreatedEvent(elementCreationRelatedEvents: Map.Entry<Element, MutableList<PropertyChangeEvent>>): PropertyChangeEvent? {
        return elementCreationRelatedEvents.value.find { isInstanceCreated(it) }
    }



    private fun isInstanceCreated(evt: PropertyChangeEvent): Boolean {
        return UML2MetamodelConstants.INSTANCE_CREATED == evt.propertyName
    }



    override fun allTransactionsCommitted() {
    }

    override fun addingListener() {
        if (shallListenToUndoEvent)
            OMFUtils.getProject().repository.transactionManager.addTransactionCommitListenerIncludingUndoAndRedo(this)
        else
            OMFUtils.getProject().repository.transactionManager.addTransactionCommitListener(this)
    }

    override fun removingListener() {
        OMFUtils.getProject().repository.transactionManager.removeTransactionCommitListener(this)
    }

    fun listenToUndoEvent() {
        this.shallListenToUndoEvent = true
    }

    /********************** ENGINE *********************/
    private fun manageHistory(history: CharacterizedEvent): Boolean {
        val liveActionEngines = liveActionEngineMap[LiveActionType.CREATE.toString()]?: return false
        return processAllMatchingLiveActions(liveActionEngines, history)
    }
    private fun manageHistory(history: SessionHistory): Boolean {
        val liveActionEngines = liveActionEngineMap[LiveActionType.HISTORY.toString()]?: return false
        return processAllMatchingLiveActions(liveActionEngines, history)
    }

    private fun manageUpdate(history: CharacterizedEvent): Boolean{
        val liveActionEngines = liveActionEngineMap[LiveActionType.UPDATE.toString()]?: return false
        return processAllMatchingLiveActions(liveActionEngines, history)
    }

    private fun manageDeletion(history: CharacterizedEvent): Boolean{
        val liveActionEngines = liveActionEngineMap[LiveActionType.DELETE.toString()] ?: return false
        return processAllMatchingLiveActions(liveActionEngines, history)
    }


    private fun processAllMatchingLiveActions(
        liveActionEngines: List<LiveActionEngine<*>>?,
        event: PropertyChangeEvent
    ): Boolean {
        if (liveActionEngines == null) return false


        var hasBlockingLiveAction = liveActionEngines
            .filter { liveActionEngine -> liveActionEngine.checkLiveActionEngineType(PropertyChangeEvent::class.java) }
            .map { liveActionEngine -> liveActionEngine as LiveActionEngine<PropertyChangeEvent> }
            .map { liveActionEngine -> liveActionEngine.processAllMatchingLiveActions(event)}
            .toList()
            .contains(true)

        return hasBlockingLiveAction
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
    private fun processAllMatchingLiveActions(
        liveActionEngines: List<LiveActionEngine<*>>?,
        history: SessionHistory
        ): Boolean {
            if (liveActionEngines == null) return false

            var hasLiveActionsBeenTriggered = liveActionEngines
                .filter { liveActionEngine -> liveActionEngine.checkLiveActionEngineType(SessionHistory::class.java) }
                .map { liveActionEngine -> liveActionEngine as LiveActionEngine<SessionHistory> }
                .map { liveActionEngine -> liveActionEngine.processAllMatchingLiveActions(history)}
                .toList()
                .contains(true)


            return hasLiveActionsBeenTriggered
    }






//    fun manageCreation(createdElement: Element): Boolean {
//        val liveActionEngines = liveActionEngineMap[LiveActionType.CREATE.toString()]!!
//        return processAllMatchingLiveActions(liveActionEngines, createdElement)
//    }

//    fun manageDeletion(elementDeletionRelatedEvent: Map<Element,List<PropertyChangeEvent>>): Boolean {
//        val liveActionEngines = liveActionEngineMap[LiveActionType.DELETE.toString()]!!
//
//        return processAllMatchingLiveActions(liveActionEngines, elementDeletionRelatedEvent)
//    }
}
