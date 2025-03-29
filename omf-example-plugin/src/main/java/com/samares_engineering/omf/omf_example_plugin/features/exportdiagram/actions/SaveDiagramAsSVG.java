package com.samares_engineering.omf.omf_example_plugin.features.exportdiagram.actions;

import com.nomagic.magicdraw.export.image.ImageExporter;
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_example_plugin.features.exportdiagram.ExportDiagramImagesFeature;

import java.io.File;
import java.io.IOException;
import java.util.List;

@DiagramAction
@DeactivateListener
@MDAction(actionName = "Save Diagram as SVG", category = "OMF EXAMPLE")
public class SaveDiagramAsSVG extends AUIAction {

    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        if (OMFUtils.isProjectVoid()) return false;
        DiagramPresentationElement activeDiagram = OMFUtils.getProject().getActiveDiagram();
        return activeDiagram != null;

    }

    @Override
    public void actionToPerform(List<Element> selectedElements) {
        exportDiagram(OMFUtils.getActiveDiagram());
    }

    private void exportDiagram(DiagramPresentationElement diagram) {
        ExportDiagramImagesFeature feature = (ExportDiagramImagesFeature) getFeature();
        final File diagramFile = new File(feature.getEnvOptionsHelper().getPathForDiagramExport(), diagram.getHumanName() + diagram.getID() + ".svg");
        try
        {
            ImageExporter.export(diagram, ImageExporter.SVG, diagramFile);
        }
        catch (IOException e)
        {
            OMFLogger.warn(e);
        }

    }


}
