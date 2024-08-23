package com.samares_engineering.omf.omf_example_plugin.features.listeners

import com.nomagic.magicdraw.core.Project
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port
import com.nomagic.uml2.impl.PropertyNames
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.KeepListenerActivated
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.AOnProjectOpenedHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.OnProjectClosedHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveActionCharacterizedEvent
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.*
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import java.beans.PropertyChangeEvent

private var isListenerActivated: Boolean = false
class ListenersFeature: SimpleFeature("Listeners Feature") {
    val listener = ElementListener(this)

    override fun getLifeCycleHooks(): MutableList<Hook> {
        return mutableListOf(onProjectHoook(listener))
    }

    class onProjectHoook(val listener: ElementListener): AOnProjectOpenedHook(), OnProjectClosedHook {

        override fun onProjectOpened(project: Project?) {
            listener.register()
        }

        override fun onProjectClosed(project: Project?) {
            listener.unregister()
        }
    }

    override fun initLiveActions(): MutableList<LiveActionEngine<*>> {
        val creation = LiveActionEngineImpl(LiveActionType.CREATE).apply {
            addLiveAction(CreateAnotherPortOnPortCreation())
            addLiveAction(RenamePartCreation())
        }
        val update = LiveActionEngineImpl(LiveActionType.UPDATE).apply {
            addLiveAction(UpdatePortOnInterfaceNameChanges())
            addLiveAction(UpdatePortInterfaceFlowNames())
        }

        val deletion2 = LiveActionEngineImpl(LiveActionType.DELETE).apply {
            addLiveAction(DeleteInterfaceOnPortDeletionCharacterizedEvent())
        }
        val deletion = ALiveActionEngine(LiveActionType.DELETE).apply {
            addLiveAction(DeleteInterfaceOnPortDeletion())
        }

        return mutableListOf(creation, update, deletion, deletion2)

    }

    override fun initFeatureActions(): MutableList<AUIAction> {
        return mutableListOf(ActivateDeactivateListeners())
    }
}


@DiagramAction
@DeactivateListener
@MDAction(actionName = "Activate/deactivate LiveActions", category = "", keyStroke = ["alt shift L"])
class ActivateDeactivateListeners : AUIAction() {
    override fun checkAvailability(selectedElements: MutableList<Element>?): Boolean {
        return true
    }

    override fun actionToPerform(selectedElements: MutableList<Element>?) {
        isListenerActivated = !isListenerActivated
        OMFLogger.infoToUIConsole("Listeners are now ${if (isListenerActivated) "activated" else "deactivated"}")
    }
}




@KeepListenerActivated
class DeleteInterfaceOnPortDeletion : ALiveAction() {
    override fun eventMatches(event: PropertyChangeEvent): Boolean {
    if (!isListenerActivated) return false

      return EventChecker()
            .isInstanceDeleted()
            .isPort()
            .isTrue { (event.source as Port).type != null }
            .test(event)
    }

    override fun process(event: PropertyChangeEvent): PropertyChangeEvent {
        val port = event.source as Port
        ModelElementsManager.getInstance().removeElement(port.type)
        return event
    }

    override fun isBlocking(): Boolean {
        return false
    }
}

@KeepListenerActivated
class DeleteInterfaceOnPortDeletionCharacterizedEvent : ALiveActionCharacterizedEvent() {
    override fun eventMatches(history: CharacterizedEvent): Boolean {
        if (!isListenerActivated) return false
        return history.element is Port && history.relatedEvents.contains("type")

    }

    override fun process(history: CharacterizedEvent): CharacterizedEvent {
        val type = history.oldRelatedElement[PropertyNames.TYPE]!! as Type
        ModelElementsManager.getInstance().removeElement(type)
        return history
    }

    override fun isBlocking(): Boolean {
        return false
    }
}






class CreateAnotherPortOnPortCreation : ALiveActionCharacterizedEvent() {
    override fun eventMatches(history: CharacterizedEvent): Boolean {
        return Profile._getSysml().proxyPort().`is`(history.element)
    }

    override fun process(history: CharacterizedEvent): CharacterizedEvent {
        val port = history.element as Port
        val newPort = SysMLFactory.getInstance().createProxyPort(port.owner)
        newPort.name = port.name + "_copy" + (port.owner?.ownedElement?.size ?: "")
        return history
    }

    override fun isBlocking(): Boolean {
        return false
    }
}

class RenamePartCreation : ALiveActionCharacterizedEvent() {
    override fun eventMatches(history: CharacterizedEvent): Boolean {
        return Profile._getSysmlAdditionalStereotypes().partProperty().`is`(history.element)
    }

    override fun process(history: CharacterizedEvent): CharacterizedEvent {
        val part = history.element as Property
        part.name += "_copy"
        return history
    }

    override fun isBlocking(): Boolean {
        return false
    }
}

class UpdatePortInterfaceFlowNames : ALiveActionCharacterizedEvent() {
    /**
     * Triggered only when a ProxyPort is renamed
     * @param history event occurred in the model
     * @return true if the event matches the rule
     */
    override fun eventMatches(history: CharacterizedEvent): Boolean {
        return history.element is Port && (history.element as Port).type != null && history.relatedEvents.contains("name")
    }


    override fun process(history: CharacterizedEvent): CharacterizedEvent {
        val port = history.element as Port
        val type = port.type
        type!!.name = port.name + "_type"
        return history
    }

    override fun isBlocking(): Boolean {
        return false
    }
}
class UpdatePortOnInterfaceNameChanges : ALiveActionCharacterizedEvent() {

    override fun eventMatches(history: CharacterizedEvent): Boolean {
        return history.element is Type
                && (history.element as Type)._typedElementOfType.isNotEmpty()
                && history.relatedEvents.contains("name")
    }


    override fun process(history: CharacterizedEvent): CharacterizedEvent {
        val type = history.element as Type
        val port = type._typedElementOfType
            .map { it as Port }
            .forEach() { it.name = type.name + "_port" }
        return history
    }

    override fun isBlocking(): Boolean {
        return false
    }
}