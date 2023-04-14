/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.apiserver.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.feature.errors.FeatureException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.apiserver.APIEnvOptionsHelper;
import com.samares_engineering.omf.omf_public_features.apiserver.OMFApiServer;

import java.util.List;

@MenuAction
@DeactivateListener
@MDAction(actionName = "Restart API Server", category = "OMF ADVANCED")
public class RestartAPIServerAction extends AUIAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null) return false;
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
            OMFErrorHandler.handleException(new FeatureException("Error while starting API server, this will strongly impact features using API Server." +
                    "\nPlease contact the plugin: " + feature.getPlugin().getName() + " provider", e, GenericException.ECriticality.CRITICAL));
        }

    }


}