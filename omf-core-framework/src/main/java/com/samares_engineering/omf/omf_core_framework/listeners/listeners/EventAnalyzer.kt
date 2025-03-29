package com.samares_engineering.omf.omf_core_framework.listeners.listeners

import com.nomagic.uml2.ext.jmi.UML2MetamodelConstants
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import java.beans.PropertyChangeEvent
import java.util.HashMap

class EventAnalyzer {
    val createdEvents = HashMap<Element, MutableList<PropertyChangeEvent>>()
    val updatedEvents = HashMap<Element, MutableList<PropertyChangeEvent>>()
    val deletedEvents = HashMap<Element, MutableList<PropertyChangeEvent>>()

    fun analyzeSessionBatch(allEventsInSession: List<PropertyChangeEvent>): EventAnalyzer {
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

    private fun processEvents(
        events: MutableList<PropertyChangeEvent>,
        categoryMap: HashMap<Element, MutableList<PropertyChangeEvent>>,
        eventType: String
    ) {
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