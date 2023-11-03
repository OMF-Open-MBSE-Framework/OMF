/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.customizationValidationFeature.actions;

import com.nomagic.uml2.MagicDrawProfile;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.samares_engineering.omf.omf_core_framework.errors.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;

import java.util.List;
import java.util.stream.Collectors;

@DiagramAction
@BrowserAction
@MDAction(actionName = "Put All customization Attribute To standard", category = "ESA")
public class PutAllCustomizationAttributeToStandard extends AUIAction {


    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return selectedElements.stream().anyMatch(Package.class::isInstance);
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        Package rootPackage = (Package) selectedElements.get(0);
        MagicDrawProfile.CustomizationStereotype custoStereotype = Profile._getMagicDraw().customization();
        List<Class> allCustos = rootPackage.getOwnedElement().stream()
                .filter(custoStereotype::is)
                .map(Class.class::cast)
                .collect(Collectors.toList());

        for (Class customization : allCustos) {
            List<String> displayedProperties = custoStereotype.getStandardExpertConfiguration(customization).stream()
                    .filter(s -> s.contains("SPF"))
                    .map(s -> s.replaceAll("<html><head><title>SPF</title></head><body><p>", ""))
                    .map(s -> s.replaceAll("</p></body></html>", ""))
                    .collect(Collectors.toList());

            customization.getOwnedElement().stream()
                    .filter(Property.class::isInstance)
                    .map(Property.class::cast)
                    .filter(property -> property.getType() != null)
                    .filter(property -> !displayedProperties.contains(property.getName()))
                    .forEach(property -> {
                        List<String> standardExpertConfiguration = custoStereotype.getStandardExpertConfiguration(customization);
                        standardExpertConfiguration
                                .add("<html><head><title>SPF</title></head><body><p>" + property.getName() + "</p></body></html>");
                        custoStereotype.setStandardExpertConfiguration(customization, standardExpertConfiguration);
                        OMFLogger.getInstance()
                                .warn("[" + customization.getName() + "]" + " Property " + property.getName()
                                        + " has been put to Standard: ", property);
                    });
        }
    }
}
