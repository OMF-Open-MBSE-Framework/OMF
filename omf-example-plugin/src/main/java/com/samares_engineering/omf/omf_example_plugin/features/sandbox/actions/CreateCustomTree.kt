package com.samares_engineering.omf.omf_example_plugin.features.sandbox.actions

import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils

@MenuAction
@BrowserAction
@DiagramAction
@MDAction(actionName = "Create Custom Tree", category = "OMF")
class CreateCustomTree : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return OMFUtils.isProjectOpened()
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        val browserTree = BrowserTreeFactory.addBrowserTree(
            Application.getInstance().mainFrame.browser,
            "S1",
            "S2",
            MyTreeRoot(OMFUtils.getProject()),
            true,
            true
        )
        browserTree!!.open()
    }
}


class MyBrowserTabTree(browser: Browser?, componentInfo: BrowserComponentInfo?, name: String?) : BrowserTabTree(browser, componentInfo, name) {
    override fun configureToolbarActions(p0: ActionsManager?) {
        TODO("Not yet implemented")
    }

    override fun configureContextActions(p0: ActionsManager?) {
        TODO("Not yet implemented")
    }
}

class MyTreeRoot(project: Project): TreeRoot(project) {

}