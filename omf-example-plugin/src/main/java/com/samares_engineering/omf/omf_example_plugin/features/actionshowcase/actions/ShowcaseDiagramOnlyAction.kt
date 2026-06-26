package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.magicdraw.uml.symbols.PresentationElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction

/**
 * Demonstrates: @DiagramAction only — never appears in the browser or main menu.
 *
 * Also demonstrates accessing PresentationElement (PE) data: the diagram-side representation
 * of a model element. A PE wraps the visual shape and exposes:
 *   - pe.element               : the underlying UML model element
 *   - pe.bounds                : java.awt.Rectangle with x, y, width, height on the diagram canvas
 *   - pe.diagramPresentationElement : the diagram this PE lives in
 *   - pe.element?.humanName    : the human-readable name of the underlying model element
 *
 * One UML element can have multiple PEs (same element shown in multiple diagrams, or multiple
 * times in the same diagram). The PE list here reflects what is visually selected on the canvas.
 *
 * Base MagicDraw API note:
 *   DiagramContextAMConfigurator.configure(ActionsManager, DiagramPE owner, PE[] selected, PE requestor)
 *   passes a "requestor" — the specific PE that was right-clicked — separately from the full
 *   selection. OMF discards the requestor. To distinguish "which element was right-clicked" from
 *   "what is selected", subclass OMFDiagramConfigurator and store the requestor in a ThreadLocal.
 */
@DiagramAction
@MDAction(actionName = "Showcase: Diagram Only (PE Info)", category = "OMFShowcase")
class ShowcaseDiagramOnlyAction : ElementUIAction() {

    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        if (isProjectVoid) return false
        return project.activeDiagram != null
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        val pes: List<PresentationElement> = diagramSelectedPresentationElements
        val log = OMFLog()
            .bold("Diagram-Only Action")
            .text(" — PresentationElement info for ${pes.size} selected shape(s):")

        pes.forEach { pe ->
            val bounds = pe.bounds
            val elementName = pe.element?.humanName ?: "no element"
            log.text("\n  PE type: ").bold(pe.javaClass.simpleName)
                .text(" | element=")
                .linkElement(elementName, pe.element)
                .text(" | bounds=(${bounds?.x},${bounds?.y} ${bounds?.width}×${bounds?.height})")
        }

        if (pes.isEmpty()) {
            log.text("\n  (nothing selected on the diagram)")
        }

        OMFLogger2.toAll().log(log)
    }
}
