/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFExceptionModifier2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog2
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*

@BrowserAction
@DiagramAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "ERROR Logging", category = "Example.Error")
class UIActionErrorExample : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return true
    }


    override fun actionToPerform(selectedElements: List<Element>) {
        try {
            createBlock(selectedElements)
        } catch (e: OMFCriticalException2) {
            OMFLogger2.errorToUIConsole(OMFLog2().err("Something went wrong, the session will be rolled back."))
            OMFLogger2.errorToNotification(OMFLog2().err("Something went wrong, the session will be rolled back."))
        }
    }

    override fun executeDiagramAction(selectedElements: List<Element>) {
        super.executeDiagramAction(selectedElements)
    }


    companion object {
        private fun createBlock(selectedElements: List<Element>) {
            val block = SysMLFactory.getInstance().createBlock(selectedElements[0].owner)
            block.name = "SHALL BE ROLLED BACKED"
            throw OMFCriticalException2(
                OMFLog2()
                    .bold("TESTING Framework ERROR")
                    .text("By throwing this exception, the framework will rollback the session and display a message to the user.\n")
                    .italic("Modifiers can be added to prevent rollback, or to make this exception silent.")
                    .linkElement("the block", block), OMFExceptionModifier2.DEACTIVATE_FEATURE
            )
        }
    }
}