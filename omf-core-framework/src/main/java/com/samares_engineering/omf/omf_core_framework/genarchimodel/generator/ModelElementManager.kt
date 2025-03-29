package com.samares_engineering.omf.omf_core_framework.genarchimodel.generator

import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_core_framework.genarchimodel.OMFMBSWProfile
import com.samares_engineering.omf.omf_core_framework.genarchimodel.generator.ModelArchitectureGenerator.Companion.generatedPackage
import com.samares_engineering.omf.omf_core_framework.genarchimodel.generator.PluginArchitectureFactory.getClassName
import java.io.FileNotFoundException

class ModelElementManager {

    private val architectureFactory: PluginArchitectureFactory
        get() = PluginArchitectureFactory
    private val profile: OMFMBSWProfile
        get() = OMFMBSWProfile.getInstance()

    val mapClassNameElement: MutableMap<String, Element> = mutableMapOf()
    val mapElementClass: MutableMap<Element, java.lang.Class<*>> = mutableMapOf()



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

        mapClassNameElement[packageName] = createdPackage

        return createdPackage
    }

    fun findOrCreateClass(
        ownerClass: String,
        className: String
    ): Classifier {
        return findOrCreateClass(findOrCreatePackage(ownerClass)!!, className)
    }

    fun findOrCreateClass(
        ownerClass: String,
        clazz: java.lang.Class<*>
    ): Classifier {
        val createdElement = findOrCreateClass(findOrCreatePackage(ownerClass)!!, clazz)
        mapElementClass[createdElement] = clazz
        return createdElement
    }

    fun findOrCreateClass(
        owner: Element,
        className: String,
        isEnum: Boolean = false,
        isAnnotation: Boolean = false,
        isInterface: Boolean = false
    ): Classifier {
        // Try to find the class by name under the given owner
        val existingType = findExistingClass(owner, className)
        
        if (existingType != null) {
            return existingType
        }
        // Apply stereotypes
        val createdElement = createAccordingClassifier(owner, className, isEnum, isAnnotation, isInterface)
        return createdElement

    }
    fun findOrCreateClass(
        owner: Element,
        clazz: java.lang.Class<*>,
    ): Classifier {
        // Try to find the class by name under the given owner
        val existingType = findExistingClass(owner, clazz)

        if (existingType != null) {
            return existingType
        }
        // Apply stereotypes
        val isEnum = clazz.isEnum
        val isAnnotation = clazz.isAnnotation
        val isInterface = clazz.isInterface
        val createdElement = createAccordingClassifier(owner, getClassName(clazz), isEnum, isAnnotation, isInterface)
        return createdElement
    }

    private fun findExistingClass(
        owner: Element,
        className: String
    ) = owner.ownedElement?.filterIsInstance<Class>()
        ?.firstOrNull { it.name == className }

    private fun findExistingClass(
        owner: Element,
        clazz: java.lang.Class<*>
    ) = owner.ownedElement?.filterIsInstance<Class>()
//        ?.firstOrNull { it.name == getClassName(clazz)  && architectureFactory.areNamespacesEqual(it, clazz.packageName) }
        ?.firstOrNull { it.name == getClassName(clazz) || it.name == buildParameterizedTypeName(clazz) }




    private fun createAccordingClassifier(
        owner: Element,
        className: String,
        isEnum: Boolean,
        isAnnotation: Boolean,
        isInterface: Boolean
    ): Classifier {
        val createdElement = when {
            isEnum -> {
                PluginArchitectureFactory.createEnumeration(owner, className)
            }

            isAnnotation -> {
                PluginArchitectureFactory.createAnnotation(owner, className)
            }

            isInterface -> {
                PluginArchitectureFactory.createInterface(owner, className)
            }

            else -> {
                PluginArchitectureFactory.createCodeClass(owner, className)
            }
        }
        mapClassNameElement[className] = createdElement
        return createdElement
    }


    fun buildParameterizedTypeName(typeInfo: TypeInfo): String {
        val rawTypeName = typeInfo.rawType?.simpleName ?: "Unknown"
        if (typeInfo.typeArguments.isEmpty()) {
            return rawTypeName
        }
        val typeArgsNames = typeInfo.typeArguments.joinToString(", ") { buildParameterizedTypeName(it) }
        return "$rawTypeName<$typeArgsNames>"
    }

    fun buildParameterizedTypeName(clazz: java.lang.Class<*>): String {
        val typeInfo = TypeInfo(clazz)
        return buildParameterizedTypeName(typeInfo)
    }

    fun findOrCreateEnumeration(
        ownerPackage: Package,
        enumName: String,
        enumLiterals: List<String> = emptyList()
    ): Any {
        val existingEnum =
            ownerPackage.ownedElement?.filterIsInstance<Enumeration>()?.firstOrNull { it.name == enumName }
        if (existingEnum != null) {
            return existingEnum
        }

        val newEnum = PluginArchitectureFactory.createEnumeration(ownerPackage, enumName, enumLiterals)

        mapClassNameElement[enumName] = newEnum
        return newEnum
    }

    fun applyInheritedStereotypes(clazz: Class, superclass: java.lang.Class<*>) {
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



    fun setFeatureAssociations(clazz: Classifier?) {
        if (clazz == null) return

        // Find associated elements (Hooks, UIActions, LiveActions, Options)
        val hooks = mutableListOf<Element>()
        val uiActions = mutableListOf<Element>()
        val liveActions = mutableListOf<Element>()
        val options = mutableListOf<Element>()

        // For simplicity, let's assume that the owned attributes or operations can be associated elements
        clazz.ownedElement
            .filterIsInstance<Property>()
            .forEach { attribute ->
                val type = attribute.type
                if (type != null) {
                    when {
                        PluginArchitectureFactory.profile.hook().`is`(type) -> hooks.add(type)
                        PluginArchitectureFactory.profile.uiAction().`is`(type) -> uiActions.add(type)
                        PluginArchitectureFactory.profile.liveAction().`is`(type) -> liveActions.add(type)
                        PluginArchitectureFactory.profile.option().`is`(type) -> options.add(type)
                    }
                }
            }

        // Set the associations in the stereotype
        PluginArchitectureFactory.profile.feature().setHooks(clazz, hooks)
        PluginArchitectureFactory.profile.feature().setUiActions(clazz, uiActions)
        PluginArchitectureFactory.profile.feature().setLiveActions(clazz, liveActions)
        PluginArchitectureFactory.profile.feature().setOptions(clazz, options)
    }
    @Throws(FileNotFoundException::class)
    fun getClassFromClassifier(classifier: Classifier): java.lang.Class<*> {
        val className = classifier.name
        return mapElementClass[classifier] ?: throw IllegalArgumentException("Class not found")
    }


}

data class TypeInfo(
    val rawType: java.lang.Class<*>?,
    val typeArguments: List<TypeInfo> = emptyList()
)
