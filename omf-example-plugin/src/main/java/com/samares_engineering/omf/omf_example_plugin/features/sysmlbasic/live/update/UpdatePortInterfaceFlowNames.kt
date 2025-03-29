/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.live.update

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.SysMLBasicFeature
import java.beans.PropertyChangeEvent

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
            .test(evt) && (feature as SysMLBasicFeature).envOptionsHelper.isLiveNamePropagationActivated
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
