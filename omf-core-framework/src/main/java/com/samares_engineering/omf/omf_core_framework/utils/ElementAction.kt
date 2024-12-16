/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.utils

import com.nomagic.magicdraw.core.Application
import com.nomagic.magicdraw.ui.dialogs.specifications.SpecificationDialogManager
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element

class ElementAction(var element: Element?) {
    fun selectInBrowser() {
        if (element == null)  return

        val browser = Application.getInstance().mainFrame.browser ?: return
        val tree = browser.containmentTree
        var parent = element
        tree.open()
        var treePath = tree.openNode(parent)

        while (treePath == null && parent != null) {
            parent = parent.owner
            treePath = tree.openNode(parent)
        }

        if (treePath == null) {
            Application.getInstance().guiLog.showMessage(
                "Element " +
                        element!!.humanName + " not found in browser."
            )
        }
    }

    fun openSpecification() {
        if (element == null) return
        SpecificationDialogManager.getManager().editSpecification(element)
    }
}
