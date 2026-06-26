package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.configurator

import com.nomagic.actions.ActionsCategory
import com.nomagic.actions.ActionsManager
import com.nomagic.magicdraw.actions.BrowserContextAMConfigurator
import com.nomagic.magicdraw.actions.MDActionsCategory
import com.nomagic.magicdraw.ui.browser.Tree
import com.nomagic.actions.AMConfigurator
import javax.swing.Action

/**
 * Post-configurator that inserts separator categories into the OMFShowcase menu.
 *
 * Must run AFTER OMFBrowserConfigurator / OMFMainMenuConfigurator, because:
 *   1. OMF's configurator adds actions (including Group A and Group B) to the category
 *   2. This configurator then finds Group B and inserts a createSeparatorCategory() before it
 *
 * Priority LOW_PRIORITY ensures OMF's MEDIUM_PRIORITY configurator runs first.
 *
 * How separators work in MagicDraw (empirically confirmed):
 *   - ActionsCategory.createSeparatorCategory() creates a plain ActionsCategory with
 *     putValue("useAsSeparatorInUI", Boolean.TRUE). When the rendering engine finds an
 *     ActionsCategory entry with this flag in a parent category's action list, it renders
 *     it as a horizontal separator line instead of a menu item.
 *   - NMAction.BELONGS_TO_SEPARATE_GROUP_IN_UI does NOT produce visual separators.
 *   - MDAction's "group" constructor parameter does NOT produce visual separators.
 *   - The MDAction "group" parameter is for logical grouping only.
 *
 * To insert a separator at a specific position:
 *   1. Find the target category by name in the actionsManager
 *   2. Find the index of the action you want the separator before
 *   3. Insert ActionsCategory.createSeparatorCategory() at that index via addAction(sep, index)
 */
class ShowcaseSeparatorConfigurator : BrowserContextAMConfigurator, AMConfigurator {

    override fun getPriority(): Int = AMConfigurator.LOW_PRIORITY

    /** Browser context menu (right-click in containment tree). */
    override fun configure(actionsManager: ActionsManager, tree: Tree) {
        insertSeparators(actionsManager)
    }

    /** Main menu (called once at startup). */
    override fun configure(actionsManager: ActionsManager) {
        insertSeparators(actionsManager)
    }

    private fun insertSeparators(actionsManager: ActionsManager) {
        val category = actionsManager.getCategories()
            .filterIsInstance<MDActionsCategory>()
            .firstOrNull { it.name == "OMFShowcase" } ?: return

        insertSeparatorBefore(category, "Showcase: Group B — separator before this")
    }

    private fun insertSeparatorBefore(category: ActionsCategory, actionName: String) {
        val actions = category.getActions()
        val index = actions.indexOfFirst { it.getValue(Action.NAME) == actionName }
        if (index >= 0) {
            category.addAction(ActionsCategory.createSeparatorCategory(), index)
        }
    }
}
