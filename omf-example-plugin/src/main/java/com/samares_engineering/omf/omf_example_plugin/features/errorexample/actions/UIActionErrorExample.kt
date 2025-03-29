/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFExceptionModifier
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
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
        } catch (e: OMFCriticalException) {
            OMFLogger.errorToUIConsole(OMFLog().err("Something went wrong, the session will be rolled back."))
            OMFLogger.errorToNotification(OMFLog().err("Something went wrong, the session will be rolled back."))
        }
    }

    override fun executeDiagramAction(selectedElements: List<Element>) {
        super.executeDiagramAction(selectedElements)
    }


    companion object {
        private fun createBlock(selectedElements: List<Element>) {
            val block = SysMLFactory.getInstance().createBlock(selectedElements[0].owner)
            block.name = "SHALL BE ROLLED BACKED"
            throw OMFCriticalException(
                    OMFLog()
                            .bold("TESTING Framework ERROR")
                            .text("By throwing this exception, the framework will rollback the session and display a message to the user.\n")
                            .italic("Modifiers can be added to prevent rollback, or to make this exception silent.")
                            .linkElement("the block", block), OMFExceptionModifier.DEACTIVATE_FEATURE
            )
        }
    }

    /**
     * This method is used to demonstrate the rollback of the session when an exception is thrown.
     * It creates a port with a type that does not exist, which will throw an exception.
     * @param element The element to which the port will be added.
     * @return The element passed as parameter.
     */
    fun doStuff(element: Class):Element {
        try {
            OMFBarrierExecutor.executeWithinBarrier {
                // Do stuff here
                val port = SysMLFactory.getInstance().createProxyPort(element)
                port.name = "Port"
                port.type!!.name = "PortType" //PORT TYPE DOES NOT EXIST --> THROWS EXCEPTION
            }
        }catch (e: OMFCriticalException) {
            OMFLogger2.toAll().error("Something went wrong, the session has been rolled back, preventing the creation of the element.")
        }

        return element
    }
}