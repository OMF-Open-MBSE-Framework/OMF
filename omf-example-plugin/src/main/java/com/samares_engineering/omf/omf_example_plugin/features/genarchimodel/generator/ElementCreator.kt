package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator

import com.nomagic.magicdraw.core.Application
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.nomagic.uml2.impl.ElementsFactory
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.ModelArchitectureGenerator.Companion.generatedPackage

class ElementCreator {

    private val magicdrawFactory: ElementsFactory
        get() = Application.getInstance().project!!.elementsFactory


    fun createTypeElement(
        typeInfo: TypeInfo,
        defaultPackageName: String
    ): Type? {
        val rawTypeClass = typeInfo.rawType ?: return null
        val typeName = rawTypeClass.simpleName
        val packageName = rawTypeClass.packageName

        // Create or find the raw type in the model
        val rawTypeElement = findOrCreateClass(findOrCreatePackage(packageName), typeName)

        // If there are no type arguments, return the raw type
        if (typeInfo.typeArguments.isEmpty()) {
            return rawTypeElement
        }

        // Create a new Classifier to represent the parameterized type
        val parameterizedTypeName = buildParameterizedTypeName(typeInfo)
        val parameterizedTypeElement = findOrCreateClass(findOrCreatePackage(defaultPackageName), parameterizedTypeName)

        // Create a property 'generic' to hold the type arguments
        for ((index, typeArgInfo) in typeInfo.typeArguments.withIndex()) {
            val typeArgElement = createTypeElement(typeArgInfo, defaultPackageName)
            if (typeArgElement != null) {
                val genericProperty = SysMLFactory.getInstance().createProperty(parameterizedTypeElement)
                genericProperty.name = "T$index"
                genericProperty.type = typeArgElement
                // Optionally set property as read-only, derived, etc.
            }
        }

        // Optionally, set the raw type as a generalization or association
        val generalization = magicdrawFactory.createGeneralizationInstance()
        generalization.specific = parameterizedTypeElement
        generalization.general = rawTypeElement
        generalization.owner = parameterizedTypeElement

        return parameterizedTypeElement
    }

    // Find or create package

    fun findOrCreatePackage(packageName: String): Package? {
        var currentPackage = generatedPackage

        val packageParts = packageName.split(".")
        for (packagePart in packageParts) {
            val existingPackage = currentPackage?.nestedPackage?.firstOrNull { it.name == packagePart }
            currentPackage = if (existingPackage == null) {
                createPackage(packagePart, currentPackage)
            } else {
                existingPackage
            }
        }

        return currentPackage
    }

    private fun createPackage(packageName: String, parentPackage: Package?): Package {
        val createdPackage = OMFUtils.getProject().elementsFactory.createPackageInstance()
        createdPackage.name = packageName
        createdPackage.owner = parentPackage
        return createdPackage
    }

    fun findOrCreateClass(
        ownerClass: String,
        className: String
    ): Class? {
        return findOrCreateClass(findOrCreatePackage(ownerClass), className)
    }

    fun findOrCreateClass(
        owner: Element?,
        className: String
    ): Class? {
        // Try to find the class by name under the given owner
        val existingType = owner?.ownedElement?.filterIsInstance<Class>()?.firstOrNull { it.name == className }
        if (existingType != null) {
            return existingType
        }

        // If not found, create a new class
        val newClass = SysMLFactory.getInstance().createBlock(owner)
        newClass.name = className
        newClass.owner = owner
        return newClass
    }


    private fun buildParameterizedTypeName(typeInfo: TypeInfo): String {
        val rawTypeName = typeInfo.rawType?.simpleName ?: "Unknown"
        if (typeInfo.typeArguments.isEmpty()) {
            return rawTypeName
        }
        val typeArgsNames = typeInfo.typeArguments.joinToString(", ") { buildParameterizedTypeName(it) }
        return "$rawTypeName<$typeArgsNames>"
    }

    fun findOrCreateEnumeration(ownerPackage: Package?, enumName: String, enumLiterals:List<String> = emptyList()): Any {
        val existingEnum = ownerPackage?.ownedElement?.filterIsInstance<Enumeration>()?.firstOrNull { it.name == enumName }
        if (existingEnum != null) {
            return existingEnum
        }

        val newEnum = PluginArchitectureFactory.createEnumeration(ownerPackage, enumName, enumLiterals)
        return newEnum
    }


}

data class TypeInfo(
    val rawType: java.lang.Class<*>?,
    val typeArguments: List<TypeInfo> = emptyList()
)
