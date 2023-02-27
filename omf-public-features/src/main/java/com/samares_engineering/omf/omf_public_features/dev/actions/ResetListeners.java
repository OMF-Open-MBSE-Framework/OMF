/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.dev.actions;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;

import java.util.List;

@BrowserAction
@MDAction(actionName = "Toggle listener activation", category = "Dev")
public class ResetListeners extends AUIAction {
    public boolean activated = true;
    protected Project project = null;

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            if (activated) {
                ListenerManager.getInstance().deactivateAllListeners();
                OMFLogger.getInstance().log("DEACTIVATED", null, OMFLogLevel.INFO);
            } else {
                ListenerManager.getInstance().activateAllListeners();
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
