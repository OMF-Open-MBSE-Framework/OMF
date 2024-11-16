package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric

import com.nomagic.magicdraw.uml.Finder
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DataType
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ValueSpecification
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File

class ExcelParametricImporter(private val file: File) {

    val integerType: DataType by lazy { findType("Integer") as DataType }
    val realType: DataType by lazy { findType("Real") as DataType }
    val stringType: DataType by lazy { findType("String") as DataType }
    val workbook = open()

    // Maps for easy access to ValueProperties and cells
    private val nameToCellMap = mutableMapOf<String, String>() // Map name to cell reference
    private val cellToMDElementMap = mutableMapOf<String, NamedElement>() // Map cell to ValueProperty or ConstraintBlock

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
                nameToCellMap[propertyName] = cellReference // Map name to cell
                cellToMDElementMap[cellReference] = valueProperty // Map cell to ValueProperty
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
            cellToMDElementMap[equationCell.address.formatAsString()] = constraintBlock!!

            // Store the equation for the next phase
            val equation = "$constraintName = " + equationCell.cellFormula
            constraintsToCreate.add(constraintBlock to equation)

            listImportedConstraints.add(constraintName)
        }

        // Phase 2: Create all Constraints (link constraint parameters and set the equations)
        for ((constraintBlock, equation) in constraintsToCreate) {
            // Parse formula to determine dependencies (e.g., cell references)
            val dependentCells = parseFormula(equation)

            // Create constraint parameters for each dependent cell
            createConstraintParameters(dependentCells, constraintBlock)

            // Set up the equation for the constraint
            createConstraint(constraintBlock, dependentCells, equation)
        }

        return listImportedConstraints
    }


    private fun createConstraint(
        constraintBlock: Class?,
        dependentCells: List<String>,
        equation: String
    ) {
        val constraintSpecification =
            SysMLFactory.getInstance().createConstraint(constraintBlock, buildEquationString(dependentCells, equation))
    }

    private fun createConstraintParameters(
        dependentCells: List<String>,
        constraintBlock: Class?
    ) {
        dependentCells.forEach { cellReference ->
            cellToMDElementMap[cellReference]?.let { valueProperty ->
                val constraintParameter = SysMLFactory.getInstance().createConstraintParameter(constraintBlock)
                constraintParameter.name = valueProperty.name
                constraintParameter.type = when (valueProperty) {
                    is Classifier -> valueProperty
                    is Property -> valueProperty.type
                    else -> throw IllegalArgumentException("Referenced element is not a Classifier or Property")
                }
                // Link the constraint parameter to the corresponding ValueProperty
                linkConstraintParameterToValueProperty(constraintParameter, valueProperty)
            }
        }
        val outputParameter = SysMLFactory.getInstance().createConstraintParameter(constraintBlock).let { outputParameter ->
            outputParameter.name = constraintBlock!!.name
            outputParameter.type = realType
        }
        
    }

    private fun createConstraintBlock(
        owner: Classifier,
        constraintName: String
    ): Class? {
        val constraintBlock = SysMLFactory.getInstance().createConstraintBlock(owner)
        constraintBlock.name = constraintName
        return constraintBlock
    }

    // Parse Excel formula to find referenced cell names
    private fun parseFormula(formula: String): List<String> {
        val references = mutableListOf<String>()

        // Mise à jour du regex pour couvrir :
        // 1. Les références du type ValueProperties!B3
        // 2. Les références locales comme C2, C9, etc.
        val regex = Regex("""(ValueProperties![A-Z]+\d+)|([A-Z]+\d+)""")

        regex.findAll(formula).forEach { matchResult ->
            val valuePropertiesReference = matchResult.groups[1]?.value
            val localReference = matchResult.groups[2]?.value

            if (!valuePropertiesReference.isNullOrEmpty()) {
                references.add(valuePropertiesReference)
            }

            if (!localReference.isNullOrEmpty()) {
                references.add(localReference)
            }
        }

        return references
    }



    private fun buildEquationString(cells: List<String>, formula: String): String {
        var equation = formula

        val referenceRegex = Regex("""ValueProperties!([A-Z]+\d+|[\w\s\(\)]+)""")

        referenceRegex.findAll(formula).forEach { matchResult ->
            val reference = matchResult.groupValues[1]

            // Chercher dans la map pour obtenir le bon nom de la propriété
            cellToMDElementMap[reference]?.name?.let { propertyName ->
                equation = equation.replace("ValueProperties!$reference", propertyName)
            }
        }

        // Traitement des références locales dans le même onglet
        cells.forEach { cellReference ->
            if (!referenceRegex.containsMatchIn(cellReference)) {
                cellToMDElementMap[cellReference]?.name?.let { propertyName ->
                    equation = equation.replace(cellReference, propertyName)
                }
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
}
