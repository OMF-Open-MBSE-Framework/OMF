package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric

import com.github.javaparser.resolution.Navigator.findType
import com.nomagic.magicdraw.uml.Finder
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DataType
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ValueSpecification
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class ExcelParametricImporter(private val file: File) {

    val integerType: DataType by lazy { findType("Integer") as DataType }
    val realType: DataType by lazy { findType("Real") as DataType }
    val stringType: DataType by lazy { findType("String") as DataType }
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
    private val propertyNameToCellMap = mutableMapOf<String, String>() // "Name" -> "Cell reference"
    private val constraintNameToCell = mutableMapOf<String, String>() // "Name" -> "Cell reference"
    private val cellToValueProperty = mutableMapOf<String, NamedElement>() // "Cell reference" -> ValueProperty
    private val cellToConstraint = mutableMapOf<String, NamedElement>() // "Cell reference" -> Constraint

    // Beans to store the parsed data
    val valuePropertyBeans = mutableListOf<ValuePropertyBean>()
    val constraintBeans = mutableListOf<ConstraintBean>()
    val beans: List<EquationParameter>
        get() = valuePropertyBeans + constraintBeans
    private val cellToValuePropertyBean = mutableMapOf<String, ValuePropertyBean>()
    private val cellToConstraintBean = mutableMapOf<String, ConstraintBean>()

    private fun open(): XSSFWorkbook {
        return XSSFWorkbook(file)
    }

    fun close() {
        workbook.close()
    }

    fun generateBeans() {
        parseValueProperties()
        parseConstraints()
    }

    private fun parseValueProperties() {
        val sheet = workbook.getSheetAt(0)  // Get the first sheet
        val rowIterator = sheet.rowIterator()
        rowIterator.next() // Skip the first row (header)
        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val nameCell = row.getCell(0)
            val unitCell = row.getCell(1)
            val valueCell = row.getCell(2) // Assume the value is in the third column
            val cellReference = valueCell.address.formatAsString() // Cell reference (e.g., "C2")

            if (nameCell != null && valueCell != null) {
                val propertyName = nameCell.stringCellValue
                val propertyValue = valueCell.toString()
                val unit = unitCell?.stringCellValue ?: ""

                val valuePropertyBean = ValuePropertyBean(
                    name = propertyName,
                    type = getTypeFromValue(propertyValue).name,
                    value = propertyValue,
                    unit = unit
                )

                valuePropertyBeans.add(valuePropertyBean)
                propertyNameToCellMap[propertyName] = cellReference // Map name to cell
                cellToValuePropertyBean["$valuesSheetName!$cellReference"] = valuePropertyBean // Map cell to ValuePropertyBean
            }
        }
    }

    private fun parseConstraints() {
        val sheet = workbook.getSheetAt(1)
        val rowIterator = sheet.rowIterator()
        rowIterator.next() // Skip the first row (header)

        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val constraintNameCell = row.getCell(0) // Name of the constraint/result property
            val unitCell = row.getCell(2)
            val equationCell = row.getCell(3) // Equation cell
            val cellReference = equationCell.address.formatAsString()

            if (constraintNameCell == null || equationCell == null) continue

            val constraintName = constraintNameCell.stringCellValue
            val unit = unitCell?.stringCellValue ?: ""
            val equation = "$constraintName = " + equationCell.cellFormula

            val constraintBean = ConstraintBean(
                name = constraintName,
                type = realType.name,
                equation = equation,
                mapParameterNameToEquationParameter = mutableMapOf()
            )

            constraintBeans.add(constraintBean)
            constraintNameToCell[constraintName] = cellReference
            cellToConstraintBean[cellReference] = constraintBean
        }
    }

    private fun getTypeFromValue(propertyValue: String) = when {
        propertyValue.toIntOrNull() != null -> integerType
        propertyValue.toDoubleOrNull() != null -> realType
        else -> stringType
    }

    fun createSysMLElements(owner: Classifier) {
        // Create ValueProperties
        val valuePropertyNameToElement = mutableMapOf<String, Property>()
        for (valuePropertyBean in valuePropertyBeans) {
            val valueProperty = SysMLFactory.getInstance().createValueProperty(owner)
            valueProperty.name = valuePropertyBean.name

            val propertyValue = valuePropertyBean.value
            val defaultValue: ValueSpecification = when {
                propertyValue.toIntOrNull() != null -> SysMLFactory.getInstance()
                    .createLiteralInteger(valueProperty, propertyValue.toInt())
                propertyValue.toDoubleOrNull() != null -> SysMLFactory.getInstance()
                    .createLiteralReal(valueProperty, propertyValue.toDouble())
                else -> SysMLFactory.getInstance()
                    .createLiteralString(valueProperty, propertyValue)
            }
            valueProperty.defaultValue = defaultValue

            valueProperty.type = findType(valuePropertyBean.type)

            // Update concreteElement in the bean
            valuePropertyBean.concreteElement = valueProperty

            valuePropertyNameToElement[valuePropertyBean.name] = valueProperty

            // Map cell reference to ValueProperty
            val cellReference = propertyNameToCellMap[valuePropertyBean.name]
            if (cellReference != null) {
                cellToValueProperty["$valuesSheetName!$cellReference"] = valueProperty
            }
        }

        // Create Constraints
        val constraintNameToElement = mutableMapOf<String, Class>()
        for (constraintBean in constraintBeans) {
            // Create ConstraintBlock
            val constraintBlock = SysMLFactory.getInstance().createConstraintBlock(owner)
            constraintBlock.name = constraintBean.name

            // Update concreteElement in the bean
            constraintBean.concreteElement = constraintBlock

            // Map cell reference to ConstraintBlock
            val cellReference = constraintNameToCell[constraintBean.name]
            if (cellReference != null) {
                cellToConstraint[cellReference] = constraintBlock
            }

            constraintNameToElement[constraintBean.name] = constraintBlock
        }

        // Now, create constraint parameters and constraints
        for (constraintBean in constraintBeans) {
            val constraintBlock = constraintNameToElement[constraintBean.name]
            if (constraintBlock != null) {
                val equation = constraintBean.equation
                val dependentCells = parseFormula(equation, valuesSheetName)

                // Map parameter names to EquationParameters (beans)
                mapParametersToEquationParameters(constraintBean, dependentCells)

                // Create constraint parameters
                createConstraintParameters(constraintBean, constraintBlock)

                // Set up the equation for the constraint
                createConstraint(constraintBlock, dependentCells, equation)
            }
        }
    }

    private fun mapParametersToEquationParameters(
        constraintBean: ConstraintBean,
        dependentCells: FormulaReferences
    ) {
        // Map ValueProperty references
        dependentCells.valueReferences.forEach { reference ->
            val valuePropertyBean = cellToValuePropertyBean[reference]
            if (valuePropertyBean != null) {
                constraintBean.mapParameterNameToEquationParameter[valuePropertyBean.name] = valuePropertyBean
            }
        }

        // Map Constraint references
        dependentCells.constraintReferences.forEach { reference ->
            val constraintBeanRef = cellToConstraintBean[reference]
            if (constraintBeanRef != null) {
                constraintBean.mapParameterNameToEquationParameter[constraintBeanRef.name] = constraintBeanRef
            }
        }
    }

    private fun createConstraintParameters(
        constraintBean: ConstraintBean,
        constraintBlock: Class
    ) {
        // Create constraint parameters for each mapped parameter
        constraintBean.mapParameterNameToEquationParameter.forEach { (paramName, equationParameter) ->
            val constraintParameter = SysMLFactory.getInstance().createConstraintParameter(constraintBlock)
            constraintParameter.name = paramName
            constraintParameter.type = findType(equationParameter.type)

            // Update concreteElement in the EquationParameter
            equationParameter.concreteElement = constraintParameter

            // Link the constraint parameter to the corresponding ValueProperty or Constraint
            linkConstraintParameterToEquationParameter(constraintParameter, equationParameter)
        }

        // Create output parameter
        val outputParameter = SysMLFactory.getInstance().createConstraintParameter(constraintBlock).apply {
            name = constraintBlock.name
            type = realType
        }
        // Update concreteElement for the output parameter
        constraintBean.concreteElement = outputParameter
    }

    private fun createConstraint(
        constraintBlock: Class,
        dependentCells: FormulaReferences,
        equation: String
    ) {
        val constraintSpecification = SysMLFactory.getInstance()
            .createConstraint(constraintBlock, buildEquationString(dependentCells, equation))
    }

    private fun parseFormula(formula: String, sheetName: String): FormulaReferences {
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
            val propertyName = cellToValueProperty[reference]?.name
            if (propertyName != null) {
                equation = equation.replace(reference, propertyName)
            }
        }

        // Replace local references (C2, C9, etc.) with constraint names
        references.constraintReferences.forEach { reference ->
            val propertyName = cellToConstraint[reference]?.name
            if (propertyName != null) {
                equation = equation.replace(reference, propertyName)
            }
        }

        return equation
    }

    private fun findType(typeName: String): Classifier? {
        return Finder.byTypeRecursively()
            .find<DataType>(OMFUtils.getProject(), arrayOf(DataType::class.java))
            .firstOrNull { it.name == typeName }
    }

    // Function to link a constraint parameter to a value property or constraint
    private fun linkConstraintParameterToEquationParameter(
        constraintParameter: NamedElement,
        equationParameter: EquationParameter
    ) {
        // Implement the logic to link the constraint parameter to the corresponding element
        // For example, create a binding or association between constraintParameter and equationParameter.concreteElement
    }

    data class FormulaReferences(
        val valueReferences: List<String>,
        val constraintReferences: List<String>
    )

    open class EquationParameter(
        val name: String,
        val type: String,
        var concreteElement: NamedElement? = null
    )

    class ValuePropertyBean(
       name: String,
       type: String,
        val value: String,
        val unit: String,
        concreteElement: NamedElement? = null
    ) : EquationParameter(name, type, concreteElement)

    class ConstraintBean(
       name: String,
       type: String,
        val equation: String,
        val mapParameterNameToEquationParameter: MutableMap<String, EquationParameter>,
       concreteElement: NamedElement? = null
    ) : EquationParameter(name, type, concreteElement) {
        var constraintPropertyElement: Property? = null
    }
}
