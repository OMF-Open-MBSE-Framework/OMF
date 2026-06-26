package com.samares_engineering.omf.omf_example_plugin.features.actionshowcase.actions

import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.ElementUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import java.awt.event.ActionEvent

/**
 * Demonstrates: overriding initDiagramActions() to access DefaultDiagramAction internals.
 *
 * What DefaultDiagramAction provides beyond the OMF wrapper:
 *   - getDiagram()     : the DiagramPresentationElement the right-click occurred in.
 *                        OMF exposes this via AUIAction.diagram (same thing).
 *   - diagramType      : diagram.diagramType.type gives the diagram kind string
 *                        (e.g. SysMLConstants.SYSML_INTERNAL_BLOCK_DIAGRAM)
 *
 * THE REQUESTOR GAP (documented here because it cannot be demonstrated without framework changes):
 *   DiagramContextAMConfigurator.configure(ActionsManager am,
 *                                          DiagramPresentationElement owner,
 *                                          PresentationElement[] selected,
 *                                          PresentationElement requestor)
 *   The `requestor` parameter is the specific PE that was right-clicked (as opposed to `selected`
 *   which is the full current selection). This is useful when the right-clicked element is not
 *   in the selection (e.g., user right-clicks on a non-selected shape).
 *
 *   OMFDiagramConfigurator.configure() DISCARDS the requestor — it is never passed to actions.
 *   To access the requestor, subclass OMFDiagramConfigurator, override configure(), store the
 *   requestor in a ThreadLocal<PresentationElement>, then read it inside the action.
 *
 * This example instead demonstrates the diagram and type info accessible via DefaultDiagramAction.
 */
@DiagramAction
@MDAction(actionName = "Showcase: Diagram Context (DefaultDiagramAction)", category = "OMFShowcase")
class ShowcaseDiagramRequestorAction : ElementUIAction() {

    /** Stores the diagram captured from the NMAction at execution time. */
    private var capturedDiagram: DiagramPresentationElement? = null

    override fun initDiagramActions() {
        diagramNMAction = object : DefaultDiagramAction("", getName(), keyStroke, null) {

            override fun actionPerformed(actionEvent: ActionEvent?) {
                if (OMFUtils.isProjectVoid() || OMFUtils.getProject().activeDiagram == null) return
                if (!checkDiagramAvailability()) return

                super.actionPerformed(actionEvent)
                init()

                // DefaultDiagramAction.getDiagram() returns the diagram that was right-clicked.
                capturedDiagram = getDiagram() ?: OMFUtils.getActiveDiagram()
                // Store back via the DiagramAction interface (same as AUIAction does)
                setDiagram(capturedDiagram)

                executeDiagramAction(diagramSelectedElements)
                OMFAutomationManager.getInstance().automationTriggered()
            }

            override fun getDiagram(): DiagramPresentationElement? =
                super.getDiagram() ?: OMFUtils.getActiveDiagram()

            override fun updateState() {
                super.updateState()
                if (OMFUtils.isProjectVoid() || OMFUtils.getProject().activeDiagram == null) {
                    isEnabled = false
                    return
                }
                isEnabled = checkDiagramAvailability()
            }
        }
    }

    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        if (isProjectVoid) return false
        return project.activeDiagram != null
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        val diagramPE = capturedDiagram ?: diagram
        val diagramType = diagramPE.diagramType?.type ?: "unknown"
        val diagramName = diagramPE.diagram?.name ?: "unnamed"

        OMFLogger2.toAll().log(
            OMFLog()
                .bold("Diagram Context Action")
                .text("\n  Diagram name: ").bold(diagramName)
                .text("\n  Diagram type: ").bold(diagramType)
                .text("\n  Selected shapes: ").bold(selectedElements.size.toString())
                .text("\n  NOTE: The 'requestor' PE (right-clicked element vs selection) is")
                .text(" not accessible via OMF. See class-level KDoc for the workaround.")
        )
    }
}
