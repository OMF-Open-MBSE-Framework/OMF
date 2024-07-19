package com.samares_engineering.omf.omf_example_plugin.features.display.actions;

import com.nomagic.magicdraw.sysml.util.SysMLConstants;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.LayoutManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@DiagramAction
@DeactivateListener
@MDAction(actionName = "Display Inner layer", category = "")
public class DisplayInnerLayer extends AUIAction {

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if (OMFUtils.getProject() == null) return false;
        DiagramPresentationElement activeDiagram = OMFUtils.getProject().getActiveDiagram();
        if (activeDiagram == null) return false;
        String diagramType = activeDiagram.getDiagramType().getType();
        return diagramType.equals(SysMLConstants.SYSML_INTERNAL_BLOCK_DIAGRAM);

    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        LayoutManager layoutManager = new LayoutManager(getDiagramAction().getDiagram());
        if (selectedElements.isEmpty()) {
            displayDiagramFirstLevel(layoutManager);
        } else {
            displayPartsInnerLevel(selectedElements, layoutManager);
        }


    }

    private void displayPartsInnerLevel(Collection<? extends Element> selectedElements, LayoutManager layoutManager) {
        selectedElements.stream()
                .filter(Profile._getSysmlAdditionalStereotypes().partProperty()::is)
                .map(Property.class::cast)
                .filter(property -> property.getType() != null && property.getType() instanceof Class)
                .map(property -> (Class) property.getType())
                .forEach(owner -> displayInnerLevel(owner, layoutManager));

    }

    private void displayDiagramFirstLevel(LayoutManager layoutManager) {
        Diagram diagram = OMFUtils.getProject().getActiveDiagram().getDiagram();
        displayInnerLevel((Class) diagram.getOwner(), layoutManager);

    }

    private void displayInnerLevel(Class owner, LayoutManager layoutManager) {
        Set<Property> micParts = owner.getOwnedAttribute().stream()
                .filter(Profile._getSysmlAdditionalStereotypes().partProperty()::is)
                .collect(Collectors.toSet());
        List<PresentationElement> portsPresentationElements = new ArrayList<>();

        for (Property micPart : micParts) {
            layoutManager.refreshPart(micPart);
            layoutManager.refreshAllPorts((Class) micPart.getType(), micPart); //TODO: check if it has a type

            micPart.getType().getOwnedElement()
                    .stream()
                    .filter(Port.class::isInstance)
                    .map(Port.class::cast)
                    .forEach(port -> layoutManager.displayAllNestedPortRecursively(port));
        }
        layoutManager.displayAllPaths(layoutManager.getAllDisplayedElements());
    }


}
