/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile

@MenuAction
@DiagramAction
@BrowserAction
@MDAction(actionName = "Propagate Port Name to Interface/Flow", category = "SysMLBasic")
class SyncAllNameAction : AUIAction() {
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
        //OLD CODE
        val port = selectedElements[0] as Port

        if (port.type == null) return

        port.type!!.name = port.name

        port.type!!.ownedElement.stream()
            .filter { element: Element? -> Profile._getSysml().flowProperty().`is`(element) }
            .map { obj: Element? -> Property::class.java.cast(obj) }
            .forEach { property: Property -> property.name = port.name }
    }
}
