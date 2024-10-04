
package com.samares_engineering.omf.omf_example_plugin.features.groupfeature.actions;

import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.magicdraw.uml.symbols.PresentationElement;
import com.nomagic.magicdraw.uml.symbols.shapes.PartView;
import com.nomagic.ui.ProgressStatusRunner;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.nomagic.uml2.ext.magicdraw.mdprofiles.Stereotype;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.clone.ElementGetter;
import com.samares_engineering.omf.omf_core_framework.utils.group.IBDExtractor;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.InternalDiagramManagement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@DiagramAction
@DeactivateListener
@MDAction(actionName = "Group Parts", category = "OMF.Group")
public class GroupPartsAction extends AUIAction {

    private ElementGetter elementGetter = new ElementGetter(); //Helper class to retrieve elements from others

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if (selectedElements.size() < 2) return false;
        Element firstPart = selectedElements.get(0);
        boolean portOwnerIsABlock = Profile._getSysml().block().is(firstPart.getOwner());
        return portOwnerIsABlock && selectedElements.stream()
                .allMatch(Profile._getSysmlAdditionalStereotypes().partProperty()::is)
                && firstPart.getOwner().getOwnedElement().containsAll(selectedElements);
    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        List<Property> selectedMICPorts = selectedElements.stream().map(Property.class::cast)
                .collect(Collectors.toList());
        ProgressStatusRunner.runWithProgressStatus(progressStatus -> groupSelectedPorts(selectedMICPorts),
                "Grouping Ports in progress", false, 0);
    }



    protected void groupSelectedPorts(List<Property> selectedParts) {
        DiagramPresentationElement diagram = OMFUtils.getProject().getActiveDiagram();
        Property firstSelectedMICPart =  selectedParts.get(0);

        Element newBlockOwner = firstSelectedMICPart.getType().getOwner();

        List<PresentationElement> listPartViews = selectedParts.stream()
                .map(this::findPresentationElement)
                .collect(Collectors.toList());

        Class newBlock = SysMLFactory.getInstance().createBlock(newBlockOwner);
        newBlock.setName("[GROUPED_BLOCK]");

        Stereotype partPropertySTR = Profile._getSysmlAdditionalStereotypes().partProperty().getStereotype();
        new IBDExtractor().extractParts(listPartViews, newBlock, partPropertySTR);
    }

    private PresentationElement findPresentationElement(Property property) {
        DiagramPresentationElement diagram = OMFUtils.getProject().getActiveDiagram();
        return diagram.findPresentationElement(property, PartView.class);
    }


    /**
     * Refresh all the diagram representation elements:
     * - Deleting the previous representation elements
     * - Displaying the newport, and all the nested ports
     * - Displaying all the connectors (including the nested ones)
     * @param selectedPorts the ports to group
     * @param allConnectorsFromPorts the connectors to update
     * @param partHost the part host
     * @param newPort the new port
     */
    private void refreshAllDiagramPresentationElements(List<Port> selectedPorts, List<Connector> allConnectorsFromPorts,
                                                       PresentationElement partHost, Port newPort) {
        DiagramPresentationElement activeDiagram = getDiagram();

        //Deleting the previous representation elements
        deletePreviousRepresentationElements(selectedPorts, allConnectorsFromPorts);

        //Displaying the newport, and all the nested ports
        List<PresentationElement> portsPEList = new ArrayList<>();
        portsPEList.addAll(InternalDiagramManagement.refreshSinglePort(newPort, (Property) partHost.getElement(), activeDiagram.getDiagram()));
        portsPEList.addAll(InternalDiagramManagement.displayAllNestedPortRecursively(newPort, activeDiagram));

        //Displaying all the connectors (including the nested ones)
        InternalDiagramManagement.displayPath(portsPEList);

    }

    /**
     * Delete the old representation elements:
     * - The selected and grouped ports
     * - The connectors connected to them
     * @param portsToDelete the ports to delete
     * @param listConnectors the connectors to delete
     */
    private void deletePreviousRepresentationElements(List<Port> portsToDelete, List<Connector> listConnectors) {
        DiagramPresentationElement diagramPE = getDiagram();
        listConnectors.forEach(connector -> InternalDiagramManagement.deleteRepresentationElement(connector, diagramPE));
        portsToDelete.forEach(port -> InternalDiagramManagement.deleteRepresentationElement(port, diagramPE));
    }


}
