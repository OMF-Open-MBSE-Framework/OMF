package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction

/**
 * Plain action in Group A — no separator flag, no special configuration.
 * ShowcaseSeparatorConfigurator inserts ActionsCategory.createSeparatorCategory() between
 * this action and ShowcaseGroupSeparatorAction (Group B) at menu-build time.
 */
@BrowserAction
@MenuAction
@MDAction(actionName = "Showcase: Group A — no separator", category = "OMFShowcase")
class ShowcaseGroupSiblingAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean = isProjectOpened

    override fun actionToPerform(selectedElements: List<Element>) {
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Group A — no separator")
                .text(": this action has no BELONGS_TO_SEPARATE_GROUP_IN_UI flag.")
                .text(" The separator is on Group B (below this action), not on this one.")
        )
    }
}
