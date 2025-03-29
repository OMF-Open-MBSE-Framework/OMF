package com.samares_engineering.omf.omf_example_plugin.features.exportdiagram.options

import com.nomagic.magicdraw.properties.StringProperty
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind
import java.io.File

/**
 * This class is used to manage the options of the ExportDiagram feature.
 * It declares the options and their default values, in the Group "OMF Features":
 *
 */
class ExportDiagramOptionHelper(feature: OMFFeature?) : EnvOptionsHelper(feature) {
    private val GROUP = "Export Diagram"

    var userHome: String = System.getProperty("user.home")
    var desktopDir: File = File(userHome, "Desktop")
    var picturesDir: File = File(userHome, "Pictures")

    val allOptions: List<Option>
        get() {
            val exportDiagramPath = StringProperty(EXPORT_DIAGRAM_PATH, picturesDir.absolutePath)
            val isInterfaceCreationActivatedOption = OptionImpl(
                exportDiagramPath,
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

    var pathForDiagramExport: String
        get() = getPropertyByName(EXPORT_DIAGRAM_PATH).value as String
        set(value) {
            getPropertyByName(EXPORT_DIAGRAM_PATH).value = value
        }


    companion object {
        const val EXPORT_DIAGRAM_PATH: String =
            "Path where the diagram will be exported: "
    }
}
