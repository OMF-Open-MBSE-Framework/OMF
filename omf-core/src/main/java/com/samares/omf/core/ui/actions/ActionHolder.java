/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.ui.actions;

import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction;
import com.nomagic.magicdraw.ui.browser.actions.DefaultBrowserAction;
import com.samares.omf.core.listeners.ListenerManager;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.errors.OMFErrorHandler;

import java.awt.event.ActionEvent;

public abstract class ActionHolder {
    DiagramActionImpl diagramAction;
    BrowserActionImpl browserAction;
    String actionName;
    String sessionName;

    public ActionHolder(String actionName) {
        this(actionName, "Executing action: " + actionName);
    }

    public ActionHolder(String actionName, String sessionName) {
        this.actionName = actionName;
        this.sessionName = sessionName;
        browserAction = new BrowserActionImpl(this);
        diagramAction = new DiagramActionImpl(this);
    }

    public void diagramActionPerformed(ActionEvent e) {
        Runnable runnable = () -> {
            try {
                execBehavior(e, diagramAction, browserAction);
            } catch (Exception exception) {
                OMFErrorHandler.handleException(exception, true);
            }
        };
        ListenerManager.getInstance().removeAllListeners();
        try {
            SessionManager.getInstance().executeInsideSession(OMFUtils.currentProject, sessionName, runnable);
        } catch (Exception exception) {
            OMFErrorHandler.handleException(exception, false);
        }
        ListenerManager.getInstance().activateAllListeners();
    }

    public abstract void execBehavior(ActionEvent e, DiagramActionImpl diagramAction, BrowserActionImpl browserAction);

    public DefaultDiagramAction getDiagramAction() {
        return diagramAction;
    }

    public DefaultBrowserAction getBrowserAction() {
        return browserAction;
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
