package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer

import akka.dispatch.sysmsg.Create
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ValueSpecification
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer.TypeFinder.findType
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer.TypeFinder.realType
import org.apache.poi.hssf.record.CFRuleBase.parseFormula

class ConcreteSysMLElementFactory(
    val valuePropertyBeans: List<ValuePropertyBean>,
    val constraintBeans: List<ConstraintBean>,
    val valuesSheetName: String,
    val beanMapping: BeanMapping
) {

    private val valuePropertyNameToElement = mutableMapOf<String, Property>()
    private val constraintNameToElement = mutableMapOf<String, Class>()

    fun createSysMLElements(owner: Classifier) {
        // Create ValuePropertievals
        val vaFormulaReferencesluePropertyNameToElement = mutableMapOf<String, Property>()
        for (valuePropertyBean in valuePropertyBeans) {
            val valueProperty = createValueProperty(owner, valuePropertyBean)

            // Update concreteElement in the bean
            valuePropertyBean.concreteElement = valueProperty

            valuePropertyNameToElement[valuePropertyBean.name] = valueProperty

            // Map cell reference to ValueProperty
            val cellReference = beanMapping.propertyNameToCellMap[valuePropertyBean.name]
            if (cellReference != null) {
                beanMapping.cellToValueProperty["$valuesSheetName!$cellReference"] = valueProperty
            }
        }

        // Create Constraints
        for (constraintBean in constraintBeans) {
            // Create ConstraintBlock
            val constraintBlock = createConstraintBlock(owner, constraintBean)

            // Update concreteElement in the bean
            constraintBean.concreteElement = constraintBlock

            constraintNameToElement[constraintBean.name] = constraintBlock

            // Map cell reference to ConstraintBlock
            val cellReference = beanMapping.constraintNameToCell[constraintBean.name]
            if (cellReference != null) {
                beanMapping.cellToConstraint[cellReference] = constraintBlock
            }

            constraintNameToElement[constraintBean.name] = constraintBlock
        }

        // Only after create constraint parameters and constraints, as they depend on ValueProperties and Constraints
        for (constraintBean in constraintBeans) {
            val constraintBlock = constraintNameToElement[constraintBean.name]
            if (constraintBlock != null) {
//                 Create constraint parameters
                createConstraintParameters(constraintBean, constraintBlock)

                // Set up the equation for the constraint
                createConstraint(constraintBlock, constraintBean.humanEquation)
            }
        }
    }

    private fun createConstraintBlock(
        owner: Classifier,
        constraintBean: ConstraintBean
    ): Class {
        val constraintBlock = SysMLFactory.getInstance().createConstraintBlock(owner)
        constraintBlock.name = constraintBean.name
        return constraintBlock
    }

    private fun createValueProperty(
        owner: Classifier,
        valuePropertyBean: ValuePropertyBean
    ): Property {
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
        return valueProperty
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
//            equationParameter.concreteElement = constraintParameter

            // Link the constraint parameter to the corresponding ValueProperty or Constraint
//            linkConstraintParameterToEquationParameter(constraintParameter, equationParameter)

        }

        // Create output parameter
        val outputParameter = SysMLFactory.getInstance().createConstraintParameter(constraintBlock).apply {
            name = constraintBlock.name
            type = realType
        }
        // Update concreteElement for the output parameter
        constraintBean.outputParameter = outputParameter
    }

    private fun createConstraint(
        constraintBlock: Class,
        equation: String
    ) {
        val constraintSpecification = SysMLFactory.getInstance()
            .createConstraint(constraintBlock, equation)
    }




}