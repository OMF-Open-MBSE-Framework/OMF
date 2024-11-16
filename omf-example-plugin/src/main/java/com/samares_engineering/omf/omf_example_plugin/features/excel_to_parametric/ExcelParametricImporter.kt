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
    private val regexCell = "([A-Z]+\\d+)" // ex:


    private fun regexValueFromSheet(sheetName:String): String { return """($sheetName![A-Z]+\d+)"""}
    private val regexRefCellPropertySheet:String
        get(){val sheetNameRegex = regexValueFromSheet(valuesSheetName)
            return """$sheetNameRegex|$regexCell"""}

    // Maps for easy access to ValueProperties and cells
    private val propertyNameToCellMap = mutableMapOf<String, String>() // "Name" -> "Cell reference"
    private val constraintNameToCell = mutableMapOf<String, String>() // "Name" -> "Cell reference"
    private val cellToValueProperty = mutableMapOf<String, NamedElement>() // "Cell reference" -> ValueProperty
    private val cellToConstraint = mutableMapOf<String, NamedElement>() // "Cell reference" -> Constraint

    private fun open(): XSSFWorkbook {
        return XSSFWorkbook(file)
    }

    fun close() {
        workbook.close()
    }

    fun importValueProperties(owner: Classifier): MutableList<String> {
        val sheet = workbook.getSheetAt(0)  // Get the first sheet
        val listImportedProperties = mutableListOf<String>()

        val rowIterator = sheet.rowIterator()
        rowIterator.next() // Skip the first row (header)
        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val nameCell = row.getCell(0)
            val unit = row.getCell(1)
            val valueCell = row.getCell(2) // Assume the result value is in the second column
            val cellReference = valueCell.address.formatAsString() // Cell reference (e.g., "B2")

            if (nameCell != null && valueCell != null) {
                val propertyName = nameCell.stringCellValue
                val propertyValue = valueCell.toString()

                // Create ValueProperty
                val valueProperty = SysMLFactory.getInstance().createValueProperty(owner)
                valueProperty.name = propertyName

                val defaultValue: ValueSpecification = when {
                    propertyValue.toIntOrNull() != null -> SysMLFactory.getInstance().createLiteralInteger(valueProperty, propertyValue.toInt())
                    propertyValue.toDoubleOrNull() != null -> SysMLFactory.getInstance().createLiteralReal(valueProperty, propertyValue.toDouble())
                    else -> SysMLFactory.getInstance().createLiteralString(valueProperty, propertyValue)
                }
                valueProperty.defaultValue = defaultValue

                valueProperty.type = getTypeFromValue(propertyValue)

                listImportedProperties.add(propertyName)

                // Store mappings for cell reference and ValueProperty
                propertyNameToCellMap[propertyName] = cellReference // Map name to cell
                cellToValueProperty["$valuesSheetName!$cellReference"] = valueProperty // Map cell to ValueProperty
            }
        }
        return listImportedProperties
    }

    private fun getTypeFromValue(propertyValue: String) = when {
        propertyValue.toIntOrNull() != null -> integerType
        propertyValue.toDoubleOrNull() != null -> realType
        else -> stringType
    }

    fun importConstraints(owner: Classifier): MutableList<String> {
        val sheet = workbook.getSheetAt(1)
        val listImportedConstraints = mutableListOf<String>()
        val constraintsToCreate = mutableListOf<Pair<Class, String>>() // Stores pairs of (ConstraintBlock, Equation)

        val rowIterator = sheet.rowIterator()
        rowIterator.next() // Skip the first row (header)

        // Phase 1: Create all ConstraintBlocks
        while (rowIterator.hasNext()) {
            val row = rowIterator.next()
            val constraintNameCell = row.getCell(0) // Name of the constraint/result property
            val unit = row.getCell(2) //
            val equationCell = row.getCell(3) // Equation cell

            if (constraintNameCell == null || equationCell == null) continue

            val constraintName = constraintNameCell.stringCellValue

            // Create the constraint block
            val constraintBlock = createConstraintBlock(owner, constraintName)
            cellToConstraint[equationCell.address.formatAsString()] = constraintBlock!!

            // Store the equation for the next phase
            val equation = "$constraintName = " + equationCell.cellFormula
            constraintsToCreate.add(constraintBlock to equation)

            constraintNameToCell[constraintName] = equationCell.address.formatAsString()
            cellToConstraint[equationCell.address.formatAsString()] = constraintBlock

            listImportedConstraints.add(constraintName)
        }

        // Phase 2: Create all Constraints (link constraint parameters and set the equations)
        for ((constraintBlock, equation) in constraintsToCreate) {
            // Parse formula to determine dependencies (e.g., cell references)
            val dependentCells = parseFormula(equation, valuesSheetName)

            // Create constraint parameters for each dependent cell
            createConstraintParameters(dependentCells, constraintBlock)

            // Set up the equation for the constraint
            createConstraint(constraintBlock, dependentCells, equation)
        }

        return listImportedConstraints
    }


    private fun createConstraint(
        constraintBlock: Class?,
        dependentCells: FormulaReferences,
        equation: String
    ) {
        val constraintSpecification =
            SysMLFactory.getInstance().createConstraint(constraintBlock, buildEquationString(dependentCells, equation))
    }

    private fun createConstraintParameters(
        dependentCells: FormulaReferences,
        constraintBlock: Class?
    ) {
        dependentCells.valueReferences
            .filter {cellToValueProperty[it] != null}
            .map {cellToValueProperty[it]}
            .forEach {namedElement ->
                createConstraintParameter(constraintBlock, namedElement)
            }


        dependentCells.constraintReferences
            .filter {cellToConstraint[it] != null}
            .map {cellToConstraint[it]}
            .forEach {namedElement ->
                createConstraintParameter(constraintBlock, namedElement)
            }

        val outputParameter = SysMLFactory.getInstance().createConstraintParameter(constraintBlock).let { outputParameter ->
            outputParameter.name = constraintBlock!!.name
            outputParameter.type = realType
        }

    }

    private fun createConstraintParameter(
        constraintBlock: Class?,
        namedElement: NamedElement?
    ) {
        val constraintParameter = SysMLFactory.getInstance().createConstraintParameter(constraintBlock)
        constraintParameter.name = namedElement?.name
        constraintParameter.type = when (namedElement) {
            is Classifier -> namedElement
            is Property -> namedElement.type
            else -> throw IllegalArgumentException("Referenced element is not a Classifier or Property")
        }
        // Link the constraint parameter to the corresponding ValueProperty
        linkConstraintParameterToValueProperty(constraintParameter, namedElement)
    }

    private fun createConstraintBlock(
        owner: Classifier,
        constraintName: String
    ): Class? {
        val constraintBlock = SysMLFactory.getInstance().createConstraintBlock(owner)
        constraintBlock.name = constraintName
        return constraintBlock
    }



    private fun parseFormula(formula: String, sheetName: String): FormulaReferences {
        val valueReferences = mutableListOf<String>()
        val constraintReferences = mutableListOf<String>()

        // Regex combiné pour trouver les deux types de références
        val regex = Regex(regexRefCellPropertySheet)

//        println("Parsing formula: $formula")
//        println("Regex: $regex")
//
//        regex.findAll(formula).forEach { matchResult ->
//            println("Full match: ${matchResult.value}")
//            println("Group 1 (ValueProperties): ${matchResult.groups[1]?.value}")
//            println("Group 2 (Local): ${matchResult.groups[2]?.value}")
//        }


        regex.findAll(formula).forEach { matchResult ->
            val valuePropertiesReference = matchResult.groups[1]?.value // Match `ValueProperties!B3`
            val localReference = matchResult.groups[2]?.value           // Match `C2`

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

        // Remplacer les références `ValueProperties!B3` par les noms des propriétés
        references.valueReferences.forEach { reference ->
            val propertyName = cellToValueProperty[reference]?.name
            if (propertyName != null) {
                equation = equation.replace(reference, propertyName)
            }
        }

        // Remplacer les références locales (`C2`, `C9`, etc.) par les noms des contraintes
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

    // Function to link a constraint parameter to a value property (can be adapted to MagicDraw APIs)
    private fun linkConstraintParameterToValueProperty(
        constraintParameter: NamedElement,
        valueProperty: NamedElement
    ) {
        // Here, link the constraint parameter to the value property
        // Adapt this function according to the tools and APIs available in your project
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
        val unit: String
    ) : EquationParameter(name, type)

    class ConstraintBean(
        name: String,
        type: String,
        val equation: String,
        val mapParameterNameToConstraintBean: Map<String, ConstraintBean>
    ) : EquationParameter(name, type)
}
