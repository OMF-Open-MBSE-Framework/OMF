package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.actions

import com.google.common.reflect.ClassPath
import com.nomagic.magicdraw.core.Application
import com.nomagic.magicdraw.uml.Finder
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type
import com.nomagic.uml2.impl.ElementsFactory
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import java.io.IOException
import java.lang.reflect.*
import kotlin.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses

class ModelArchitectureGenerator {

    private val magicdrawFactory: ElementsFactory
        get() = Application.getInstance().project!!.elementsFactory

    fun generateCodeModelArchitecture(owner: Element, domain: String) {
        generatedPackage = owner as Package
        val architectureOwner = SysMLFactory.getInstance().createBlock(owner)
        architectureOwner.name = "ArchitectureOwner"

        val pluginClasses = getAllPluginClasses(domain)
        generatePluginBDD(pluginClasses, architectureOwner)
    }

    private fun generatePluginBDD(pluginClassList: List<java.lang.Class<*>>, owner: Element?) {
        for (pluginClass in pluginClassList) {
            if (shouldSkipClass(pluginClass)) continue

            val packageName = pluginClass.packageName
            val clazz = findOrCreateClass(findOrCreatePackage(packageName), pluginClass.simpleName)
            clazz?.name = pluginClass.simpleName

            try {
                if (isKotlinClass(pluginClass)) {
                    processKotlinClass(pluginClass, packageName, clazz)
                } else {
                    processJavaClass(pluginClass, packageName, clazz)
                }
            } catch (e: Exception) {
                OMFLogger.err(e)
            }
        }
    }

    // Helper methods

    private fun shouldSkipClass(pluginClass: java.lang.Class<*>): Boolean {
        return pluginClass.isSynthetic || pluginClass.isAnonymousClass || pluginClass.isLocalClass
    }

    private fun isKotlinClass(pluginClass: java.lang.Class<*>): Boolean {
        return pluginClass.isAnnotationPresent(Metadata::class.java)
    }

    // Process Kotlin classes

    private fun processKotlinClass(
        pluginClass: java.lang.Class<*>,
        packageName: String,
        clazz: Class?
    ) {
        val kClass = pluginClass.kotlin

        if (shouldSkipKotlinClass(kClass)) return

        processKotlinProperties(kClass, packageName, clazz)
        processKotlinFunctions(kClass, packageName, clazz)
        processKotlinSuperclasses(kClass, packageName, clazz)
    }

    private fun shouldSkipKotlinClass(kClass: KClass<*>): Boolean {
        return kClass.isCompanion || kClass.isValue || kClass.isFun || kClass.java.isSynthetic
    }

    private fun processKotlinProperties(
        kClass: KClass<*>,
        packageName: String,
        clazz: Class?
    ) {
        for (property in kClass.memberProperties) {
            try {
                val attribute = createAttributeFromKotlinProperty(property, packageName, clazz)
                if (attribute != null) {
                    clazz?.ownedAttribute?.add(attribute)
                }
            } catch (e: Exception) {
                OMFLogger.err(e)
            }
        }
    }

    private fun createAttributeFromKotlinProperty(
        property: KProperty1<out Any, *>,
        packageName: String,
        clazz: Class?
    ): Property? {
        val propertyName = property.name
        val propertyType = property.returnType

        val attribute = SysMLFactory.getInstance().createProperty(clazz)
        attribute.name = propertyName

        val (typeName, multiplicity, typePackageName) = getTypeNameAndMultiplicityFromKType(propertyType)

        // Exclude 'kotlin.' or 'java.' packages if desired
        val finalPackageName = if (typePackageName.startsWith("kotlin.") || typePackageName.startsWith("java.")) {
            "DefaultPackage"
        } else {
            typePackageName
        }

        val type = findOrCreateClass(findOrCreatePackage(finalPackageName), typeName)
        if (type != null) {
            attribute.type = type
        } else {
            // Handle cases where type is null
            return null
        }

        // Handle multiplicity if needed
        return attribute
    }

    private fun processKotlinFunctions(
        kClass: KClass<*>,
        packageName: String,
        clazz: Class?
    ) {
        for (function in kClass.memberFunctions) {
            try {
                val operation = createOperationFromKotlinFunction(function, packageName, clazz)
                if (operation != null) {
                    clazz?.ownedOperation?.add(operation)
                }
            } catch (e: Exception) {
                OMFLogger.err(e)
            }
        }
    }

