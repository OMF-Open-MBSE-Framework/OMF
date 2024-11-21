package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer.TypeFinder.integerType
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer.TypeFinder.realType
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer.TypeFinder.stringType
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class ExcelParametricImporter(private val file: File) {
    val workbook = open()
    private val valuesSheetName = "ValueProperties"
    private val regexCell = "([A-Z]+\\d+)" // ex: B3

    private fun regexValueFromSheet(sheetName: String): String {
        return """($sheetName![A-Z]+\d+)"""
    }

    private val regexRefCellPropertySheet: String
        get() {
            val sheetNameRegex = regexValueFromSheet(valuesSheetName)
            return """$sheetNameRegex|$regexCell"""
        }

    // Maps for easy access to ValueProperties and cells
   val beanMapping = BeanMapping()

    // Beans to store the parsed data
    val valuePropertyBeans = mutableListOf<ValuePropertyBean>()
    val constraintBeans = mutableListOf<ConstraintBean>()
    val beans: List<EquationParameter>
        get() = valuePropertyBeans + constraintBeans


    private fun open(): XSSFWorkbook {
        return XSSFWorkbook(file)
    }

    fun close() {
        workbook.close()
    }

    fun generateBeans() {
        parseValueProperties()
        parseConstraints()
        setEquationMappingWithBean()
    }

    private fun setEquationMappingWithBean() {
        for (constraintBean in constraintBeans) {
            val constraintBlock = beanMapping.constraintNameToBean[constraintBean.name]
            if (constraintBlock == null) {
                OMFLogger.errorToSystemConsole("ConstraintBlock not found: ${constraintBean.name}")
                continue
            }
            val equation = constraintBean.equation
            val dependentCells = parseFormula(equation)

            // Map parameter names to EquationParameters (beans)
            mapParametersToEquationParameters(constraintBean, dependentCells)

            // Build the equation string with the correct names
            constraintBean.humanEquation = buildEquationString(dependentCells, equation)
        }
    }

    fun createSysMLElements(ownerProperties: Class) {
        ConcreteSysMLElementFactory(
            valuePropertyBeans,
            constraintBeans,
            valuesSheetName,
            beanMapping)
            .createSysMLElements(ownerProperties)

    }


    private fun mapParametersToEquationParameters(
        constraintBean: ConstraintBean,
        dependentCells: FormulaReferences
    ) {
        // Map ValueProperty references
        dependentCells.valueReferences.forEach { reference ->
            val valuePropertyBean = beanMapping.cellToValuePropertyBean[reference]
            if (valuePropertyBean != null) {
                constraintBean.mapParameterNameToEquationParameter[valuePropertyBean.name] = valuePropertyBean
            } else{
                OMFLogger.errorToSystemConsole("ValueProperty reference not found: $reference")
            }
        }

        // Map Constraint references
        dependentCells.constraintReferences.forEach { reference ->
            val constraintBeanRef = beanMapping.cellToConstraintBean[reference]
            if (constraintBeanRef != null) {
                constraintBean.mapParameterNameToEquationParameter[constraintBeanRef.name] = constraintBeanRef
            }else{
                OMFLogger.errorToSystemConsole("Constraint reference not found: $reference")
            }
        }

    }


    private fun parseValueProperties() {
        val sheet = workbook.getSheetAt(0)  // Get the first sheet
        val rowIterator = sheet.rowIterator()
        rowIterator.next() // Skip the first row (header)
        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val nameCell = row.getCell(0) // -> nameCell e.g "Field length"
            val unitCell = row.getCell(1) // -> unitCell e.g "m"
            val valueCell = row.getCell(2) // -> valueCell e.g "=100"
            val cellReference = valueCell.address.formatAsString() // Cell reference (e.g., "C2")

            if (nameCell == null || valueCell == null){
                OMFLogger.errorToSystemConsole("Name or Value cell not found")
                continue
            }

            val propertyName = nameCell.stringCellValue
            val propertyValue = valueCell.toString()
            val unit = unitCell?.stringCellValue ?: ""

            val valuePropertyBean = ValuePropertyBean(
                name = "$propertyName $unit",
                type = getTypeFromValue(propertyValue).name,
                value = propertyValue,
                unit = unit
            )

            valuePropertyBeans.add(valuePropertyBean)
            beanMapping.propertyNameToCellMap[propertyName] = cellReference // Map name to cell
            beanMapping.cellToValuePropertyBean["$valuesSheetName!$cellReference"] =
            valuePropertyBean // Map cell to ValuePropertyBean
        }
    }

    private fun parseConstraints() {
        val sheet = workbook.getSheetAt(1)
        val rowIterator = sheet.rowIterator()
        rowIterator.next() // Skip the first row (header)

        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val constraintNameCell = row.getCell(0) // Name of the constraint/result property: e.g., "Total length"
            val unitCell = row.getCell(1)           // Unit of the constraint   e.g., "m"
            val equationCell = row.getCell(3)       // Equation cell  e.g., "=C2 + C3"
            val cellReference = equationCell.address.formatAsString()

            if (constraintNameCell == null || equationCell == null){
                OMFLogger.errorToSystemConsole("Name or Equation cell not found")
                continue
            }

            val constraintName = constraintNameCell.stringCellValue
            val unit = unitCell?.stringCellValue ?: ""
            val equation = "$constraintName = " + equationCell.cellFormula

            val constraintBean = ConstraintBean(
                name = "$constraintName $unit",
                type = realType.name,
                equation = equation,
                mapParameterNameToEquationParameter = mutableMapOf()
            )

            constraintBeans.add(constraintBean)
            beanMapping.constraintNameToCell[constraintName] = cellReference
            beanMapping.cellToConstraintBean[cellReference] = constraintBean
            beanMapping.constraintNameToBean[constraintBean.name] = constraintBean
        }
    }

    private fun getTypeFromValue(propertyValue: String) = when {
        propertyValue.toIntOrNull() != null -> integerType
        propertyValue.toDoubleOrNull() != null -> realType
        else -> stringType
    }



    private fun parseFormula(formula: String): FormulaReferences {
        val valueReferences = mutableListOf<String>()
        val constraintReferences = mutableListOf<String>()

        // Combined regex to find both types of references
        val regex = Regex(regexRefCellPropertySheet)

        regex.findAll(formula).forEach { matchResult ->
            val valuePropertiesReference = matchResult.groups[1]?.value // Match ValueProperties!B3
            val localReference = matchResult.groups[2]?.value           // Match C2

            if (!valuePropertiesReference.isNullOrEmpty()) {
                valueReferences.add(valuePropertiesReference)
            }

            if (!localReference.isNullOrEmpty()) {
                constraintReferences.add(localReference)
            }
        }

        return FormulaReferences(valueReferences, constraintReferences)
    }



    private fun buildEquationString(
        references: FormulaReferences,
        formula: String
    ): String {
        var equation = formula

        // Replace ValueProperties!B3 references with property names
        references.valueReferences.forEach { reference ->
            val propertyName = beanMapping.cellToValuePropertyBean[reference]?.name
            if (propertyName != null) {
                equation = equation.replace(reference, propertyName)
            }else{
                OMFLogger.errorToSystemConsole("Property name not found for $reference")
            }
        }

        // Replace local references (C2, C9, etc.) with constraint names
        references.constraintReferences.forEach { reference ->
            val propertyName = beanMapping.cellToConstraintBean[reference]?.name
            if (propertyName != null) {
                equation = equation.replace(reference, propertyName)
            }else{
                OMFLogger.errorToSystemConsole("Constraint name not found for $reference")
            }
        }

        return equation
    }


}
