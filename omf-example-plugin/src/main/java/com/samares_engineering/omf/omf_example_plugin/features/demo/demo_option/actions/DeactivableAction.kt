/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MenuAction
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import com.samares_engineering.omf.omf_example_plugin.features.demo.demo_option.OptionsDemoFeature

@MenuAction
@DiagramAction
@BrowserAction
@MDAction(actionName = "Demo Action", category = "DEMO")
class DeactivableAction : AUIAction() {

    //AVAILABLE ONLY WHEN OPTION IS CHECKED
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return isProjectOpened && getFeature().envOptionsHelper.isDemoUIACtionAvailable
    }


    override fun actionToPerform(selectedElements: List<Element>) {
        OMFLogger2.toNotification().success("Demo Action performed")
    }

    override fun getFeature(): OptionsDemoFeature {
        return super.getFeature() as OptionsDemoFeature
    }
}
