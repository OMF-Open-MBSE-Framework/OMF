package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.NamedElement

class BeanMapping {
    val propertyNameToCellMap = mutableMapOf<String, String>() // "Name" -> "Cell reference"
    val constraintNameToCell = mutableMapOf<String, String>() // "Name" -> "Cell reference"
    val cellToValueProperty = mutableMapOf<String, NamedElement>() // "Cell reference" -> ValueProperty
    val cellToConstraint = mutableMapOf<String, NamedElement>() // "Cell reference" -> Constraint,

    val cellToValuePropertyBean = mutableMapOf<String, ValuePropertyBean>() // "Cell reference" -> ValuePropertyBean
    val cellToConstraintBean = mutableMapOf<String, ConstraintBean>() // "Cell reference" -> ConstraintBean


    val constraintNameToBean = mutableMapOf<String, ConstraintBean>() // "Name" -> ConstraintBean
}