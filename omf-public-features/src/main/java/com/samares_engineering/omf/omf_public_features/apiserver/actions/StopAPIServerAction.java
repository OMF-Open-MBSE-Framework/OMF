/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.apiserver.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OMFFeatureException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.apiserver.OMFApiServer;

import java.util.List;

@MenuAction
@DeactivateListener
@MDAction(actionName = "Stop API Server", category = "OMF.OMF ADVANCED")
public class StopAPIServerAction extends AUIAction {

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.getProject() == null) return false;
        return true;
    }


    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            if(OMFApiServer.getInstance().isStarted())
                OMFApiServer.getInstance().stopServer();
            OMFLogger.getInstance().info("API Server stopped");
        } catch (Exception e) {
            OMFErrorHandler.handleException(new OMFFeatureException("Error while starting API server, this will strongly impact features using API Server." +
                    "\nPlease contact the plugin: " + feature.getPlugin().getName() + " provider",
                    getFeature(), e, GenericException.ECriticality.CRITICAL));
        }

    }


}