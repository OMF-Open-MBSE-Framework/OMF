package com.samares_engineering.omf.omf_example_plugin.features.elementspecification.actions

import com.nomagic.magicdraw.ui.dialogs.specifications.SpecificationDialogManager
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils

@DiagramAction
@DeactivateListener
@MDAction(actionName = "Add Custom Specification", category = "OMF EXAMPLE")
class AddCustomSpecificationOnElement : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return OMFUtils.getProject() != null
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        SpecificationDialogManager.getManager().addConfigurator(Element::class.java, SpecificationNodeConfigurator())
    }
}
