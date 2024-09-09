package com.samares_engineering.omf.omf_core_framework.listeners.listeners.analysis

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype
import com.nomagic.uml2.impl.PropertyNames
import java.beans.PropertyChangeEvent

class TaggedValueContextHelper(
    val element: Element,
    mapElement_events: Map<Element, List<PropertyChangeEvent>>,
) {
    val relatedTaggedValuesContext: Map<TaggedValue, List<PropertyChangeEvent>> = mapElement_events.keys
        .filter { TaggedValue::class.isInstance(it) }
        .filter { isConcerningElement(mapElement_events[it] ?: emptyList()) }
        .associate { it as TaggedValue to mapElement_events[it]!! }

    fun isConcerningElement(events: List<PropertyChangeEvent>): Boolean{
        return events.filter { it.propertyName == PropertyNames.TAGGED_VALUE_OWNER }
            .map { it.oldValue as Element }
            .any{it == element}
    }

    fun getTaggedValueByProperty(property: Property): TaggedValue?{
        return relatedTaggedValuesContext.keys
            .filter { relatedTaggedValuesContext[it]?.any { it.propertyName == PropertyNames.TAG_DEFINITION && it.oldValue == property } ?: false }
            .map { it }
            .firstOrNull()
    }

    fun concernProperty(property: Property): Boolean{
        return relatedTaggedValuesContext.keys
            .filter { relatedTaggedValuesContext[it]?.any { it.propertyName == PropertyNames.TAG_DEFINITION && it.oldValue == property } ?: false }
            .any()
    }

    fun getValue(taggedValue: TaggedValue): Any?{
        return relatedTaggedValuesContext[taggedValue]
            ?.filter { it.propertyName == PropertyNames.VALUE }
            ?.map { it.oldValue }
            ?.firstOrNull()
    }

    fun getValue(property: Property): Any?{
        return getTaggedValueByProperty(property)?.let { getValue(it) }
    }

    fun getStereotype(taggedValue: TaggedValue): Stereotype?{
        return relatedTaggedValuesContext[taggedValue]
            ?.filter { it.propertyName == PropertyNames.TAG_DEFINITION }
            ?.map { (it.oldValue as Property).owner }
            ?.firstOrNull() as? Stereotype
    }

    fun concernStereotype(stereotype: Stereotype): Boolean{
        return relatedTaggedValuesContext.keys
            .filter { relatedTaggedValuesContext[it]?.any { it.propertyName == PropertyNames.TAG_DEFINITION && (it.oldValue as Property).owner == stereotype } ?: false }
            .any()
    }

    fun getElement(taggedValue: TaggedValue): Element?{
        return relatedTaggedValuesContext[taggedValue]
            ?.filter { it.propertyName == PropertyNames.TAGGED_VALUE_OWNER }
            ?.map {it.source }
            ?.firstOrNull() as? Element
    }

}
