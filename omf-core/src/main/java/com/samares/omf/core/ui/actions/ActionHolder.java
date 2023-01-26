/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.ui.actions;

import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.samares.omf.core.listeners.OMFListenerManager;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.utils.errorManagement.OMFErrorHandler;

import java.awt.event.ActionEvent;

public abstract class ActionHolder {

    DiagramActionImpl diagram_action;
    BrowserActionImpl browser_action;
    String actionName;
    String sessionName;

    public ActionHolder(String actionName) {
        this(actionName, "Executing action: " + actionName);
    }

    public ActionHolder(String actionName, String sessionName) {
        this.actionName = actionName;
        this.sessionName = sessionName;
        browser_action = new BrowserActionImpl(this);
        diagram_action = new DiagramActionImpl(this);
    }

    public void diagramActionPerformed(ActionEvent e) {
        Runnable runnable = () -> {
            try {
                execBehavior(e, diagram_action, browser_action);
            } catch (Exception exception) {
                OMFErrorHandler.handleException(exception, true);
            }
        };
        OMFListenerManager.getInstance().removeAllListeners();
        try {
            SessionManager.getInstance().executeInsideSession(OMFUtils.currentProject, sessionName, runnable);
        } catch (Exception exception) {
            OMFErrorHandler.handleException(exception, false);
        }
        OMFListenerManager.getInstance().activateAllListeners();
    }

    public abstract void execBehavior(ActionEvent e, DiagramActionImpl diagram_action, BrowserActionImpl browser_action);

    public DefaultDiagramAction getDiagram_action() {
        return diagram_action;
    }

    public DefaultBrowserAction getBrowser_action() {
        return browser_action;
    }


    protected class DiagramActionImpl extends DefaultDiagramAction {
        private ActionHolder behaviorHolder;

        public DiagramActionImpl(ActionHolder behaviorHolder) {
            super("", actionName, null, null);
            this.behaviorHolder = behaviorHolder;
        }

        public void actionPerformed(ActionEvent e) {
            behaviorHolder.diagramActionPerformed(e);
        }
    }

    protected class BrowserActionImpl extends DefaultBrowserAction {
        private ActionHolder behaviorHolder;

        public BrowserActionImpl(ActionHolder behaviorHolder) {
            super("", actionName, null, null);
            this.behaviorHolder = behaviorHolder;
        }

        public void actionPerformed(ActionEvent e) {
            behaviorHolder.diagramActionPerformed(e);
        }
    }
}
