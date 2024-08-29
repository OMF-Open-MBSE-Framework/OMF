package com.samares_engineering.omf.omf_example_plugin.features.listeners

import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency

import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.KeepListenerActivated
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.events.SessionHistory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveActionSession
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile

@KeepListenerActivated
class TestSessionLiveAction : ALiveActionSession() {

    override fun eventMatches(history: SessionHistory): Boolean {

        history.analyzeSession()
        val deletedEvents = history.eventAnalyzer.deletedEvents
        val satisfy = Profile._getSysml().satisfy()
        deletedEvents.keys
            .filter {it is Dependency}
            .map { history.getTaggedValueHelper(it, history.eventAnalyzer.updatedEvents) }
            .filter { it.concernStereotype(satisfy.stereotype) }
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
