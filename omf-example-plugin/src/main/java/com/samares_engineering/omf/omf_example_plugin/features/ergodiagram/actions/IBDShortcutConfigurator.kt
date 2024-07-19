package com.samares_engineering.omf.omf_example_plugin.features.ergodiagram.actions

import com.nomagic.actions.ActionsManager
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager
import com.nomagic.magicdraw.actions.ActionsProvider
import com.nomagic.magicdraw.ui.actions.BaseDiagramShortcutsConfigurator

class IBDShortcutConfigurator : BaseDiagramShortcutsConfigurator() {
    override fun configureCommonActions(actionsManager: ActionsManager) {
        super.configureCommonActions(actionsManager, null)
        val mdActionsManager = ActionsProvider.getInstance().creator.createGeneralActions()
        val category = mdActionsManager.getCategory("COMMON_DIAGRAMS")
        actionsManager.addCategory(category)
        category!!.addAction(mdActionsManager.getActionFor("Delegate"))
    }

    override fun configure(var1: ActionsManager) {
        this.configureCommonActions(var1)
    }

    fun register() {
        val actionManager = ActionsConfiguratorsManager.getInstance()
        actionManager.addAnyDiagramShortcutsConfigurator(this)
    }
}