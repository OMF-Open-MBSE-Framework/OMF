/*******************************************************************************
 * @copyright ...
 */
package com.samares_engineering.omf.omf_core_framework.genarchimodel.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*
import com.samares_engineering.omf.omf_core_framework.genarchimodel.generator.ModelArchitectureGenerator


@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Generate Model Archi", category = "OMF.ArchiGeneration")
class GenerateArchiModel_V3 : AUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        if (isProjectVoid) return false
        if (selectedElements.isEmpty()) return false

        return true
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        val owner = selectedElements[0]

        ModelArchitectureGenerator("com.samares_engineering.omf",
            "C:\\Workspace\\Plugins\\OMF_Private",
            feature.plugin).generateCodeModelArchitecture(owner)
    }

}
