/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.plugin.features.dev.actions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares.omf.core.actions.v2.AGenericAction;
import com.samares.omf.core.actions.v2.annotations.BrowserAction;
import com.samares.omf.core.actions.v2.annotations.MDAction;
import com.samares.omf.core.listeners.OMFListenerManager;
import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.OMFLogLevel;
import com.samares.omf.core.errors.OMFLogger;

import java.util.List;

@BrowserAction
@MDAction(actionName = "Toggle listener activation", category = "Dev")
public class ResetListeners extends AGenericAction {
    public boolean activated = true;
    protected Project project = null;

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            if (activated) {
                OMFListenerManager.getInstance().deactivateAllListeners();
                OMFLogger.getInstance().log("DEACTIVATED", null, OMFLogLevel.INFO);
            } else {
                OMFListenerManager.getInstance().activateAllListeners();
                OMFLogger.getInstance().log("ACTIVATED", null, OMFLogLevel.INFO);
            }
            activated = !activated;

        } catch (Exception e) {
            if (SessionManager.getInstance().isSessionCreated(Application.getInstance().getProject())) {
                SessionManager.getInstance().closeSession(Application.getInstance().getProject());
            }
            OMFErrorHandler.handleException(e, false);
        }
    }

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return true;
    }
}
