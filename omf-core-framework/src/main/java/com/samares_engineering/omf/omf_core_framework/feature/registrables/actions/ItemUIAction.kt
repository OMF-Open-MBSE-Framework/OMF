package com.samares_engineering.omf.omf_core_framework.feature.registrables.actions

import com.nomagic.magicdraw.ui.browser.Node
import com.nomagic.magicdraw.uml.symbols.PresentationElement
import com.nomagic.magicdraw.uml2.Elements
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element

import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

abstract class V2ElementUIAction: AUIAction<Element, PresentationElement>() {
    /**
     * Get the selected Elements inside the Containment Tree.
     * Hypothesis: Order correspond to the user Element selection one.
     *
     * @return selected elements list.
     */
    override fun getSelectedBrowserElements(): List<Element> {
        if (getSelectedBrowserNodes().isEmpty()) return emptyList()
        return getSelectedBrowserNodes()
            .mapNotNull { it.userObject }
            .filterIsInstance<Element>()
            .toList()
    }

    /**
     * Get the Presentation elements of the selected elements inside the active diagram.
     * Hypothesis: Order correspond to the user Element selection one.
     *
     * @return selected Presentation Element list.
     */
    override fun getSelectedDiagramPresentationElements(): List<PresentationElement> {
        if (isProjectVoid) return emptyList()
        return emptyList()
    }

    /**
     * Get the selected Elements inside the active diagram.
     * Hypothesis: Order correspond to the user Element selection one.
     *
     * @return selected elements list.
     */
    override fun getSelectedDiagramElements(): List<Element> {
        return emptyList()
    }

}