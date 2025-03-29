package com.samares_engineering.omf.omf_example_plugin.features.connection.options

import com.nomagic.magicdraw.properties.BooleanProperty
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind

/**
 * This class is used to manage the options of the Connection feature.
 * It declares the options and their default values, in the Group "OMF Features":
 *
 */
class ConnectionOptionHelper(feature: OMFFeature?) : EnvOptionsHelper(feature) {
    private val GROUP = "Connection automation"

    val allOptions: List<Option>
        get() {
            val isAutoDelegationOnConnectionActivated = BooleanProperty(ACTIVATE_AUTO_DELEGATION_ON_CONNECTION, true)
            val isInterfaceCreationActivatedOption = OptionImpl(
                isAutoDelegationOnConnectionActivated,
                GROUP,
                feature.plugin.environmentOptionsGroup
                    .orElseThrow {
                        FeatureRegisteringException(
                                "Environment options group not registered" +
                                        "for plugin"
                        )
                    },
                OptionKind.Environment
            )


            return java.util.List.of<Option>(
                isInterfaceCreationActivatedOption
            )
        }

    var isAutoDelegationOnCreationActivated: Boolean
        get() = getPropertyByName(ACTIVATE_AUTO_DELEGATION_ON_CONNECTION).value as Boolean
        set(value) {
            getPropertyByName(ACTIVATE_AUTO_DELEGATION_ON_CONNECTION).value = value
        }


    companion object {
        const val ACTIVATE_AUTO_DELEGATION_ON_CONNECTION: String =
            "Activate automatic Delegation on Connector creation (port+interface+connectors for each layer):"
    }
}
