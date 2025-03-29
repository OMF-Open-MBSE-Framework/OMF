package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.events

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.analysis.EventAnalyzer
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.analysis.TaggedValueContextHelper
import java.beans.PropertyChangeEvent

class SessionHistory(val sessionHistory:List<PropertyChangeEvent>)  {
    val eventAnalyzer = EventAnalyzer()

    fun analyzeSession(): SessionHistory {
        eventAnalyzer.analyzeSessionBatch(sessionHistory)
        return this
    }

    fun getTaggedValueHelper(element: Element, sessionContext: Map<Element, List<PropertyChangeEvent>>,): TaggedValueContextHelper {
        return TaggedValueContextHelper(element, sessionContext)
    }

}