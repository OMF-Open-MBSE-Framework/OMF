/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.utils;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.ui.browser.Browser;
import com.nomagic.magicdraw.ui.browser.ContainmentTree;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;

import javax.swing.tree.TreePath;
import java.lang.ref.WeakReference;

public class SelectInBrowserRunnable implements Runnable
{
    private final WeakReference<Element> mElement;

    public SelectInBrowserRunnable(Element element)
    {
        mElement = new WeakReference<>(element);
    }

    public void run()
    {
        Element element = mElement.get();
        if (element != null)
        {
            Browser browser = Application.getInstance().getMainFrame().getBrowser();
            if (browser != null)
            {
                ContainmentTree tree = browser.getContainmentTree();
                Element parent = element;
                tree.open();
                TreePath treePath = tree.openNode(parent);

                while (treePath == null && parent != null)
                {
                    parent = parent.getOwner();
                    treePath = tree.openNode(parent);
                }

                if (treePath == null)
                {
                    Application.getInstance().getGUILog().showMessage("Element " +
                            element.getHumanName() + " not found in browser.");
                }
            }
        }
    }
}
