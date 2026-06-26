package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.magicdraw.ui.browser.Node
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction

/**
 * Demonstrates: @BrowserAction only — never appears in diagram context menus or the main menu.
 *
 * Also demonstrates accessing the raw MagicDraw Tree Node objects via getSelectedBrowserNodes(),
 * which provides information the OMF element list alone does not expose:
 *   - Node.userObject  : the element (same as what getSelectedBrowserElements() returns)
 *   - Node.isLeaf      : whether this node has no children in the containment tree
 *   - Node.toString()  : the display label used in the browser
 *
 * Base MagicDraw API note:
 *   OMFBrowserConfigurator.configure(ActionsManager, Tree) receives the full Tree object, but
 *   OMF discards it. If you need the Tree (e.g., to scroll, expand, or inspect the entire tree
 *   state), you must subclass OMFBrowserConfigurator and store the Tree yourself.
 */
@BrowserAction
@MDAction(actionName = "Showcase: Browser Only (Node Info)", category = "OMFShowcase")
class ShowcaseBrowserOnlyAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean =
        isProjectOpened && selectedElements.isNotEmpty()

    override fun actionToPerform(selectedElements: List<Element>) {
        val nodes: Array<Node> = getSelectedBrowserNodes()
        val log = OMFLog()
            .bold("Browser-Only Action")
            .text(" — raw Node info for ${nodes.size} selected node(s):")

        nodes.forEach { node ->
            val element = node.userObject
            log.text("\n  Node: ").bold(node.toString())
                .text(" | isLeaf=").bold(node.isLeaf.toString())
                .text(" | userObject type=").bold(element?.javaClass?.simpleName ?: "null")
        }

        OMFLogger2.toAll().log(log)
    }
}
