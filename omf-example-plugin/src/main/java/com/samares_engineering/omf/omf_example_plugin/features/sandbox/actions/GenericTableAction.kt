package com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions

import com.nomagic.diagramtable.Table
import com.nomagic.diagramtable.actions.base.DefaultTableAction
import com.nomagic.magicdraw.core.Application
import com.nomagic.uml2.MagicDrawProfile
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.transaction.TransactionCommitListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import java.awt.event.ActionEvent

@MenuAction
@BrowserAction
@DiagramAction
@MDAction(actionName = "Sandbox UI Action", category = "OMF")
class GenericTableAction : AUIAction() {
    override fun actionToPerform(selectedElements: List<Element>) {


    }

    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return OMFUtils.isProjectOpened()
    }
}



class MyGenericTableAction: DefaultTableAction("MyGenericTableAction", "MyGenericTableAction2", null) {
    override fun actionPerformed(p0: ActionEvent?, p1: Table?) {

    }


}