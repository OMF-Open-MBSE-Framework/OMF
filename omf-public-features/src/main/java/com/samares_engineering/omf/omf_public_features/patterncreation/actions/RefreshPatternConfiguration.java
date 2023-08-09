/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.patterncreation.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.patterncreation.PatternCreationFeature;

import java.util.List;

@MenuAction
@DeactivateListener
@MDAction(actionName = "Refresh Pattern Configuration", category = "PATTERNS")
public class RefreshPatternConfiguration extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null) return false;
       return true;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            ((PatternCreationFeature) getFeature()).refreshPatterConfiguration();
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, true);
        }
    }



}