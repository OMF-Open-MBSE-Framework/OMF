package com.samares_engineering.omf.omf_example_plugin.features.display.actions

import com.nomagic.magicdraw.sysml.util.SysMLConstants
import com.nomagic.magicdraw.ui.actions.DefaultDiagramAction
import com.nomagic.magicdraw.uml.symbols.PresentationElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.LayoutManager
import java.util.stream.Collectors

@DiagramAction
@DeactivateListener
@MDAction(actionName = "Display Inner layer", category = "")
class DisplayInnerLayer : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        if (OMFUtils.isProjectVoid()) return false
        val activeDiagram = OMFUtils.getProject().activeDiagram ?: return false
        val diagramType = activeDiagram.diagramType.type
        return diagramType == SysMLConstants.SYSML_INTERNAL_BLOCK_DIAGRAM
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        val layoutManager = LayoutManager(
                (diagramAction as DefaultDiagramAction).diagram
        )
        if (selectedElements.isEmpty()) {
            displayDiagramFirstLevel(layoutManager)
        } else {
            displayPartsInnerLevel(selectedElements, layoutManager)
        }
    }

    private fun displayPartsInnerLevel(selectedElements: Collection<Element>, layoutManager: LayoutManager) {
        selectedElements.stream()
            .filter { element: Element? -> Profile._getSysmlAdditionalStereotypes().partProperty().`is`(element) }
            .map { obj: Any? -> Property::class.java.cast(obj) }
            .filter { property: Property -> property.type != null && property.type is Class }
            .map { property: Property -> property.type as Class? }
            .forEach { owner: Class? -> displayInnerLevel(owner, layoutManager) }
    }

    private fun displayDiagramFirstLevel(layoutManager: LayoutManager) {
        val diagram = OMFUtils.getProject().activeDiagram!!
            .diagram
        displayInnerLevel(diagram.owner as Class?, layoutManager)
    }

    private fun displayInnerLevel(owner: Class?, layoutManager: LayoutManager) {
        val micParts = owner!!.ownedAttribute.stream()
            .filter { element: Property? -> Profile._getSysmlAdditionalStereotypes().partProperty().`is`(element) }
            .collect(Collectors.toSet())
        val portsPresentationElements: List<PresentationElement> = ArrayList()

        for (micPart in micParts) {
            layoutManager.refreshPart(micPart)
            layoutManager.refreshAllPorts(micPart.type as Class?, micPart) //TODO: check if it has a type

            micPart.type!!.ownedElement
                .stream()
                .filter { obj: Element? -> Port::class.java.isInstance(obj) }
                .map { obj: Element? -> Port::class.java.cast(obj) }
                .forEach { port: Port? -> layoutManager.displayAllNestedPortRecursively(port) }
        }
        layoutManager.displayAllPaths(layoutManager.allDisplayedElements)
    }
}
