/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.cloneexample.actions;

import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.clone.CloneManager;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.InternalDiagramManagement;

import java.util.List;

@BrowserAction
@DiagramAction
@DeactivateListener
@MDAction(actionName = "Clone Part", category = "Clone")
public class ClonePart extends AUIAction {

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return OMFUtils.currentProject != null
                && !selectedElements.isEmpty()
                && selectedElements.stream()
                .filter(Profile._getSysmlAdditionalStereotypes().partProperty()::is)
                .map(Property.class::cast)
                .allMatch(property -> property.getType() != null);
    }


    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            DiagramPresentationElement activeDiagram = OMFUtils.currentProject.getActiveDiagram();
            CloneManager cloneManager = new CloneManager();
            selectedElements.stream()
                    .map(Property.class::cast)
                    .forEach(part -> {
                        cloneManager.clonePart(part);
                        InternalDiagramManagement.layoutSinglePart((Property) cloneManager.retrieveClonedElement(part), activeDiagram.getDiagram());
                    });
        } catch (Exception e) {
            OMFErrorHandler.handleException(e, true);
        }
    }


}