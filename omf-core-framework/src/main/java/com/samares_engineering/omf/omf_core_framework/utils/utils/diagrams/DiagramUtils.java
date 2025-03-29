/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams;

import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.Objects;

public class DiagramUtils {
    private DiagramUtils() {}

    public static Diagram getOpenedDiagram() {
        DiagramPresentationElement diagramPresentationElement = Objects.requireNonNull(OMFUtils.getProject(), "Can't " +
                        "get open diagram as project is null")
                .getActiveDiagram();

        return Objects.requireNonNull(diagramPresentationElement, "Can't get open diagram as" +
                        " diagramPresentationElement is null")
                .getDiagram();
    }

    public static DiagramPresentationElement getDiagram(Diagram diagram) {
        return Objects.requireNonNull(OMFUtils.getProject(), "Can't get diagram as project is null")
                .getDiagram(diagram);
    }
}
