/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.apiserver.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;
import com.samares_engineering.omf.omf_public_features.apiserver.APIEnvOptionsHelper;
import com.samares_engineering.omf.omf_public_features.apiserver.OMFApiServer;

import java.util.List;

@MenuAction
@DeactivateListener
@MDAction(actionName = "Restart API Server", category = "OMF.OMF ADVANCED")
public class RestartAPIServerAction extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(isProjectVoid()) return false;
        return true;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            if(OMFApiServer.getInstance().isStarted())
                OMFApiServer.getInstance().stopServer();
            int port = APIEnvOptionsHelper.getInstance(getFeature()).getServerPort();
            OMFApiServer.getInstance().startServer(port);
        } catch (Exception e) {
            throw new OMFCriticalException("Error while restarting API server, this will strongly impact features using API Server." +
                    " Please contact the plugin provider.", e);
        }
    }
}