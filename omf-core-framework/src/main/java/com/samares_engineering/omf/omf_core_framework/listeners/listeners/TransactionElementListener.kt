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
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType
import com.samares_engineering.omf.omf_core_framework.listeners.AElementListener
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import java.beans.PropertyChangeEvent
import java.util.function.Consumer
import java.util.stream.Collectors

class TransactionElementListener : AElementListener(), TransactionCommitListener {
    private val stopHandlingThisBatch = false
    private var allTriggeredEventsInThisBatch: List<PropertyChangeEvent>? = null

    override fun transactionCommited(allTriggeredEventsInThisBatch: Collection<PropertyChangeEvent>): Runnable? {
        this.allTriggeredEventsInThisBatch = allTriggeredEventsInThisBatch.toList()
        return Runnable { this.runnable() }
    }

    private fun runnable() {
        try {
            if (!isActivated || CopyPasteManager.isPasting()) return

            //            stopHandlingThisBatch = false;
//
            allTriggeredEventsInThisBatch!!.forEach(Consumer { event: PropertyChangeEvent? -> this.manageAnalysis(event) })

            //
//            for (PropertyChangeEvent evt : allTriggeredEventsInThisBatch) {
//                if (isInstanceCreated(evt)) {
//                    stopHandlingThisBatch = manageCreation(evt);
//                } else {
//                    stopHandlingThisBatch = manageUpdate(evt);
//                }
//                if (stopHandlingThisBatch) return;
//            }
            val eventAnalyzer = EventAnalyzer().analyzeSessionBatch(allTriggeredEventsInThisBatch!!)
            eventAnalyzer.createdEvents.map { getInstanceCreatedEvent(it)}.forEach { evt -> manageCreation(evt!!) }
            //new solution: eventAnalyzer.createdEvents.keys.forEach {manageCreation(it) }
//            eventAnalyzer.updatedEvents.forEach { manageUpdate(it) }
        } catch (e: RollbackException) {
            UndoManager.getInstance().requestHardUndo()
        }
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
        OMFUtils.getProject().repository.transactionManager.addTransactionCommitListener(this)
    }

    override fun removingListener() {
        OMFUtils.getProject().repository.transactionManager.removeTransactionCommitListener(this)
    }


    /********************** ENGINE *********************/
    override fun manageCreation(event: PropertyChangeEvent): Boolean {
        val liveActionEngines = liveActionEngineMap[LiveActionType.CREATE.toString()]!!
        return processAllMatchingLiveActions(liveActionEngines, event)
    }

    private fun processAllMatchingLiveActions(
        liveActionEngines: List<LiveActionEngine>?,
        event: PropertyChangeEvent
    ): Boolean {
        if (liveActionEngines == null) return false
        val hasLiveActionsBeenTriggered = liveActionEngines
            .map { liveActionEngine: LiveActionEngine -> liveActionEngine.processAllMatchingLiveActions(event)}
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
