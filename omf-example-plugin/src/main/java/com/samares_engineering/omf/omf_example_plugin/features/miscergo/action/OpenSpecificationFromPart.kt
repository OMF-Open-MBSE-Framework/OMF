/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.miscergo.action

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TypedElement
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.utils.ElementAction

@DiagramAction
@BrowserAction
@MDAction(actionName = "Open Type Specification", category = "")
class OpenSpecificationFromPart : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return selectedElements.size == 1 && selectedElements[0] is TypedElement && (selectedElements[0] as TypedElement).type != null

    }


    override fun actionToPerform(selectedElements: List<Element>) {
        val element = selectedElements[0] as TypedElement
        ElementAction(element.type).openSpecification()

    }
}