    private fun createOperationFromKotlinFunction(
        function: KFunction<*>,
        packageName: String,
        clazz: Class?
    ): Operation? {
        val functionName = function.name

        val operation = magicdrawFactory.createOperationInstance()
        operation.name = functionName
        operation.owner = clazz

        // Return type
        val returnTypeName = getTypeNameFromKType(function.returnType)
        PluginArchitectureFactory.getInstance().createParameter(
            operation,
            "return",
            findOrCreateClass(findOrCreatePackage(packageName), returnTypeName),
            ParameterDirectionKindEnum.RETURN
        )

        // Parameters
        for (parameter in function.parameters) {
            // Skip 'this' instance parameter in member functions
            if (parameter.kind == KParameter.Kind.INSTANCE) continue

            val parameterName = parameter.name ?: "param"
            val parameterTypeName = getTypeNameFromKType(parameter.type)

            PluginArchitectureFactory.getInstance().createParameter(
                operation,
                parameterName,
                findOrCreateClass(findOrCreatePackage(packageName), parameterTypeName),
                ParameterDirectionKindEnum.IN
            )
        }

        return operation
    }

    private fun processKotlinSuperclasses(
        kClass: KClass<*>,
        packageName: String,
        clazz: Class?
    ) {
        for (superclassKClass in kClass.superclasses) {
            if (superclassKClass != Any::class) {
                try {
                    val superclassName = superclassKClass.simpleName ?: "Unknown"
                    val superclassPackageName = superclassKClass.java.packageName
                    val upperClassPackage = findOrCreatePackage(superclassPackageName)
                    val upperClass = findOrCreateClass(upperClassPackage, superclassName)
                    val generalization = magicdrawFactory.createGeneralizationInstance()
                    generalization.specific = clazz
                    generalization.general = upperClass
                    generalization.owner = clazz // Set the owner of the generalization
                } catch (e: Exception) {
                    OMFLogger.err(e)
                }
            }
        }
    }

    // Process Java classes

    private fun processJavaClass(
        pluginClass: java.lang.Class<*>,
        packageName: String,
        clazz: Class?
    ) {
        if (pluginClass.isEnum) {
            processJavaEnum(pluginClass, packageName)
        } else {
            processJavaFields(pluginClass, packageName, clazz)
            processJavaMethods(pluginClass, packageName, clazz)
            processJavaSuperclass(pluginClass, packageName, clazz)
        }
    }

    // Method to process Java Enums
    private fun processJavaEnum(pluginClass: java.lang.Class<*>, packageName: String) {
        val enumName = pluginClass.simpleName
        val ownerPackage = findOrCreatePackage(packageName)

        // Create Enumeration
        val enumeration = magicdrawFactory.createEnumerationInstance()
        enumeration.name = enumName
        enumeration.owner = ownerPackage

        // Add EnumerationLiterals
        val enumConstants = pluginClass.enumConstants
        for (constant in enumConstants) {
            val literal = magicdrawFactory.createEnumerationLiteralInstance()
            literal.name = constant.toString()
            literal.enumeration = enumeration
        }
    }

    private fun processJavaFields(
        pluginClass: java.lang.Class<*>,
        packageName: String,
        clazz: Class?
    ) {
        for (field in pluginClass.declaredFields) {
            try {
                val attribute = createAttributeFromJavaField(field, packageName, clazz)
                if (attribute != null) {
                    clazz?.ownedAttribute?.add(attribute)
                }
            } catch (e: Exception) {
                OMFLogger.err(e)
            }
        }
    }

    private fun createAttributeFromJavaField(
        field: Field,
        packageName: String,
        clazz: Class?
    ): Property? {
        val fieldName = field.name
        val attribute = SysMLFactory.getInstance().createProperty(clazz)
        attribute.name = fieldName

        val (typeName, multiplicity, typePackageName) = getTypeNameAndMultiplicityFromJavaType(field.genericType, field.type)

        // Exclude 'java.' packages if desired
        val finalPackageName = if (typePackageName.startsWith("java.") || typePackageName.startsWith("javax.")) {
            "DefaultPackage" // Or skip creating the type
        } else {
            typePackageName
        }

        val type = findOrCreateClass(findOrCreatePackage(finalPackageName), typeName)
        if (type != null) {
            attribute.type = type
        } else {
            // Handle cases where type is null (e.g., unresolved types)
            // You can choose to skip adding this attribute or assign a default type
            return null
        }

        // Handle multiplicity if needed
        return attribute
    }

    private fun processJavaMethods(
        pluginClass: java.lang.Class<*>,
        packageName: String,
        clazz: Class?
    ) {
        for (method in pluginClass.declaredMethods) {
            try {
                val operation = createOperationFromJavaMethod(method, packageName, clazz)
                if (operation != null) {
                    clazz?.ownedOperation?.add(operation)
                }
            } catch (e: Exception) {
                OMFLogger.err(e)
            }
        }
    }

