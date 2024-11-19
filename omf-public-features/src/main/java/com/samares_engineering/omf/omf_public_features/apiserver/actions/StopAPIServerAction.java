/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.apiserver.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;
import com.samares_engineering.omf.omf_public_features.apiserver.OMFApiServer;

import java.util.List;

@MenuAction
@DeactivateListener
@MDAction(actionName = "Stop API Server", category = "OMF.OMF ADVANCED")
public class StopAPIServerAction extends AUIAction {

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(isProjectVoid()) return false;
        return true;
    }


    @Override
    public void actionToPerform(List<Element> selectedElements) {
        if(OMFApiServer.getInstance().isStarted())
            OMFApiServer.getInstance().stopServer();
        OMFLogger.infoToUIConsole("API Server stopped");
    }
}