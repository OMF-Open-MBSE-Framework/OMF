package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator

import com.nomagic.uml2.ext.magicdraw.auxiliaryconstructs.mdmodels.Model
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.OMFMBSWProfile
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.ModelArchitectureGenerator.Companion.generatedPackage
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.PluginArchitectureFactory.factory
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.PluginArchitectureFactory.getClassName
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.PluginArchitectureFactory.profile
import org.bouncycastle.asn1.x500.style.RFC4519Style.owner
import java.io.FileNotFoundException
import java.lang.reflect.ParameterizedType

class ModelElementManager {

    private val architectureFactory: PluginArchitectureFactory
        get() = PluginArchitectureFactory
    private val profile: OMFMBSWProfile
        get() = OMFMBSWProfile.getInstance()

    val mapClassNameElement: MutableMap<String, Element> = mutableMapOf()
    val mapClassNameClass: MutableMap<String, Classifier> = mutableMapOf()



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
        return findOrCreateClass(findOrCreatePackage(ownerClass)!!, clazz)
    }

    fun findOrCreateClass(
        owner: Element,
        className: String,
        isEnum: Boolean = false,
        isAnnotation: Boolean = false
    ): Classifier {
        // Try to find the class by name under the given owner
        val existingType = findExistingClass(owner, className)
        
        if (existingType != null) {
            return existingType
        }
        // Apply stereotypes
        val createdElement = createAccordingClassifier(isEnum, owner, className, isAnnotation)
        return createdElement

    }
    fun findOrCreateClass(
        owner: Element,
        clazz: java.lang.Class<*>,
        isEnum: Boolean = false,
        isAnnotation: Boolean = false
    ): Classifier {
        // Try to find the class by name under the given owner
        val existingType = findExistingClass(owner, clazz)

        if (existingType != null) {
            return existingType
        }
        // Apply stereotypes
        val createdElement = createAccordingClassifier(isEnum, owner, getClassName(clazz), isAnnotation)
        return createdElement
    }

    private fun findExistingClass(
        owner: Element,
        className: String
    ) = owner.ownedElement?.filterIsInstance<Class>()
        ?.firstOrNull { it.name == className && architectureFactory.areNamespacesEqual(it, className) }

    private fun findExistingClass(
        owner: Element,
        clazz: java.lang.Class<*>
    ) = owner.ownedElement?.filterIsInstance<Class>()
        ?.firstOrNull { it.name == getClassName(clazz)  && architectureFactory.areNamespacesEqual(it, clazz.name) }




    private fun createAccordingClassifier(
        isEnum: Boolean,
        owner: Element,
        className: String,
        isAnnotation: Boolean
    ): Classifier {
        val createdElement = when {
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
        mapClassNameElement[className] = createdElement
        mapClassNameClass[className] = createdElement
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
        mapClassNameClass[enumName] = newEnum
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
        return mapClassNameClass[className]?.javaClass ?: throw IllegalArgumentException("Class not found")
    }


}

data class TypeInfo(
    val rawType: java.lang.Class<*>?,
    val typeArguments: List<TypeInfo> = emptyList()
)
