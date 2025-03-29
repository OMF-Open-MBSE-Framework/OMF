/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.live.creation

import com.nomagic.magicdraw.sysml.util.SysMLProfile
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.SysMLBasicFeature
import java.beans.PropertyChangeEvent

class CreateAutoInterface_OnPortCreation : ALiveAction() {
    /**
     * Triggered only when a ProxyPort is created
     * @param evt event occurred in the model
     * @return true if the event matches the rule
     */
    override fun eventMatches(evt: PropertyChangeEvent): Boolean {
        return EventChecker()
            .isElementCreated()
            .isPort()
            .hasStereotype(Profile._getSysml().proxyPort().stereotype)
            .test(evt) && (feature as SysMLBasicFeature).envOptionsHelper.isAutoInterfaceCreationActivated
    }

    /**
     * Will create an InterfaceBlock and a FlowProperty with the same name as the port
     * @param evt event occurred in the model
     * @return the event
     */
    override fun process(evt: PropertyChangeEvent): PropertyChangeEvent {
        val port = evt.source as Port
        val interfaceBlock = SysMLFactory.getInstance().createInterfaceBlock(port.owner)
        interfaceBlock.name = "TO RENAME"
        port.type = interfaceBlock

        val flowProperty = SysMLFactory.getInstance().createFlowProperty(interfaceBlock)
        flowProperty.name = "TO RENAME"
        Profile._getSysml().flowProperty().setDirection(flowProperty, SysMLProfile.FlowDirectionKindEnum.OUT)

        return evt
    }

    override fun isBlocking(): Boolean {
        return false
    }
}
