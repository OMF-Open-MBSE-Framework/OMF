/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.patterncreation.creation;

import com.nomagic.uml2.ext.magicdraw.classes.mddependencies.Dependency;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule.ARule;
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker;
import com.samares_engineering.omf.omf_public_features.patterncreation.PatternCreationFeature;
import com.samares_engineering.omf.omf_public_features.patterncreation.PatternCreationHelper;


import java.beans.PropertyChangeEvent;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

public class ElementCreatorFromPattern extends ARule {
    @Override
    protected boolean eventMatches(PropertyChangeEvent evt) {
        Set<Stereotype> configuredSTR = ((PatternCreationFeature) getFeature()).getConfiguredSTR();
        Predicate<PropertyChangeEvent> hasAStrConfigured = e ->
                ((Element) e.getSource()).getAppliedStereotype().stream()
                        .anyMatch(configuredSTR::contains);

        return new EventChecker()
                .isElementCreated()
                .isTrue(hasAStrConfigured)
                .test(evt);
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        try {
            Set<Stereotype> configuredSTR = ((PatternCreationFeature) getFeature()).getConfiguredSTR();
            Element createdElement = (Element) e.getSource();

            List<Dependency> configuredOnCreationDependencies = PatternCreationHelper.getAllOnCreationDependencyFromElement(createdElement, configuredSTR);

            PatternCreationHelper.replaceElementWithGeneratedPatterns(createdElement, configuredOnCreationDependencies);

        }catch (Exception uncheckedException){
            OMFErrorHandler.handleException(uncheckedException);
        }

        return e;
    }




    @Override
    public boolean isBlocking() {
        return false;
    }
}
