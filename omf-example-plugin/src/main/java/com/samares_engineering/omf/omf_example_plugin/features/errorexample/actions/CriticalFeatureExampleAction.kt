/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*

@BrowserAction
@DiagramAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "CRITICAL ERROR", category = "Example.Error")
class CriticalFeatureExampleAction : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element?>?): Boolean {
        return true
    }

    override fun actionToPerform(selectedElements: List<Element?>?) {
        throw OMFCriticalException(
                "TESTING Framework CRITICAL FEATURE ERROR",
                OMFExceptionModifier.NO_ROLLBACK,
                OMFExceptionModifier.SILENT,
                OMFExceptionModifier.DEACTIVATE_FEATURE
        )
    }

    override fun executeDiagramAction(selectedElements: List<Element?>?) {
        super.executeDiagramAction(selectedElements)
    }
}