/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.testlockfeature

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import java.beans.PropertyChangeEvent
import java.util.function.Predicate


class TestLockFeature : SimpleFeature("SysML Basic") {

    public override fun initFeatureActions(): List<UIAction> {
        return listOf<UIAction>(
            //             new ResetListeners()
            ChangeNameAction(),
        )
    }

    public override fun initLiveActions(): List<LiveActionEngine<*>> {
        val modificationRE = ALiveActionEngine(LiveActionType.UPDATE)
        modificationRE.addLiveAction(ChangePortTypeName())
        return listOf(modificationRE)
    }

}


class ChangePortTypeName : ALiveAction() {

    override fun eventMatches(evt: PropertyChangeEvent): Boolean {
        val nameContains0: Predicate<PropertyChangeEvent> =
            Predicate { it.newValue is String && (it.newValue as String).contains("0") }
        return EventChecker()
            .isElementCreated()
            .isPort()
            .hasStereotype(Profile._getSysml().proxyPort().stereotype)
            .isTrue(nameContains0)
            .test(evt)
    }

    /**
     * Will create an InterfaceBlock and a FlowProperty with the same name as the port
     * @param evt event occurred in the model
     * @return the event
     */
    override fun process(evt: PropertyChangeEvent): PropertyChangeEvent {
        val port = evt.source as Port
        port.type!!.name += "1"

        return evt
    }

    override fun isBlocking(): Boolean {
        return false
    }
}


@MenuAction
@DiagramAction
@BrowserAction
@MDAction(actionName = "Propagate Port Name to Interface/Flow", category = "LOCK TEST")
class ChangeNameAction : AUIAction() {
    /**
     * Available only When a Typed Port is selected
     * @param selectedElements selected elements from the browser, or the diagram
     * @return true if the action is available
     */
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        val isPortSelected = selectedElements.size == 1 && selectedElements[0] is Port
        val isPortWithInterface = isPortSelected && (selectedElements[0] as Port).type != null
        return isPortWithInterface
    }


    /**
     * Will rename the Interface and the FlowProperty with the port name
     * @param selectedElements selected elements
     */
    override fun actionToPerform(selectedElements: List<Element>) {
        val port = selectedElements[0] as Port
        port.name += "1"
        port.type!!.name = port.name
        port.type!!.ownedElement.stream()
            .filter { Profile._getSysml().flowProperty().`is`(it) }
            .map { it as Property }
            .forEach { it.name = port.name }
    }
}