    private fun createOperationFromJavaMethod(
        method: Method,
        packageName: String,
        clazz: Class?
    ): Operation? {
        val methodName = method.name

        val operation = magicdrawFactory.createOperationInstance()
        operation.name = methodName
        operation.owner = clazz

        // Return type
        val returnTypeName = getTypeNameFromJavaType(method.genericReturnType, method.returnType)
        PluginArchitectureFactory.getInstance().createParameter(
            operation,
            "return",
            findOrCreateClass(findOrCreatePackage(packageName), returnTypeName),
            ParameterDirectionKindEnum.RETURN
        )

        // Parameters
        for (parameter in method.parameters) {
            val parameterName = parameter.name
            val parameterTypeName = getTypeNameFromJavaType(parameter.parameterizedType, parameter.type)

            PluginArchitectureFactory.getInstance().createParameter(
                operation,
                parameterName,
                findOrCreateClass(findOrCreatePackage(packageName), parameterTypeName),
                ParameterDirectionKindEnum.IN
            )
        }

        return operation
    }

    private fun processJavaSuperclass(
        pluginClass: java.lang.Class<*>,
        packageName: String,
        clazz: Class?
    ) {
        val superclass = pluginClass.superclass
        if (superclass != null && superclass != Any::class.java) {
            try {
                val superclassName = superclass.simpleName
                val superclassPackageName = superclass.packageName
                val upperClassPackage = findOrCreatePackage(superclassPackageName)
                val upperClass = findOrCreateClass(upperClassPackage, superclassName)
                val generalization = magicdrawFactory.createGeneralizationInstance()
                generalization.specific = clazz
                generalization.general = upperClass
                generalization.owner = clazz // Set the owner of the generalization
            } catch (e: Exception) {
                OMFLogger.err(e)
            }
        }
    }

