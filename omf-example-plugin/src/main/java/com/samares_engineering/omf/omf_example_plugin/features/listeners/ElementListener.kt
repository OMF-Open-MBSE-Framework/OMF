package com.samares_engineering.omf.omf_example_plugin.features.listeners

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.transaction.TransactionCommitListener
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.listeners.AElementListener
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

class ElementListener(val feature: OMFFeature) : PropertyChangeListener, TransactionCommitListener, AElementListener() {

    override fun propertyChange(evt: PropertyChangeEvent?) {
//        analyseEVT(evt)
    }

    private fun analyseEVT(evt: PropertyChangeEvent?) {
//        val name = evt?.propertyName
//        val oldValue = evt?.oldValue
//        val newValue = evt?.newValue
//        val source = evt?.source
//
//        when (name) {
//            ("Diagram Window Activated") -> return
//            ("Selection Changed") -> return
//            ("Command Executed") -> {
//                if (oldValue == null) OMFLogger.warnToSystemConsole(" Command Executed - user action: $name, $oldValue, $newValue")
//                if (newValue == null) OMFLogger.warnToSystemConsole(" Command Executed - rollback : $name, $oldValue, $newValue")
//            }
//            (UML2MetamodelConstants.INSTANCE_DELETED) -> if(isSynchronizedElement(source)) manageSynchronizedElementDeletion(source as Element)
//
//            else -> {
//                if(isSynchronizedElement(source)) manageSynchronizedElementUpdate(source as Element)
//            }
//        }
    }



    override fun addingListener() {
        if (OMFUtils.isProjectVoid()) return
        OMFUtils.getProject().addPropertyChangeListener(this)
        OMFUtils.getProject().repository.transactionManager.addTransactionCommitListenerIncludingUndoAndRedo(this)
    }

    override fun removingListener() {
        if (OMFUtils.isProjectVoid()) return
        OMFUtils.getProject().removePropertyChangeListener(this)
        OMFUtils.getProject().repository.transactionManager.addTransactionCommitListenerIncludingUndoAndRedo(this)
    }

    override fun transactionCommited(allEventsInSession: MutableCollection<PropertyChangeEvent>): Runnable? {
        val eventAnalyzer = EventAnalyzer().analyzeSessionBatch(allEventsInSession)
                return null
    }


}

class EventAnalyzer{
    val createdEvents = HashMap<Element, MutableList<PropertyChangeEvent>>()
    val updatedEvents = HashMap<Element, MutableList<PropertyChangeEvent>>()
    val deletedEvents = HashMap<Element, MutableList<PropertyChangeEvent>>()

    fun analyzeSessionBatch(allEventsInSession: MutableCollection<PropertyChangeEvent>): EventAnalyzer {
        val uncategorizedEvents = allEventsInSession.toMutableList()

        // Process created/updated events
        processEvents(uncategorizedEvents, createdEvents, UML2MetamodelConstants.INSTANCE_CREATED)
        processEvents(uncategorizedEvents, deletedEvents, UML2MetamodelConstants.INSTANCE_DELETED)

        // Remaining events are considered updated
        uncategorizedEvents.forEach { evt ->
            categorizeRelatedEvents(evt.source as Element, updatedEvents, uncategorizedEvents)
        }
        return this
    }

    private fun processEvents(events: MutableList<PropertyChangeEvent>, categoryMap: HashMap<Element, MutableList<PropertyChangeEvent>>, eventType: String) {
        val eventsToCategorize = events.filter { it.propertyName == eventType }
        val toRemove = mutableListOf<PropertyChangeEvent>()

        for (event in eventsToCategorize) {
            toRemove.addAll(categorizeRelatedEvents(event.source as Element, categoryMap, events))
        }

        events.removeAll(toRemove)
    }

    private fun categorizeRelatedEvents(
        element: Element,
        category: HashMap<Element, MutableList<PropertyChangeEvent>>,
        uncategorizedEvents: MutableList<PropertyChangeEvent>
    ): Collection<PropertyChangeEvent> {
        val relatedChanges = uncategorizedEvents.filter { evt ->
            evt.source == element || evt.oldValue == element || evt.newValue == element
        }

        if (relatedChanges.isNotEmpty()) {
            category.computeIfAbsent(element) { mutableListOf() }.addAll(relatedChanges)
        }
        return relatedChanges
    }

}
