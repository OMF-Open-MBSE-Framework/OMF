package com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.options

import com.nomagic.magicdraw.properties.BooleanProperty
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OMFFeatureRegisteringException
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind

/**
 * This class is used to manage the options of the SysML Basic feature
 * It declares the options and their default values, in the Group "OMF Features":
 * - [OMF FEATURES] - Activate auto InterfaceBlock creation when port created
 * And all getters and setters to manage these options:
 * - isAutoInterfaceCreationActivated()
 */
class SysMLBasicOptionHelper(feature: MDFeature?) : EnvOptionsHelper(feature) {
    private val GROUP = "SysML Basics Features"

    val allOptions: List<IOption>
        get() {
            val isInterfaceCreationActivated = BooleanProperty(ACTIVATE_AUTO_INTERFACE_BLOCK_CREATION, false)
            val isInterfaceCreationActivatedOption = OptionImpl(
                isInterfaceCreationActivated,
                GROUP,
                feature.plugin.environmentOptionsGroup
                    .orElseThrow {
                        OMFFeatureRegisteringException(
                            "Environment options group not registered" +
                                    "for plugin"
                        )
                    },
                OptionKind.Environment
            )

            val isNameLivePropagationActivated = BooleanProperty(ACTIVATE_LIVE_INTERFACE_NAME_PROPAGATION, false)
            val isNameLivePropagationActivatedOption = OptionImpl(
                isNameLivePropagationActivated,
                GROUP,
                feature.plugin.environmentOptionsGroup
                    .orElseThrow {
                        OMFFeatureRegisteringException(
                            "Environment options group not registered" +
                                    "for plugin"
                        )
                    },
                OptionKind.Environment
            )
            return java.util.List.of<IOption>(
                isInterfaceCreationActivatedOption,
                isNameLivePropagationActivatedOption
            )
        }

    var isAutoInterfaceCreationActivated: Boolean
        get() = getPropertyByName(ACTIVATE_AUTO_INTERFACE_BLOCK_CREATION).value as Boolean
        set(value) {
            getPropertyByName(ACTIVATE_AUTO_INTERFACE_BLOCK_CREATION).value = value
        }
    val isLiveNamePropagationActivated: Boolean
        get() = getPropertyByName(ACTIVATE_LIVE_INTERFACE_NAME_PROPAGATION).value as Boolean

    companion object {
        const val ACTIVATE_AUTO_INTERFACE_BLOCK_CREATION: String =
            "Activate auto InterfaceBlock creation when port created :"
        const val ACTIVATE_LIVE_INTERFACE_NAME_PROPAGATION: String =
            "Activate live name propagation within ports/interface/flowProperty :"
    }
}
