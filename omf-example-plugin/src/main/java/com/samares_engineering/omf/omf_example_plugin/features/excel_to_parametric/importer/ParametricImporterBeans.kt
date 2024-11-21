package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port

class ParametricImporterBeans {

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
    var humanEquation: String = ""
    var outputParameter: Port? = null
    var constraintPropertyElement: Property? = null
}
