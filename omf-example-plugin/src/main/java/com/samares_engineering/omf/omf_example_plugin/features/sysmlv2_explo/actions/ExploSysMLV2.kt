package com.samares_engineering.omf.omf_example_plugin.features.sysmlv2_explo.actions

import com.dassault_systemes.modeler.foundation.project.ModelElementProject
import com.dassault_systemes.modeler.foundation.project.ProjectProvider
import com.dassault_systemes.modeler.kerml.model.kerml.Element
import com.dassault_systemes.modeler.sysml.textual.core.SysMLTextualProjectHelper
import com.dassault_systemes.modeler.sysml.textual.core.interfaces.ISysMLTextualProject
import com.dassault_systemes.modeler.sysml.textual.project.SysMLTextualProjectModelBased
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.V2ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils

@DiagramAction
@BrowserAction
@DeactivateListener
@MDAction(actionName = "Explo SysMLV2", category = "")
class ExploSysMLV2 : V2ElementUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        if (OMFUtils.isProjectVoid()) return false
        return true
    }

    override fun actionToPerform(selectedElements: List<Element>) {

        val selectedElement = selectedElements[0]
        val namespace = selectedElement.owningNamespace
        var project = ModelElementProject.getProject(selectedElement)
        var textualProject = SysMLTextualProjectModelBased(project)
        SysMLTextualProjectHelper.getContent(project as ISysMLTextualProject, namespace!!.id)


        OMFLogger2.toUI().warning("ExploSysMLV2 actionToPerform")
    }


}
