package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer

import com.nomagic.magicdraw.uml.Finder
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.DataType
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils

object TypeFinder {
    val integerType: DataType by lazy { findType("Integer") as DataType }
    val realType: DataType by lazy { findType("Real") as DataType }
    val stringType: DataType by lazy { findType("String") as DataType }

    fun findType(typeName: String): Classifier? {
        return Finder.byTypeRecursively()
            .find<DataType>(OMFUtils.getProject(), arrayOf(DataType::class.java))
            .firstOrNull { it.name == typeName }
    }

}