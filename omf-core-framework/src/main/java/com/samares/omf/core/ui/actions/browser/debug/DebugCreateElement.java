/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.ui.actions.browser.debug;


import com.google.common.base.Function;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.browser.Node;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.listeners.ListenerManager;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.errors.OMFErrorHandler;

import java.awt.event.ActionEvent;
import java.lang.reflect.Method;

public class DebugCreateElement extends DefaultBrowserAction {
    public String name = "";
    public Function<Element, Element> functionToTrigger = null;
    public Method methodToTrigger = null;

    public DebugCreateElement(String name, Function<Element, Element> functionToTrigger) {
        super("", "[Debug]  create: " + name, null, null);
        this.name = name;
        this.functionToTrigger = functionToTrigger;
    }

    public DebugCreateElement(String name, Method methodToTrigger) {
        super("", "[Debug]  create: " + name, null, null);
        this.name = name;
        this.methodToTrigger = methodToTrigger;
    }


    public void actionPerformed(ActionEvent e) {
        Runnable runnable = () -> {
            try {
                Node[] elementSelection = getTree().getSelectedNodes();
                Element selectedElement = (Element) elementSelection[0].getUserObject();
                if (functionToTrigger != null)
                    functionToTrigger.apply(selectedElement);
                if (methodToTrigger != null)
                    methodToTrigger.invoke(null, selectedElement);
            } catch (Exception exception) {
                OMFErrorHandler.handleException(exception, true);
            }
            System.out.println("END OF RUNNABLE");
        };


        ListenerManager.getInstance().removeAllListeners();
        try {
            SessionManager.getInstance().executeInsideSession(OMFUtils.currentProject, "[Debug] create: " + name + " in progress", runnable);
        } catch (Exception exception) {
            OMFErrorHandler.handleException(exception, false);
        }
        ListenerManager.getInstance().activateAllListeners();
    }

}
