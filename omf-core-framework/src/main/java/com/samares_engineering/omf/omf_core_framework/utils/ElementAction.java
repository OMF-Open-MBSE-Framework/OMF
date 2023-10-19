/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.utils;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.browser.Browser;
import com.nomagic.magicdraw.ui.browser.ContainmentTree;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.swing.tree.TreePath;

public class ElementAction {
    Element element;

    public ElementAction(Element element) {
        this.element = element;
    }

    public void selectInBrowser() {
        if (element == null) {
            return;
        }
        Browser browser = Application.getInstance().getMainFrame().getBrowser();
        if (browser == null) {
            return;
        }
        ContainmentTree tree = browser.getContainmentTree();
        Element parent = element;
        tree.open();
        TreePath treePath = tree.openNode(parent);

        while (treePath == null && parent != null) {
            parent = parent.getOwner();
            treePath = tree.openNode(parent);
        }

        if (treePath == null) {
            Application.getInstance().getGUILog().showMessage("Element " +
                    element.getHumanName() + " not found in browser.");
        }
    }
}
