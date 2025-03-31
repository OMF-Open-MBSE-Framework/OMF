package com.samares_engineering.omf.omf_example_plugin.features.featureexample.live

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker
import com.samares_engineering.omf.omf_example_plugin.features.featureTemplate.creation.LiveActionExample
import java.beans.PropertyChangeEvent

//Will be evaluated when model change is detected
class DemoLiveAction: ALiveAction() {

    //Will be assessed for each event of the  session
    override fun eventMatches(evt: PropertyChangeEvent?): Boolean {
        return EventChecker()
            .isElementCreated() //event concern an element creation
            .isBlock() // event concern a block
            .test(evt) // evaluate block creation
    }


    //Triggered when the event is detected
    //This method is executed in a session, and inside OMFBarrier
    override fun process(event: PropertyChangeEvent): PropertyChangeEvent {
            val block =  event.source as Class
            block.setName("succeed");
        return event
    }

    //Allows to execute other LiveAction for this session batch
    override fun isBlocking(): Boolean {
        return false
    }

}