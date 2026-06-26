package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction

/**
 * Demonstrates: keyboard shortcut registration via the keyStroke field of @MDAction.
 *
 * Format: pass ALL modifiers and the key as a SINGLE string element, space-separated.
 * AUIAction joins the array elements with "->" before calling KeyStroke.getKeyStroke(String),
 * so multi-element arrays never work — the "->" separator is invalid for that API.
 *
 * Examples:
 *   keyStroke = ["ctrl shift F11"]   → Ctrl+Shift+F11  ✓
 *   keyStroke = ["ctrl A"]           → Ctrl+A           ✓
 *   keyStroke = ["alt F4"]           → Alt+F4  (be careful — closes MagicDraw window)
 *   keyStroke = []                   → no shortcut (default)
 *
 *   WRONG: keyStroke = ["ctrl", "shift", "F11"]  → "ctrl->shift->F11" → KeyStroke returns null
 *
 * The shortcut is registered on all three NMAction instances (browser, diagram, menu) because
 * AUIAction creates each with the same KeyStroke from the annotation.
 *
 * Base MagicDraw API note:
 *   @BrowserAction(keyStroke = ...) and @DiagramAction(keyStroke = ...) exist on the annotations
 *   but OMF NEVER reads them — only @MDAction.keyStroke is used. All three action objects
 *   (browser, diagram, menu) receive the same KeyStroke from @MDAction.
 *
 * Shortcut conflicts: MagicDraw silently ignores duplicate shortcuts. Test carefully.
 */
@BrowserAction
@MenuAction
@MDAction(
    actionName = "Showcase: Keyboard Shortcut (Ctrl+Shift+F11)",
    category = "OMFShowcase",
    keyStroke = ["ctrl shift F11"]
)
class ShowcaseKeyboardShortcutAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean = isProjectOpened

    override fun actionToPerform(selectedElements: List<Element>) {
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Shortcut Action")
                .text(" triggered (can be invoked with Ctrl+Shift+F11 anywhere).")
        )
    }
}