    private fun extractTypeInfoFromType(type: java.lang.reflect.Type): TypeInfo? {
        return when (type) {
            is java.lang.Class<*> -> TypeInfo(rawType = type)
            is ParameterizedType -> {
                val rawType = type.rawType as? java.lang.Class<*>
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

    private fun extractTypeInfoFromKType(kType: KType?): TypeInfo? {
        if (kType == null) return null

        val classifier = kType.classifier
        return when (classifier) {
            is KClass<*> -> {
                val typeArgs = kType.arguments.mapNotNull { extractTypeInfoFromKType(it.type) }
                TypeInfo(rawType = classifier.java, typeArguments = typeArgs)
            }
            is KTypeParameter -> {
                // Handle type parameters if necessary
                null // Or return a TypeInfo with a default type
            }
            else -> null
        }
    }


    // Helper methods to extract type names and multiplicity
    private fun getTypeNameAndMultiplicityFromKType(kType: KType): Triple<String, Multiplicity, String> {
        val classifier = kType.classifier
        var typeName = "Unknown"
        var multiplicity = Multiplicity.ONE
        var typePackageName = "default.package"

        if (classifier is KClass<*>) {
            typeName = classifier.simpleName ?: "Unknown"
            typePackageName = classifier.java.packageName

            if (Collection::class.java.isAssignableFrom(classifier.java)) {
                multiplicity = Multiplicity.ZERO_OR_MORE

                val typeArguments = kType.arguments
                if (typeArguments.isNotEmpty()) {
                    val argType = typeArguments[0].type
                    val argClass = extractClassFromKType(argType)
                    if (argClass != null) {
                        typeName = argClass.simpleName ?: "Unknown"
                        typePackageName = argClass.java.packageName
                    } else {
                        typeName = "Any"
                        typePackageName = "default.package"
                    }
                }
            }
        }

        return Triple(typeName, multiplicity, typePackageName)
    }


    private fun getTypeNameFromKType(kType: KType): String {
        val classifier = kType.classifier
        return if (classifier is KClass<*>) {
            classifier.simpleName ?: "Unknown"
        } else {
            "Any"
        }
    }

    private fun getTypeNameAndMultiplicityFromJavaType(
        genericType: java.lang.reflect.Type,
        rawType: java.lang.Class<*>
    ): Triple<String, Multiplicity, String> {
        var typeName = rawType.simpleName
        var multiplicity = Multiplicity.ONE
        var typePackageName = rawType.packageName

        if (rawType.isArray) {
            typeName = rawType.componentType.simpleName
            typePackageName = rawType.componentType.packageName
            multiplicity = Multiplicity.ZERO_OR_MORE
        } else if (Collection::class.java.isAssignableFrom(rawType)) {
            multiplicity = Multiplicity.ZERO_OR_MORE
            if (genericType is ParameterizedType) {
                val typeArguments = genericType.actualTypeArguments
                if (typeArguments.isNotEmpty()) {
                    val argType = typeArguments[0]
                    val argClass = extractClassFromType(argType)
                    if (argClass != null) {
                        typeName = argClass.simpleName
                        typePackageName = argClass.packageName
                    } else {
                        // Unresolved type variable; set default type and package
                        typeName = "Any"
                        typePackageName = "default.package"
                    }
                }
            }
        } else if (Map::class.java.isAssignableFrom(rawType)) {
            multiplicity = Multiplicity.ZERO_OR_MORE
            if (genericType is ParameterizedType) {
                val typeArguments = genericType.actualTypeArguments
                if (typeArguments.size >= 2) {
                    val valueType = typeArguments[1]
                    val valueClass = extractClassFromType(valueType)
                    if (valueClass != null) {
                        typeName = valueClass.simpleName
                        typePackageName = valueClass.packageName
                    } else {
                        // Unresolved type variable; set default type and package
                        typeName = "Any"
                        typePackageName = "default.package"
                    }
                }
            }
        }

        return Triple(typeName, multiplicity, typePackageName)
    }

    private fun getTypeNameFromJavaType(genericType: java.lang.reflect.Type, rawType: java.lang.Class<*>): String {
        val typeName: String
        if (genericType is ParameterizedType) {
            val rawClass = extractClassFromType(genericType.rawType)
            typeName = rawClass?.simpleName ?: "Unknown"
        } else {
            typeName = rawType.simpleName
        }
        return typeName
    }

    // Helper functions to extract Class<?> from Type

    private fun extractClassFromType(type: java.lang.reflect.Type): java.lang.Class<*>? {
        return when (type) {
            is java.lang.Class<*> -> type
            is ParameterizedType -> extractClassFromType(type.rawType)
            is TypeVariable<*> -> {
                // Return a default class or null
                null // Or Object::class.java if you prefer
            }
            is WildcardType -> {
                val upperBounds = type.upperBounds
                if (upperBounds.isNotEmpty()) {
                    extractClassFromType(upperBounds[0])
                } else {
                    null
                }
            }
            else -> null
        }
    }

    private fun extractClassFromKType(type: KType?): KClass<*>? {
        if (type == null) return null

        val classifier = type.classifier
        return when (classifier) {
            is KClass<*> -> classifier
            is KTypeParameter -> null // Type parameter, cannot resolve
            else -> null
        }
    }

    // Find or create package

    private fun findOrCreatePackage(packageName: String): Package? {
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

    // Find or create class

    private fun findOrCreateClass(
        owner: Element?,
        className: String
    ): Class? {
        var type = Finder.byNameRecursively()
            .find<Type>(owner, Class::class.java, className)
        if (type == null) {
            type = SysMLFactory.getInstance().createBlock(owner)
            type.name = className
            type.owner = owner // Ensure owner is set correctly
        }
        return type as? Class
    }

    // Get all plugin classes

    fun getAllPluginClasses(domain: String): List<java.lang.Class<*>> {
        val classes: MutableList<java.lang.Class<*>> = ArrayList()

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


    private fun loadClassesFromLibraries(pluginClassLoader: ClassLoader?, classes: MutableList<java.lang.Class<*>>) {
        // Load classes from associated libraries if needed
        // Modify this section as per your project requirements
        /*
        val libraryDir = File("/path/to/library/dir") // Update this path accordingly
        if (libraryDir.isDirectory) {
            val libraryFiles = libraryDir.listFiles { _, name -> name.endsWith(".jar") }
            if (libraryFiles != null) {
                for (libraryFile in libraryFiles) {
                    try {
                        val libraryUrl = libraryFile.toURI().toURL()
                        val libraryClassLoader = URLClassLoader(arrayOf(libraryUrl), pluginClassLoader)

                        val libClassPath = ClassPath.from(libraryClassLoader)
                        for (classInfo in libClassPath.allClasses) {
                            val clazz = classInfo.load()
                            if (clazz.name.startsWith(domain) && !shouldSkipClass(clazz)) {
                                classes.add(clazz)
                            }
                        }
                    } catch (e: MalformedURLException) {
                        OMFLogger.err(e)
                    } catch (e: IOException) {
                        OMFLogger.err(e)
                    }
                }
            }
        }
        */

    }

    // Multiplicity enum

    private enum class Multiplicity {
        ONE, ZERO_OR_MORE
    }

    companion object {
        var generatedPackage: Package? = null
    }
}