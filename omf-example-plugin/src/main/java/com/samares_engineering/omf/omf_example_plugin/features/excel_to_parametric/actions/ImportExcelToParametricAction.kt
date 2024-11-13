package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.actions

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.TaggedValue
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.ElementSelector
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.ExcelParametricImporter
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.XLSFileChooser
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.exception.DialogCanceledByUser
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.exception.NoOwnerSelectedException
import java.io.File

import java.io.FileInputStream
import java.io.FileNotFoundException
import java.util.function.Predicate
import javax.swing.text.html.HTML.Tag

@BrowserAction
@DeactivateListener
@MDAction(actionName = "Import Parametric from Excel", category = "OMF EXAMPLE")
class ImportExcelToParametricAction : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        return OMFUtils.getProject() != null && selectedElements.size == 1
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        var importer: ExcelParametricImporter? = null

        try {
            val excelFile = chooseExcelFile()
            val importOwner: Element = selectedElements[0]
            val ownerProperties = SysMLFactory.getInstance().createBlock(importOwner)
            val ownerConstraint = SysMLFactory.getInstance().createPackage(importOwner)
            importer = ExcelParametricImporter(excelFile)
            importer.importValueProperties(ownerProperties)
            importer.importConstraints(ownerProperties)
        } catch (canceled: DialogCanceledByUser) {
            OMFLogger.warnToSystemConsole("Import canceled by user.")
        } catch (unchecked: Exception) {
            OMFLogger.err("An error occurred during the import process.", unchecked)
        } finally {
            importer?.close()

        }

    }


    @Throws(DialogCanceledByUser::class)
    fun selectTargetImport(): Element? {
        try {
            return ElementSelector.promptUserToSelectElement(Predicate { element: Element -> true })
                .orElseThrow { NoOwnerSelectedException() }
        } catch (e: NoOwnerSelectedException) {
            OMFLogger.warnToSystemConsole("No Owner selected, import aborted.")
        }
        return null
    }

    private fun chooseExcelFile(): File {
        XLSFileChooser.getInstance().open()
        if (XLSFileChooser.getInstance().getSelectedFile() == null) throw DialogCanceledByUser()
        try {
            val xlsPath: String = XLSFileChooser.getInstance().getSelectedFile().getAbsolutePath()
            return File(xlsPath)
        } catch (e: FileNotFoundException) {
            OMFLogger.warn("File not found: " + XLSFileChooser.getInstance().getSelectedFile().getAbsolutePath())
            throw e //TODO: rethrow a more specific exception
        }
    }

}
