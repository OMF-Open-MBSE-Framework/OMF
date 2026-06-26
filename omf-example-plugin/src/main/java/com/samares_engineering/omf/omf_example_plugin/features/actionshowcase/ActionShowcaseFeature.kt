package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase

import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions.*
import com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.configurator.ShowcaseSeparatorConfigurator

/**
 * Feature that exhaustively showcases MagicDraw action API options.
 *
 * Each action demonstrates one aspect of the API. See each class for detailed documentation.
 *
 * Context placement:
 *   ShowcaseAllContextsAction           — @MenuAction @BrowserAction @DiagramAction together
 *   ShowcaseBrowserOnlyAction           — @BrowserAction only + raw Node access
 *   ShowcaseDiagramOnlyAction           — @DiagramAction only + PresentationElement access
 *   ShowcaseMenuOnlyAction              — @MenuAction only, available without a project
 *
 * Availability patterns:
 *   ShowcaseTypeFilterAction            — only when a Package is selected
 *   ShowcasePerContextAvailabilityAction — different condition per context
 *   ShowcaseMultiSelectAction           — only when 2+ elements are selected
 *
 * Category hierarchy:
 *   ShowcaseDeepCategoryAction          — 4-level dot-separated category
 *
 * Keyboard shortcut:
 *   ShowcaseKeyboardShortcutAction      — Ctrl+Shift+F11 via keyStroke annotation field
 *
 * State / checkbox:
 *   ShowcaseStatefulCheckboxAction      — extends StateAction (MDStateAction under the hood)
 *
 * Listener control:
 *   ShowcaseDeactivateListenerAction    — @DeactivateListener pauses live-action listeners
 *
 * Per-context behavior:
 *   ShowcaseContextBehaviorAction       — different execute* method per context
 *
 * Base MagicDraw API (beyond OMF annotations):
 *   ShowcaseIconMnemonicAction          — icon + mnemonic via initMenuActions() override
 *   ShowcaseGroupSiblingAction          — plain action, no separator flag
 *   ShowcaseGroupSeparatorAction        — separator inserted before it via ShowcaseSeparatorConfigurator
 *   ShowcaseDiagramRequestorAction      — initDiagramActions() override + requestor PE gap docs
 *   ShowcaseExploreGroupsAction         — logs main-menu categories/groups/keystrokes at runtime
 */
class ActionShowcaseFeature : SimpleFeature("Action Showcase") {

    override fun onRegistering() {
        val separatorConfigurator = ShowcaseSeparatorConfigurator()
        ActionsConfiguratorsManager.getInstance().addContainmentBrowserContextConfigurator(separatorConfigurator)
        ActionsConfiguratorsManager.getInstance().addMainMenuConfigurator(separatorConfigurator)
    }

    override fun initFeatureActions(): List<UIAction> = listOf(
        // Context placement
        ShowcaseAllContextsAction(),
        ShowcaseBrowserOnlyAction(),
        ShowcaseDiagramOnlyAction(),
        ShowcaseMenuOnlyAction(),
        // Availability
        ShowcaseTypeFilterAction(),
        ShowcasePerContextAvailabilityAction(),
        ShowcaseMultiSelectAction(),
        // Category / shortcut / state
        ShowcaseDeepCategoryAction(),
        ShowcaseKeyboardShortcutAction(),
        ShowcaseStatefulCheckboxAction(),
        // Listener control
        ShowcaseDeactivateListenerAction(),
        // Per-context behavior
        ShowcaseContextBehaviorAction(),
        // Base MagicDraw API
        ShowcaseIconMnemonicAction(),
        ShowcaseGroupSiblingAction(),
        ShowcaseGroupSeparatorAction(),
        ShowcaseDiagramRequestorAction(),
        ShowcaseExploreGroupsAction()
    )
}
