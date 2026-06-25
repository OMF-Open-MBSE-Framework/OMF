package com.samares_engineering.omf.omf_example_plugin.features.optionsexample.options

import com.nomagic.magicdraw.core.options.ProjectOptions
import com.nomagic.magicdraw.properties.*
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Package
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind
import com.samares_engineering.omf.omf_core_framework.utils.options.OptionsHelper
import java.awt.Color
import java.awt.Font

/**
 * Demonstrates every publicly available MagicDraw property type as both an
 * environment option (global, Options > Environment) and a project option
 * (per-project, Options > Project).
 *
 * Notes:
 * - Environment options are retrieved via [getPropertyByName]: the property is added to the
 *   plugin's OMFPropertyOptionsGroup during registration.
 * - Project options must be retrieved via [OptionsHelper.getProjectOptionByCategoryName]:
 *   they are stored in ProjectOptions.PROJECT_GENERAL_PROPERTIES, not in the env group.
 * - [ElementProperty.setSelectableTypes] / [ElementProperty.setDisplayableTypes] implement
 *   the @InternalApi ElementFilterProperty interface, but are public on the concrete classes
 *   and functional in 2026x.
 */
class OptionsExampleOptionHelper(feature: OMFFeature?) : EnvOptionsHelper(feature) {

    private val group
        get() = feature.plugin.environmentOptionsGroup
            .orElseThrow { FeatureRegisteringException("Environment options group not registered for plugin") }

    // -------------------------------------------------------------------------
    // Option declarations
    // -------------------------------------------------------------------------

    val envOptions: List<Option>
        get() {
            val g = group
            return listOf(
                // Boolean — yes/no checkbox
                OptionImpl(BooleanProperty(ENV_BOOLEAN, false), ENV_GROUP, g, OptionKind.Environment),

                // String — text area (multiline=true is the practical default for options dialogs)
                OptionImpl(StringProperty(ENV_STRING, "", true), ENV_GROUP, g, OptionKind.Environment),

                // Number (integer) — input box with integer format validation
                OptionImpl(NumberProperty(ENV_INTEGER, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), ENV_GROUP, g, OptionKind.Environment),

                // Number (double) — input box with double format validation
                OptionImpl(NumberProperty(ENV_DOUBLE, 0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), ENV_GROUP, g, OptionKind.Environment),

                // Number (float) — input box with float format validation
                OptionImpl(NumberProperty(ENV_FLOAT, 0f, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), ENV_GROUP, g, OptionKind.Environment),

                // Number (long) — input box with long format validation
                OptionImpl(NumberProperty(ENV_LONG, 0L, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), ENV_GROUP, g, OptionKind.Environment),

                // Color — RGB color picker
                OptionImpl(ColorProperty(ENV_COLOR, Color.BLACK), ENV_GROUP, g, OptionKind.Environment),

                // Font — font name + style + size picker
                OptionImpl(FontProperty(ENV_FONT, Font("Dialog", Font.PLAIN, 12)), ENV_GROUP, g, OptionKind.Environment),

                // File (any type) — single file browser, no extension filter
                OptionImpl(FileProperty(ENV_FILE, ""), ENV_GROUP, g, OptionKind.Environment),

                // File (extension-filtered) — single file browser restricted to XML/XMI files
                OptionImpl(
                    FileProperty(ENV_FILE_XML, "").also { it.fileExtensions = listOf("xml", "xmi") },
                    ENV_GROUP, g, OptionKind.Environment
                ),

                // Directory — file-system browser for folders only
                OptionImpl(FileProperty(ENV_DIRECTORY, "", FileProperty.DIRECTORIES_ONLY), ENV_GROUP, g, OptionKind.Environment),

                // Choice — single-value dropdown from a fixed list
                OptionImpl(ChoiceProperty(ENV_CHOICE, CHOICE_A, listOf(CHOICE_A, CHOICE_B, CHOICE_C)), ENV_GROUP, g, OptionKind.Environment),

                // Password — masked text field (MagicDraw handles encryption at rest)
                OptionImpl(PasswordProperty(ENV_PASSWORD, ""), ENV_GROUP, g, OptionKind.Environment),
            )
        }

