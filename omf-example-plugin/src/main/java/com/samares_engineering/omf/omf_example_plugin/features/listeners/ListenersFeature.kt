package com.samares_engineering.omf.omf_example_plugin.features.listeners

import com.nomagic.magicdraw.core.Project
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.AOnProjectOpenedHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.OnProjectClosedHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType
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

    override fun initLiveActions(): MutableList<LiveActionEngine> {
        val creation = ALiveActionEngine(LiveActionType.CREATE).apply {
            addLiveAction(CreateAnotherPortOnPortCreation())
            addLiveAction(RenamePartCreation())
        }
        val update = ALiveActionEngine(LiveActionType.UPDATE).apply {
            addLiveAction(UpdatePortInterfaceFlowNames())
        }

        val deletion = ALiveActionEngine(LiveActionType.DELETE).apply {
            addLiveAction(DeleteInterfaceOnPortDeletion())
        }

        return mutableListOf(creation, update, deletion)

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





class DeleteInterfaceOnPortDeletion : ALiveAction() {
    override fun eventMatches(event: PropertyChangeEvent): Boolean {
    if (isListenerActivated) return false

      return EventChecker()
            .isInstanceDeleted()
            .isPort()
            .isTrue { (event.source as Port).type != null }
            .test(event)
    }

    override fun process(event: PropertyChangeEvent): PropertyChangeEvent {
        ModelElementsManager.getInstance().removeElement((event.source as Port).type)
        return event
    }

    override fun isBlocking(): Boolean {
        return false
    }
}




class CreateAnotherPortOnPortCreation : ALiveAction() {
    override fun eventMatches(event: PropertyChangeEvent): Boolean {
        return EventChecker()
            .isInstanceCreated()
            .isPort()
            .hasStereotype(Profile._getSysml().proxyPort().stereotype)
            .test(event)
    }

    override fun process(event: PropertyChangeEvent): PropertyChangeEvent {
        val port = event.source as Port
        val newPort = SysMLFactory.getInstance().createProxyPort(port.owner)
        newPort.name = port.name + "_copy" + (port.owner?.ownedElement?.size ?: "")
        return event
    }

    override fun isBlocking(): Boolean {
        return false
    }
}

class RenamePartCreation : ALiveAction() {
    override fun eventMatches(event: PropertyChangeEvent): Boolean {
        return EventChecker()
            .isInstanceCreated()
            .isPart()
            .test(event)
    }

    override fun process(event: PropertyChangeEvent): PropertyChangeEvent {
        val part = event.source as Property
        part.name += "_copy"
        return event
    }

    override fun isBlocking(): Boolean {
        return false
    }
}

class UpdatePortInterfaceFlowNames : ALiveAction() {
    /**
     * Triggered only when a ProxyPort is renamed
     * @param evt event occurred in the model
     * @return true if the event matches the rule
     */
    override fun eventMatches(evt: PropertyChangeEvent): Boolean {
        return EventChecker()
            .isElementRenamed()
            .isPort()
            .hasStereotype(Profile._getSysml().proxyPort().stereotype)
            .isTrue { (evt.source as Port).type != null }
            .test(evt)
    }


    override fun process(evt: PropertyChangeEvent): PropertyChangeEvent {
        val port = evt.source as Port
        val interfaceBlock = port.type ?: return evt

        interfaceBlock.name = port.name
        interfaceBlock.ownedElement.stream()
            .filter { element: Element? -> Profile._getSysml().flowProperty().`is`(element) }
            .map { obj: Element? -> Property::class.java.cast(obj) }
            .forEach { flowProperty: Property -> flowProperty.name = port.name }

        return evt
    }

    override fun isBlocking(): Boolean {
        return false
    }
}