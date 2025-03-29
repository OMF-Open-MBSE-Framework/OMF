/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.errorexample

import com.nomagic.magicdraw.properties.BooleanProperty
import com.nomagic.magicdraw.properties.Property
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option
import com.samares_engineering.omf.omf_example_plugin.features.errorexample.actions.*
import com.samares_engineering.omf.omf_example_plugin.features.errorexample.creation.LiveActionErrorExample
import java.beans.PropertyChangeEvent
import java.util.*

class ErrorManagementFeatureExample : SimpleFeature("ERROR MANAGEMENT TEST FEATURE") {
    public override fun initFeatureActions(): List<UIAction> {
        return Arrays.asList<UIAction>(
            CriticalFeatureExampleAction(),
            ErrorInAvailabilityCheck(),
            UIActionErrorExample(),
            HandlingErrorUIAction(),
            NotHandledException(),
            ShieldedErrorUIAction()
        )
    }

    public override fun initLiveActions(): List<LiveActionEngine<*>> {
        val creationRE = ALiveActionEngine(LiveActionType.CREATE)
        creationRE.addLiveAction(LiveActionErrorExample())
        return java.util.List.of(creationRE)
    }


    public override fun initOptions(): List<Option> {
        ACTIVATE_ERROR_LIVE_ACTION = "[TEST ERROR] Activate Live Action:"
        OMF_ERROR_EXAMPLE = "OMF Errors Example"
        val testEnvOption = createEnvOption(
            BooleanProperty(ACTIVATE_ERROR_LIVE_ACTION, false),
            OMF_ERROR_EXAMPLE
        )


        testEnvOption.addListenerToRegister(object : AOptionListener() {
            override fun updateByEnvironmentProperties(list: List<Property>) {
                super.updateByEnvironmentProperties(list)
            }

            override fun propertyChange(evt: PropertyChangeEvent) {
                super.propertyChange(evt)
            }
        })

        return java.util.List.of<Option>(
            testEnvOption
        )
    }


    companion object {
        @JvmField
        var ACTIVATE_ERROR_LIVE_ACTION: String? = null
        var OMF_ERROR_EXAMPLE: String? = null
    }
}
