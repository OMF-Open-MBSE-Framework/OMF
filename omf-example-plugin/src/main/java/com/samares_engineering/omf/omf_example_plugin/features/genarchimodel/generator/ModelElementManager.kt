package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator

import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.OMFMBSWProfile
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.ModelArchitectureGenerator.Companion.generatedPackage
import org.bouncycastle.asn1.x500.style.RFC4519Style.owner

class ModelElementManager {

    private val architectureFactory: PluginArchitectureFactory
        get() = PluginArchitectureFactory
    private val profile: OMFMBSWProfile
        get() = OMFMBSWProfile.getInstance()


    fun createTypeElement(
        typeInfo: TypeInfo,
        defaultPackageName: String
    ): Type? {
        val rawTypeClass = typeInfo.rawType ?: return null
        val typeName = rawTypeClass.simpleName
        val packageName = rawTypeClass.packageName

        // Create or find the raw type in the model
        val rawTypeElement = findOrCreateClass(packageName, typeName)

        // If there are no type arguments, return the raw type
        if (typeInfo.typeArguments.isEmpty()) {
            return rawTypeElement
        }

        // Create a new Classifier to represent the parameterized type
        val parameterizedTypeName = buildParameterizedTypeName(typeInfo)
        val parameterizedTypeElement = findOrCreateClass(defaultPackageName, parameterizedTypeName)

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
        architectureFactory.createGeneralization(rawTypeElement, parameterizedTypeElement)

        return parameterizedTypeElement
    }

    // Find or create package

    fun findOrCreatePackage(packageName: String): Package? {
        var currentPackage = generatedPackage

        val packageParts = packageName.split(".")
        for (packagePart in packageParts) {
            currentPackage = currentPackage?.nestedPackage?.firstOrNull { it.name == packagePart } ?: createPackage(
                packagePart,
                currentPackage
            )
        }

        return currentPackage
    }

    private fun createPackage(packageName: String, parentPackage: Package?): Package {
        val createdPackage = OMFUtils.getProject().elementsFactory.createPackageInstance()
        createdPackage.name = packageName
        createdPackage.owner = parentPackage
        val namespace = profile.namespacePackage()
        namespace.apply(createdPackage)
        var parentNameSpace = (namespace.getNamespace(parentPackage)?:"") + "."
        parentNameSpace = if(parentNameSpace == ".") "" else parentNameSpace
        val packageNameSpace = if(parentPackage is Model) "" else  parentNameSpace + (parentPackage?.name ?: "")
        namespace.setNamespace(createdPackage, packageNameSpace)
        return createdPackage
    }

    fun findOrCreateClass(
        ownerClass: String,
        className: String
    ): Classifier {
        return findOrCreateClass(findOrCreatePackage(ownerClass)!!, className)
    }

    fun findOrCreateClass(
        owner: Element,
        className: String,
        isEnum: Boolean = false,
        isAnnotation: Boolean = false
    ): Classifier {
        // Try to find the class by name under the given owner
        val existingType = owner.ownedElement?.filterIsInstance<Class>()?.firstOrNull { it.name == className }
        if (existingType != null) {
            return existingType
        }

        // Apply stereotypes
        return when {
            isEnum -> {
                architectureFactory.createEnumeration(owner, className)
            }

            isAnnotation -> {
                architectureFactory.createAnnotation(owner, className)
            }

            else -> {
                architectureFactory.createCodeClass(owner, className)
            }
        }

    }


    private fun buildParameterizedTypeName(typeInfo: TypeInfo): String {
        val rawTypeName = typeInfo.rawType?.simpleName ?: "Unknown"
        if (typeInfo.typeArguments.isEmpty()) {
            return rawTypeName
        }
        val typeArgsNames = typeInfo.typeArguments.joinToString(", ") { buildParameterizedTypeName(it) }
        return "$rawTypeName<$typeArgsNames>"
    }

    fun findOrCreateEnumeration(
        ownerPackage: Package,
        enumName: String,
        enumLiterals: List<String> = emptyList()
    ): Any {
        val existingEnum =
            ownerPackage?.ownedElement?.filterIsInstance<Enumeration>()?.firstOrNull { it.name == enumName }
        if (existingEnum != null) {
            return existingEnum
        }

        val newEnum = PluginArchitectureFactory.createEnumeration(ownerPackage, enumName, enumLiterals)
        return newEnum
    }

    private fun applyInheritedStereotypes(clazz: Class?, superclass: java.lang.Class<*>) {
        val superclassName = superclass.simpleName

        when (superclassName) {
            "Hook" -> profile.hook().apply(clazz)
            "UIAction" -> profile.uiAction().apply(clazz)
            "LiveAction" -> profile.liveAction().apply(clazz)
            "Option" -> profile.option().apply(clazz)
            "Feature" -> {
                profile.feature().apply(clazz)
                // Find and set associated elements
                setFeatureAssociations(clazz)
            }
        }
    }
    private fun setFeatureAssociations(clazz: Class?) {
        if (clazz == null) return

        val hooks = mutableListOf<Element>()
        val uiActions = mutableListOf<Element>()
        val liveActions = mutableListOf<Element>()
        val options = mutableListOf<Element>()


        clazz.ownedAttribute.forEach { attribute ->
            val type = attribute.type
            if (type != null) {
                when {
                    profile.hook().`is`(type) -> hooks.add(type)
                    profile.uiAction().`is`(type) -> uiActions.add(type)
                    profile.liveAction().`is`(type) -> liveActions.add(type)
                    profile.option().`is`(type) -> options.add(type)
                }
            }
        }

        // Set the associations in the stereotype
        profile.feature().setHooks(clazz, hooks)
        profile.feature().setUiActions(clazz, uiActions)
        profile.feature().setLiveActions(clazz, liveActions)
        profile.feature().setOptions(clazz, options)
    }



}

data class TypeInfo(
    val rawType: java.lang.Class<*>?,
    val typeArguments: List<TypeInfo> = emptyList()
)
