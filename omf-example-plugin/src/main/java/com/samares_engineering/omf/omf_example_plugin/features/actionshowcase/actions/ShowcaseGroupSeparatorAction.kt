package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction

/**
 * Target action for the separator demo. A visual separator line is inserted BEFORE this
 * action in the OMFShowcase menu by ShowcaseSeparatorConfigurator (registered from
 * ActionShowcaseFeature.onRegistering).
 *
 * How visual separators work in MagicDraw:
 *   ActionsCategory.createSeparatorCategory() creates a plain ActionsCategory with
 *   putValue("useAsSeparatorInUI", Boolean.TRUE). Insert this into the parent category
 *   at the desired index via category.addAction(sep, index).
 *
 * What does NOT work:
 *   - NMAction.BELONGS_TO_SEPARATE_GROUP_IN_UI — the constant exists but setting it on an
 *     action produces NO visual separator.
 *   - MDAction constructor's "group" parameter — logical grouping only, no visual effect.
 *
 * Timing requirement:
 *   The separator must be inserted AFTER the target action is in the category. Register a
 *   second AMConfigurator with LOW_PRIORITY; OMF's configurator runs at MEDIUM_PRIORITY first.
 *
 * See ShowcaseSeparatorConfigurator for the full implementation.
 */
@BrowserAction
@MenuAction
@MDAction(actionName = "Showcase: Group B — separator before this", category = "OMFShowcase")
class ShowcaseGroupSeparatorAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean = isProjectOpened

    override fun actionToPerform(selectedElements: List<Element>) {
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Group B — separator before this")
                .text(": separator inserted via ActionsCategory.createSeparatorCategory() in ShowcaseSeparatorConfigurator.")
        )
    }
}
