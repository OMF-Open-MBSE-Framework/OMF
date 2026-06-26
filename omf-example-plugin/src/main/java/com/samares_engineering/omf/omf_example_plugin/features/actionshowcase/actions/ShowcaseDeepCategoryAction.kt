package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction

/**
 * Demonstrates: deep category nesting using dot-separated hierarchy in the category string.
 *
 * OMF parses the category string by splitting on "." and recursively creates or finds
 * MDActionsCategory objects. The result is nested submenus:
 *
 *   OMFShowcase
 *   └── Actions
 *       └── Deep
 *           └── Nested
 *               └── [this action]
 *
 * Rules:
 *   - Each dot segment becomes one level of submenu.
 *   - Segments are reused if a category with the same name already exists at that level.
 *   - The category string must not be empty (OMF throws an error otherwise).
 *   - A single-segment string like "OMFShowcase" creates a flat top-level group.
 *
 * Implementation detail:
 *   UIActionConfiguratorUtils.findOrCreateCategory() handles creation and caching.
 *   MDActionsCategory.setNested(true) is called automatically by OMF on each new category.
 */
@BrowserAction
@MenuAction
@MDAction(actionName = "Showcase: Deep Nested Category", category = "OMFShowcase.Actions.Deep.Nested")
class ShowcaseDeepCategoryAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean = isProjectOpened

    override fun actionToPerform(selectedElements: List<Element>) {
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Deep Category Action")
                .text(" lives at OMFShowcase → Actions → Deep → Nested.")
        )
    }
}
