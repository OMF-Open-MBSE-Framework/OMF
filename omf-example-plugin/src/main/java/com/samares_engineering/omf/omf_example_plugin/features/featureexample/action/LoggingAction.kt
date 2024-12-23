/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.featureexample.action

import com.nomagic.magicdraw.core.Application
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFColors
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Logging example", category = "OMF")
class LoggingAction : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return isProjectOpened && selectedElements.isNotEmpty()
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        val log = OMFLog("Logging example")
            .color("red log", OMFColors.RED)
            .color("green log", OMFColors.GREEN)
            .info("This is an info log")
            .warn("This is a warning log")
            .err("This is an error log")
            .expandText(OMFLog("This is an expandable text"))
            .expandText(OMFLog("This is an expandable text with a red color").color("red", OMFColors.RED))
            .linkElement("This is a link to an element", selectedElements[0])
            .info(OMFLog("1"))

        OMFLogger2.toAll().log(log)

    }
}