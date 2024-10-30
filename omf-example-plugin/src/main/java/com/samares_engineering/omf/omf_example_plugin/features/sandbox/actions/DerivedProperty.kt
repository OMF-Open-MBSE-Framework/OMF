package com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions

import com.nomagic.uml2.MagicDrawProfile
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile

@MenuAction
@BrowserAction
@DiagramAction
@MDAction(actionName = "Sandbox UI Action", category = "OMF")
class DerivedProperty : AUIAction() {
    override fun actionToPerform(selectedElements: List<Element>) {
        val myReq = selectedElements.get(0)
        val myStr = myReq.appliedStereotype.get(0)
        val mdProfile = MagicDrawProfile.getInstance(myStr);// get tagged values where the stereotype is value
        val derivedProperties = myStr._elementTaggedValue
            .asSequence()
            .filter { it.tagDefinition != null}
            .filter { it.tagDefinition!!.name.equals(MagicDrawProfile.CustomizationStereotype.CUSTOMIZATIONTARGET) }
            .map { it.taggedValueOwner }
            .filterIsInstance<Class>()
            .filter { mdProfile.customization().`is`(it) }
            .map { it.ownedAttribute }
            .flatten()
            .filter { mdProfile.derivedPropertySpecification().`is`(it) }
            .toList()

//        Profile._getMagicDraw().customization().get
    }

    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return selectedElements.size == 2
    }
}
