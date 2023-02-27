/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.ui.actions;


import com.nomagic.magicdraw.actions.ActionsGroups;
import com.nomagic.magicdraw.actions.MDStateAction;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.openapi.uml.SessionManager;
import com.samares_engineering.omf.omf_core_framework.utils.OMFConstants;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogLevel;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;

import javax.annotation.CheckForNull;
import java.awt.event.ActionEvent;

public class DebugMode extends MDStateAction {

    protected Project project = null;

    public DebugMode(@CheckForNull String s, @CheckForNull String s1) {
        super(s, s1, null, ActionsGroups.PROJECT_OPENED_RELATED);
    }

    public void actionPerformed(ActionEvent e) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if (OMFConstants.DEBUG_MODE_ACTIVATED) {
                        OMFLogger.getInstance().log("DEBUG MODE DEACTIVATED", null, OMFLogLevel.INFO);
                        OMFConstants.DEBUG_MODE_ACTIVATED = false;
                    } else {
                        OMFLogger.getInstance().log("DEBUG MODE ACTIVATED\"", null, OMFLogLevel.INFO);
                        OMFConstants.DEBUG_MODE_ACTIVATED = true;
                    }


                } catch (Exception e) {
                    if (SessionManager.getInstance().isSessionCreated(Application.getInstance().getProject())) {
                        SessionManager.getInstance().closeSession(Application.getInstance().getProject());
                    }
                    OMFErrorHandler.handleException(e, false);
                }

            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


}
