/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.patterncreation.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFeatureException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.patterncreation.PatternCreationHelper;
import com.samares_engineering.omf.omf_public_features.patterncreation.profile.PatternCreatorProfile;

import java.util.List;
import java.util.stream.Collectors;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Generate Template From Stereotype", category = "***PATTERNS")
public class GenerateTemplateFromSTR extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(OMFUtils.currentProject == null) return false;
        if(selectedElements.size() != 1) return false;
        Element selectedSTR = selectedElements.get(0);
        if(!(selectedSTR instanceof Stereotype)) return false;
       return true;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            Stereotype selectedSTR = (Stereotype) selectedElements.get(0);

            List<Dependency> configuredOnCreationDependencies = selectedSTR.getClientDependency().stream()
                    .filter(PatternCreatorProfile.getInstance().onCreation()::is)
                    .collect(Collectors.toList());
            if(configuredOnCreationDependencies.isEmpty())
                throw new OMFFeatureException('"' + selectedSTR.getName() + '"' + " has no OnCreation dependency configured for pattern generation.", getFeature(), GenericException.ECriticality.ALERT);

            Element rootModel = OMFUtils.currentProject.getPrimaryModel();
            PatternCreationHelper.replaceElementWithGeneratedPatterns(rootModel, configuredOnCreationDependencies);

        } catch (Exception e) {
            OMFErrorHandler.handleException(e, true);
        }
    }



}