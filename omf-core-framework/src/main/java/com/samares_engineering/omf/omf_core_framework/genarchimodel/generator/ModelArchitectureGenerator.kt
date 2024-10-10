package com.samares_engineering.omf.omf_core_framework.genarchimodel.generator

import com.google.common.reflect.ClassPath
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin
import java.io.IOException
import java.lang.Class
import java.lang.reflect.*
import kotlin.reflect.*

class ModelArchitectureGenerator(val domain: String,
                                 val pathToFiles: String,
                                 val pluginClass: OMFPlugin? = null) {
    val elementManager = ModelElementManager()
    val updater: ModelUpdater = ModelUpdater(this)
    val docImporter = JDocImporter(elementManager)
    val factory: PluginArchitectureFactory
        get() = PluginArchitectureFactory

    fun generateCodeModelArchitecture(owner: Element) {
//        generatedPackage = owner as Package
//        val architectureOwner = SysMLFactory.getInstance().createBlock(owner)
//        architectureOwner.name = "ArchitectureOwner"
//
//        val pluginClasses = getAllPluginClasses(domain)
//        generatePluginModel(pluginClasses)
        generatedPackage = owner as Package

        val pluginClasses = getAllPluginClasses(domain)
        for (pluginClass in pluginClasses) {
            updater.processClass(pluginClass)
        }

        if (pluginClass != null) {
            processFeaturesItems(pluginClass)
        }
    }

    private fun processFeaturesItems(pluginClass: OMFPlugin) {
        val features = pluginClass.features
        features.forEach { treatOptions(it) }
        features.forEach { treatActions(it) }
        features.forEach { treatHooks(it) }
    }

    private fun treatOptions(feature: OMFFeature) {
        val featureElement = findFeatureElement(feature)
        feature.options.forEach {treatOption(it, featureElement)}
    }

    private fun treatOption(option: Option, featureElement: Classifier) {
        PluginArchitectureFactory.createOption(featureElement, option)
    }

    private fun treatActions(feature: OMFFeature) {
        val featureElement = findFeatureElement(feature)
        feature.uiActions.forEach {treatAction(it, featureElement)}
    }

    private fun treatAction(action: UIAction, featureElement: Classifier) {
        PluginArchitectureFactory.applyUIAction(
            elementManager.findOrCreateClass(
                action::class.java.packageName,
                action::class.java
            )
        )
    }


    private fun treatHooks(feature: OMFFeature) {
        val featureElement = findFeatureElement(feature)
        feature.lifeCycleHooks.forEach {treatHook(it, featureElement)}
    }

    private fun treatHook(hooks: Hook, featureElement: Classifier) {
        val hookElement =
            PluginArchitectureFactory.createHook(
                elementManager.findOrCreateClass(
                    hooks::class.java.packageName,
                    hooks::class.java
                ), hooks
            )
    }

    private fun findFeatureElement(feature: OMFFeature): Classifier {
        val featureClass:Class<*> = feature::class.java
        return elementManager.findOrCreateClass(featureClass.packageName, featureClass)
    }


    // Helper methods
    fun shouldSkipClass(pluginClass: Class<*>): Boolean {
        return pluginClass.isSynthetic || pluginClass.isAnonymousClass || pluginClass.isLocalClass
    }

    private fun shouldSkipKotlinClass(kClass: KClass<*>): Boolean {
        return kClass.isCompanion || kClass.isValue || kClass.isFun || kClass.java.isSynthetic
    }


    fun extractTypeInfoFromType(type: java.lang.reflect.Type): TypeInfo? {
        return when (type) {
            is Class<*> -> TypeInfo(rawType = type)
            is ParameterizedType -> {
                val rawType = type.rawType as? Class<*>
                val typeArgs = type.actualTypeArguments.mapNotNull { extractTypeInfoFromType(it) }
                TypeInfo(rawType = rawType, typeArguments = typeArgs)
            }
            is TypeVariable<*> -> {
                // Handle type variables if necessary
                null // Or return a TypeInfo with a default type
            }
            is WildcardType -> {
                val upperBounds = type.upperBounds
                if (upperBounds.isNotEmpty()) {
                    extractTypeInfoFromType(upperBounds[0])
                } else {
                    null
                }
            }
            else -> null
        }
    }


    // Get all plugin classes

    fun getAllPluginClasses(domain: String): List<Class<*>> {
        val classes: MutableList<Class<*>> = ArrayList()

        val pluginClassLoader = javaClass.classLoader

        try {
            val classPath = ClassPath.from(pluginClassLoader)
            for (classInfo in classPath.allClasses) {
                if (classInfo.name.startsWith(domain)) {
                    try {
                        val clazz = classInfo.load()
                        if (!shouldSkipClass(clazz) && !isExcludedPackage(clazz.packageName)) {
                            classes.add(clazz)
                        }
                    } catch (e: NoClassDefFoundError) {
                        OMFLogger.err("Class not found: ${classInfo.name}", OMFCriticalException("class not found: ${classInfo.name}", e))
                    } catch (e: ClassNotFoundException) {
                        OMFLogger.err("Class not found: ${classInfo.name}", OMFCriticalException("class not found: ${classInfo.name}", e))
                    } catch (e: Throwable) {
                        OMFLogger.err("Error loading class: ${classInfo.name}", OMFCriticalException("Error loading: ${classInfo.name}", e))
                    }
                }
            }
        } catch (e: IOException) {
            OMFLogger.err(e)
        }

        return classes
    }

    private fun isExcludedPackage(packageName: String): Boolean {
        // Exclude standard Java packages or any other packages as needed
        return packageName.startsWith("java.") || packageName.startsWith("javax.")
    }



    fun createTypeElement(
        typeInfo: TypeInfo,
        defaultPackageName: String
    ): Type? {
        val rawTypeClass = typeInfo.rawType ?: return null
        val typeName = rawTypeClass.simpleName
        val packageName = rawTypeClass.packageName

        // Create or find the raw type in the model
        val rawTypeElement = elementManager.findOrCreateClass(packageName, rawTypeClass)

        // If there are no type arguments, return the raw type
        if (typeInfo.typeArguments.isEmpty()) {
            return rawTypeElement
        }

        // Create a new Classifier to represent the parameterized type
        val parameterizedTypeName = elementManager.buildParameterizedTypeName(typeInfo)
        val parameterizedTypeElement = elementManager.findOrCreateClass(defaultPackageName, parameterizedTypeName)

        PluginArchitectureFactory.applyClassTypeSTR(rawTypeClass, rawTypeElement)

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
        PluginArchitectureFactory.createGeneralization(rawTypeElement, parameterizedTypeElement)

        return parameterizedTypeElement
    }
//
//
//
//    // Multiplicity enum
//
//    private enum class Multiplicity {
//        ONE, ZERO_OR_MORE
//    }

    companion object {
        var generatedPackage: Package? = null
    }
}