    val projectOptions: List<Option>
        get() {
            val g = group
            return listOf(
                // Boolean
                OptionImpl(BooleanProperty(PROJ_BOOLEAN, false), PROJ_GROUP, g, OptionKind.Project),

                // String (multiline)
                OptionImpl(StringProperty(PROJ_STRING, "", true), PROJ_GROUP, g, OptionKind.Project),

                // Number (integer) — input box with integer format validation
                OptionImpl(NumberProperty(PROJ_INTEGER, 0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY), PROJ_GROUP, g, OptionKind.Project),

                // Element (unrestricted) — any model element can be selected
                OptionImpl(ElementProperty(PROJ_ELEMENT, null), PROJ_GROUP, g, OptionKind.Project),

                // Element (type-filtered) — only Package elements are selectable/displayable
                OptionImpl(
                    ElementProperty(PROJ_ELEMENT_PACKAGE_ONLY, null).also {
                        it.setSelectableTypes(listOf(Package::class.java))
                        it.setDisplayableTypes(listOf(Package::class.java))
                    },
                    PROJ_GROUP, g, OptionKind.Project
                ),

                // ElementList (unrestricted) — any model elements can be selected
                OptionImpl(ElementListProperty(PROJ_ELEMENT_LIST, null), PROJ_GROUP, g, OptionKind.Project),

                // ElementList (type-filtered) — displayable tree shows Packages and Classes,
                // but only Classes are selectable
                OptionImpl(
                    ElementListProperty(PROJ_ELEMENT_LIST_TYPED, null).also {
                        it.setDisplayableTypes(listOf(Package::class.java, Class::class.java))
                        it.setSelectableTypes(listOf(Class::class.java))
                    },
                    PROJ_GROUP, g, OptionKind.Project
                ),

                // Choice — single-value dropdown
                OptionImpl(ChoiceProperty(PROJ_CHOICE, PROJ_CHOICE_1, listOf(PROJ_CHOICE_1, PROJ_CHOICE_2, PROJ_CHOICE_3)), PROJ_GROUP, g, OptionKind.Project),

                // Color
                OptionImpl(ColorProperty(PROJ_COLOR, null), PROJ_GROUP, g, OptionKind.Project),
            )
        }

    val allOptions: List<Option> get() = envOptions + projectOptions

    // -------------------------------------------------------------------------
    // Environment option accessors
    // Properties are registered in the OMFPropertyOptionsGroup → getPropertyByName works.
    // -------------------------------------------------------------------------

    var envBoolean: Boolean
        get() = getPropertyByName(ENV_BOOLEAN).value as Boolean
        set(value) { getPropertyByName(ENV_BOOLEAN).value = value }

    var envString: String
        get() = getPropertyByName(ENV_STRING).value as? String ?: ""
        set(value) { getPropertyByName(ENV_STRING).value = value }

    var envInteger: Int
        get() = getPropertyByName(ENV_INTEGER).value as? Int ?: 0
        set(value) { getPropertyByName(ENV_INTEGER).value = value }

    var envDouble: Double
        get() = getPropertyByName(ENV_DOUBLE).value as? Double ?: 0.0
        set(value) { getPropertyByName(ENV_DOUBLE).value = value }

    var envFloat: Float
        get() = getPropertyByName(ENV_FLOAT).value as? Float ?: 0f
        set(value) { getPropertyByName(ENV_FLOAT).value = value }

    var envLong: Long
        get() = getPropertyByName(ENV_LONG).value as? Long ?: 0L
        set(value) { getPropertyByName(ENV_LONG).value = value }

    var envColor: Color?
        get() = (getPropertyByName(ENV_COLOR) as? ColorProperty)?.color
        set(value) { getPropertyByName(ENV_COLOR).value = value }

    var envFont: Font?
        get() = (getPropertyByName(ENV_FONT) as? FontProperty)?.font
        set(value) { getPropertyByName(ENV_FONT).value = value }

    var envFile: String
        get() = getPropertyByName(ENV_FILE).value as? String ?: ""
        set(value) { getPropertyByName(ENV_FILE).value = value }

    var envFileXml: String
        get() = getPropertyByName(ENV_FILE_XML).value as? String ?: ""
        set(value) { getPropertyByName(ENV_FILE_XML).value = value }

    var envDirectory: String
        get() = getPropertyByName(ENV_DIRECTORY).value as? String ?: ""
        set(value) { getPropertyByName(ENV_DIRECTORY).value = value }

    var envChoice: String?
        get() = getPropertyByName(ENV_CHOICE).value as? String
        set(value) { getPropertyByName(ENV_CHOICE).value = value }

    var envPassword: String?
        get() = getPropertyByName(ENV_PASSWORD).value as? String
        set(value) { getPropertyByName(ENV_PASSWORD).value = value }

