package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import java.awt.event.KeyEvent
import javax.swing.ImageIcon

/**
 * Demonstrates: setting an icon and mnemonic on a menu action — two features the OMF annotation
 * system does NOT expose. Access them by overriding initMenuActions() and post-configuring
 * the NMAction after calling super.
 *
 * Icon — NMAction.setSmallIcon(Icon):
 *   - Standard 16x16 icon displayed beside the action name in menus.
 *   - Use the Kotlin property syntax: menuNMAction?.smallIcon = ImageIcon(...)
 *     This calls NMAction.setSmallIcon(), which MagicDraw reads when rendering menus.
 *   - Supply a javax.swing.Icon (e.g. ImageIcon from a classpath resource).
 *   - If the resource is null, MagicDraw renders the action without an icon.
 *
 * Mnemonic — NMAction.setMnemonicKey(int):
 *   - NMAction has setMnemonicKey(int) / getMnemonicKey() which are PUBLIC — use these directly.
 *   - There is also setMnemonic(int) visible in some decompilers but that one may be
 *     package-private. Use setMnemonicKey() to be safe.
 *   - To trigger: open the OMFShowcase menu (click on it), then press 'I' alone.
 *   - WRONG: pressing Alt while a menu is open navigates the menu bar — it does NOT
 *     activate the mnemonic. Press just the letter while the dropdown is visible.
 */
@BrowserAction
@MenuAction
@com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction(
    actionName = "Showcase: Icon + Mnemonic",
    category = "OMFShowcase"
)
class ShowcaseIconMnemonicAction : ElementUIAction() {

    override fun initMenuActions() {
        super.initMenuActions()

        val iconUrl = javaClass.getResource("/img/rickroll-icon.gif")
        if (iconUrl != null) menuNMAction?.smallIcon = ImageIcon(iconUrl)

        menuNMAction?.setMnemonicKey(KeyEvent.VK_I)
    }

    override fun checkAvailability(selectedElements: List<Element>): Boolean = isProjectOpened

    override fun actionToPerform(selectedElements: List<Element>) {
        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Icon + Mnemonic Action")
                .text(" triggered. Open the OMFShowcase menu, then press 'I' (no Alt) to use the mnemonic.")
        )
    }
}
