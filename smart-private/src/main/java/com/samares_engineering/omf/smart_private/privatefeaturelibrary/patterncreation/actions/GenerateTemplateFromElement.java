/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation.actions;

import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation.PatternCreationFeature;
import com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation.PatternCreationHelper;
import com.samares_engineering.omf.smart_private.privatefeaturelibrary.patterncreation.profile.PatternCreatorProfile;

import java.util.List;
import java.util.Set;

@DiagramAction
@BrowserAction
@DeactivateListener
@MDAction(actionName = "Generate and replace Pattern structure From Element", category = "OMF.PATTERNS")
public class GenerateTemplateFromElement extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if(isProjectVoid()) return false;
        if(selectedElements.size() != 1) return false;
        if(!PatternCreatorProfile.getInstance().patternInstance().is(selectedElements.get(0))) return false;
       return true;
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        Set<Stereotype> configuredSTR = ((PatternCreationFeature) getFeature()).getConfiguredSTR();
        Element createdElement = selectedElements.get(0);

        List<Dependency> configuredOnCreationDependencies = PatternCreationHelper.getAllOnCreationDependencyFromElement(createdElement, configuredSTR);
        PatternCreationHelper.replaceElementWithGeneratedPatterns(createdElement, configuredOnCreationDependencies);
    }
}