package com.samares_engineering.omf.omf_example_plugin.features.listeners

import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency

import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.KeepListenerActivated
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.events.SessionHistory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveActionSession
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile

@KeepListenerActivated
class TestSessionLiveAction : ALiveActionSession() {

    /**
     * Analyze the session history and check if there is one or more Satisfy links deleted
     * @param history the session history
     * @return true if the event matches the live action
     */
    override fun eventMatches(history: SessionHistory): Boolean {
        // Analyze the session history, characterizing the events
        // by type (created, updated, deleted),
        // and per element - related events
        history.analyzeSession()
        // Get a map of deleted elements - related PropertyChangeEvents
        val deletedEvents = history.eventAnalyzer.deletedEvents
        val satisfy = Profile._getSysml().satisfy()
        deletedEvents.keys
            .filter {it is Dependency} //if a Dependency is deleted
            //invoke the TaggedValueHelper to analyze Stereotype related elements
            .map { history.getTaggedValueHelper(it, history.eventAnalyzer.updatedEvents) }
            .filter { it.concernStereotype(satisfy.stereotype) } //if the Dependency is a Satisfy link
            .toList()

        return false
    }

    override fun process(history: SessionHistory): SessionHistory {

        return history
    }

    override fun isBlocking(): Boolean {
        return false
    }
}
