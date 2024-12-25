/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.featureexample.action

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction

@MenuAction
@DeactivateListener
@MDAction(actionName = "Notification cluster bomb", category = "OMF")
class NotificationClusterBomb : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return true
    }

    override fun actionToPerform(selectedElements: List<Element>) {

        // New thread to avoid blocking the UI
        Thread {
            for (i in 0..100) {
                OMFLogger2.toNotification().log(OMFLog("BOOM ! Testing notification throttling x$i"))
                // Sleep for 0.1 seconds
                Thread.sleep(100)
            }
        }.start()
    }
}