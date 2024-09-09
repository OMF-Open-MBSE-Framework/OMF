package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine

import com.nomagic.magicdraw.uml.BaseElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import java.beans.PropertyChangeEvent

class CharacterizedEvent(val element: Element,  relatedEvents: List<PropertyChangeEvent>) {
    val relatedEvents: LinkedHashMap<String, PropertyChangeEvent> =
        relatedEvents.map { it.propertyName to it }.toMap(LinkedHashMap())

    val oldRelatedElement: HashMap<String, BaseElement> = relatedEvents
        .filter { it.oldValue is BaseElement }
        .associateTo(HashMap()) { it.propertyName to it.oldValue as BaseElement }

    val newRelatedElement: HashMap<String, BaseElement> = relatedEvents
        .filter { it.newValue is BaseElement }
        .associateTo(HashMap()) { it.propertyName to it.newValue as BaseElement }

    val sourceRelatedElement: HashMap<String, BaseElement> = relatedEvents
        .filter { it.source is BaseElement }
        .associateTo(HashMap()) { it.propertyName to it.source as BaseElement }
}