    // -------------------------------------------------------------------------
    // Project option accessors
    // Properties live in ProjectOptions.PROJECT_GENERAL_PROPERTIES → use OptionsHelper.
    // -------------------------------------------------------------------------

    private fun getProjectProperty(name: String): Property? =
        OptionsHelper.getProjectOptionByCategoryName(ProjectOptions.PROJECT_GENERAL_PROPERTIES, name).orElse(null)

    var projBoolean: Boolean
        get() = getProjectProperty(PROJ_BOOLEAN)?.value as? Boolean ?: false
        set(value) { getProjectProperty(PROJ_BOOLEAN)?.value = value }

    var projString: String
        get() = getProjectProperty(PROJ_STRING)?.value as? String ?: ""
        set(value) { getProjectProperty(PROJ_STRING)?.value = value }

    var projInteger: Int
        get() = getProjectProperty(PROJ_INTEGER)?.value as? Int ?: 0
        set(value) { getProjectProperty(PROJ_INTEGER)?.value = value }

    fun getProjElement(): NamedElement? =
        getProjectProperty(PROJ_ELEMENT)?.value as? NamedElement

    fun setProjElement(element: NamedElement) {
        getProjectProperty(PROJ_ELEMENT)?.value = element
    }

    fun getProjElementPackageOnly(): Package? =
        getProjectProperty(PROJ_ELEMENT_PACKAGE_ONLY)?.value as? Package

    fun setProjElementPackageOnly(pkg: Package) {
        getProjectProperty(PROJ_ELEMENT_PACKAGE_ONLY)?.value = pkg
    }

    fun getProjElementList(): Array<Element> =
        (getProjectProperty(PROJ_ELEMENT_LIST) as? ElementListProperty)?.elements ?: emptyArray()

    fun setProjElementList(elements: Array<Element>) {
        getProjectProperty(PROJ_ELEMENT_LIST)?.value = elements
    }

    fun getProjElementListTyped(): Array<Element> =
        (getProjectProperty(PROJ_ELEMENT_LIST_TYPED) as? ElementListProperty)?.elements ?: emptyArray()

    fun setProjElementListTyped(elements: Array<Element>) {
        getProjectProperty(PROJ_ELEMENT_LIST_TYPED)?.value = elements
    }

    var projChoice: String?
        get() = getProjectProperty(PROJ_CHOICE)?.value as? String
        set(value) { getProjectProperty(PROJ_CHOICE)?.value = value }

    var projColor: Color?
        get() = (getProjectProperty(PROJ_COLOR) as? ColorProperty)?.color
        set(value) { getProjectProperty(PROJ_COLOR)?.value = value }

    companion object {
        const val ENV_GROUP = "Options Example - Environment"
        const val PROJ_GROUP = "Options Example - Project"

        // Environment option IDs (also used as display labels in the options dialog)
        const val ENV_BOOLEAN = "Example boolean"
        const val ENV_STRING = "Example string"
        const val ENV_INTEGER = "Example integer"
        const val ENV_DOUBLE = "Example double"
        const val ENV_FLOAT = "Example float"
        const val ENV_LONG = "Example long"
        const val ENV_COLOR = "Example color"
        const val ENV_FONT = "Example font"
        const val ENV_FILE = "Example file (any)"
        const val ENV_FILE_XML = "Example file (xml/xmi only)"
        const val ENV_DIRECTORY = "Example directory"
        const val ENV_CHOICE = "Example choice"
        const val ENV_PASSWORD = "Example password"

        const val CHOICE_A = "Option A"
        const val CHOICE_B = "Option B"
        const val CHOICE_C = "Option C"

        // Project option IDs
        const val PROJ_BOOLEAN = "Example boolean (project)"
        const val PROJ_STRING = "Example string (project)"
        const val PROJ_INTEGER = "Example integer (project)"
        const val PROJ_ELEMENT = "Example element (project)"
        const val PROJ_ELEMENT_PACKAGE_ONLY = "Example element - packages only (project)"
        const val PROJ_ELEMENT_LIST = "Example element list (project)"
        const val PROJ_ELEMENT_LIST_TYPED = "Example element list - classes only (project)"
        const val PROJ_CHOICE = "Example choice (project)"
        const val PROJ_COLOR = "Example color (project)"

        const val PROJ_CHOICE_1 = "Choice 1"
        const val PROJ_CHOICE_2 = "Choice 2"
        const val PROJ_CHOICE_3 = "Choice 3"
    }
}
