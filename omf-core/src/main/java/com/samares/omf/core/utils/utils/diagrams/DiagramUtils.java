package com.samares.omf.core.utils.utils.diagrams;

import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.samares.omf.core.utils.OMFUtils;

import java.util.Objects;

public class DiagramUtils {
    private DiagramUtils() {}

    public static Diagram getOpenedDiagram() {
        DiagramPresentationElement diagramPresentationElement = Objects.requireNonNull(OMFUtils.currentProject, "Can't " +
                        "get open diagram as project is null")
                .getActiveDiagram();

        return Objects.requireNonNull(diagramPresentationElement, "Can't get open diagram as" +
                        " diagramPresentationElement is null")
                .getDiagram();
    }

    public static DiagramPresentationElement getDiagram(Diagram diagram) {
        return Objects.requireNonNull(OMFUtils.currentProject, "Can't get diagram as project is null")
                .getDiagram(diagram);
    }
}
