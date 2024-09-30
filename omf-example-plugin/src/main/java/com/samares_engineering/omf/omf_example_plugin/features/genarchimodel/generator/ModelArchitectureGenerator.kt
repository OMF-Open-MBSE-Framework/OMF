package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator

import com.google.common.reflect.ClassPath
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.OMFCriticalException
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.PluginArchitectureFactory.profile
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.PluginArchitectureFactory.setModifiers
import java.io.IOException
import java.lang.reflect.*
import kotlin.reflect.*
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.superclasses

class ModelArchitectureGenerator(val domain: String, val pathToFiles: String) {
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
    }



//    private fun generatePluginModel(pluginClassList: List<java.lang.Class<*>>) {
//        for (pluginClass in pluginClassList) {
//            if (shouldSkipClass(pluginClass)) continue
//
//            val packageName = pluginClass.packageName
//            val clazz = elementManager.findOrCreateClass(packageName, pluginClass.simpleName)
//            clazz.name = pluginClass.simpleName
//
//            try {
//                if (isKotlinClass(pluginClass)) {
//                    processKotlinClass(pluginClass, packageName, clazz)
//                } else {
//                    processJavaClass(pluginClass, packageName, clazz)
//                }
//            } catch (e: Exception) {
//                OMFLogger.err(e)
//            }
//        }
//    }

    // Helper methods

    fun shouldSkipClass(pluginClass: java.lang.Class<*>): Boolean {
        return pluginClass.isSynthetic || pluginClass.isAnonymousClass || pluginClass.isLocalClass
    }

//    private fun isKotlinClass(pluginClass: java.lang.Class<*>): Boolean {
//        return pluginClass.isAnnotationPresent(Metadata::class.java)
//    }

    // Process Kotlin classes

//    private fun processKotlinClass(
//        pluginClass: java.lang.Class<*>,
//        packageName: String,
//        clazz: Classifier?
//    ) {
//        val kClass = pluginClass.kotlin
//
//        if (shouldSkipKotlinClass(kClass)) return
//
//        processKotlinProperties(kClass, packageName, clazz)
//        processKotlinFunctions(kClass, packageName, clazz)
//        processKotlinSuperclasses(kClass, clazz)
//    }

    private fun shouldSkipKotlinClass(kClass: KClass<*>): Boolean {
        return kClass.isCompanion || kClass.isValue || kClass.isFun || kClass.java.isSynthetic
    }

//    private fun processKotlinProperties(
//        kClass: KClass<*>,
//        packageName: String,
//        clazz: Classifier?
//    ) {
//        for (property in kClass.memberProperties) {
//            try {
//                val attribute = createAttributeFromKotlinProperty(property, packageName, clazz)
//                if (attribute != null) {
//                    clazz?.ownedElement?.add(attribute)
//                }
//            } catch (e: Exception) {
//                OMFLogger.err(e)
//            }
//        }
//    }

//    private fun createAttributeFromKotlinProperty(
//        property: KProperty1<out Any, *>,
//        packageName: String,
//        clazz: Classifier?
//    ): Property? {
//        val propertyName = property.name
//        val attribute = SysMLFactory.getInstance().createProperty(clazz)
//        attribute.name = propertyName
//
//        val typeInfo = extractTypeInfoFromKType(property.returnType)
//        if (typeInfo == null || typeInfo.rawType == null) {
//            // Handle unresolved types
//            return null
//        }
//
//        val typeElement = createTypeElement(typeInfo, packageName)
//        attribute.type = typeElement
//
//        // Handle multiplicity if needed
//
//        return attribute
//    }
//
//
//
//    private fun processKotlinFunctions(
//        kClass: KClass<*>,
//        packageName: String,
//        clazz: Classifier?
//    ) {
//        for (function in kClass.memberFunctions) {
//            try {
//                val operation = createOperationFromKotlinFunction(function, packageName, clazz)
//                clazz?.ownedElement?.add(operation)
//            } catch (e: Exception) {
//                OMFLogger.err(e)
//            }
//        }
//    }

//    private fun createOperationFromKotlinFunction(
//        function: KFunction<*>,
//        packageName: String,
//        clazz: Classifier?
//    ): Operation {
//        val functionName = function.name
//
//        val operation = PluginArchitectureFactory.createOperation(clazz, functionName)
//
//        // Return type
//        val returnTypeName = getTypeNameFromKType(function.returnType)
//        PluginArchitectureFactory.createParameter(
//            operation,
//            "return",
//            elementManager.findOrCreateClass(packageName, returnTypeName),
//            ParameterDirectionKindEnum.RETURN
//        )
//
//        // Parameters
//        for (parameter in function.parameters) {
//            // Skip 'this' instance parameter in member functions
//            if (parameter.kind == KParameter.Kind.INSTANCE) continue
//
//            val parameterName = parameter.name ?: "param"
//            val parameterTypeName = getTypeNameFromKType(parameter.type)
//
//            PluginArchitectureFactory.createParameter(
//                operation,
//                parameterName,
//                elementManager.findOrCreateClass(packageName, parameterTypeName),
//                ParameterDirectionKindEnum.IN
//            )
//        }
//
//        return operation
//    }

//    private fun processKotlinSuperclasses(
//        kClass: KClass<*>,
//        clazz: Classifier?
//    ) {
//        for (superclassKClass in kClass.superclasses) {
//            if (superclassKClass != Any::class) {
//                try {
//                    val superclassName = superclassKClass.simpleName ?: "Unknown"
//                    val superclassPackageName = superclassKClass.java.packageName
//                    val upperClassPackage = elementManager.findOrCreatePackage(superclassPackageName)
//                    val upperClass = elementManager.findOrCreateClass(upperClassPackage!!, superclassName)
//                    PluginArchitectureFactory.createGeneralization(clazz!!, upperClass)
//
//                    // Apply stereotypes based on superclass
//                    if (clazz is Class) elementManager.applyInheritedStereotypes(clazz, superclassKClass.java)
//                } catch (e: Exception) {
//                    OMFLogger.err(e)
//                }
//            }
//        }
//    }

    // Process Java classes

//    private fun processJavaClass(
//        pluginClass: java.lang.Class<*>,
//        packageName: String,
//        clazz: Classifier?
//    ) {
//        if (pluginClass.isEnum) {
//            processJavaEnum(pluginClass, packageName)
//        }else if (pluginClass.isAnnotation) {
//            profile.annotation().apply(clazz)
//        } else {
//            processJavaFields(pluginClass, packageName, clazz)
//            processJavaMethods(pluginClass, packageName, clazz)
//            processJavaSuperclass(pluginClass, clazz)
//
//        }
//    }

    // Method to process Java Enums
//    private fun processJavaEnum(pluginClass: java.lang.Class<*>, packageName: String) {
//        val enumName = pluginClass.simpleName
//        val ownerPackage = elementManager.findOrCreatePackage(packageName)
//
//        // Create Enumeration
//        val enumConstants = pluginClass.enumConstants.map { it.toString() }
//        elementManager.findOrCreateEnumeration(ownerPackage!!, enumName, enumConstants)
//    }

//    private fun processJavaFields(
//        pluginClass: java.lang.Class<*>,
//        packageName: String,
//        clazz: Classifier?
//    ) {
//        for (field in pluginClass.declaredFields) {
//            try {
//                val attribute = createAttributeFromJavaField(field, packageName, clazz)
//                if (attribute != null) {
//                    clazz?.ownedElement?.add(attribute)
//                }
//            } catch (e: Exception) {
//                OMFLogger.err(e)
//            }
//        }
//    }
//
//    private fun createAttributeFromJavaField(
//        field: Field,
//        packageName: String,
//        clazz: Classifier?
//    ): Property? {
//        val fieldName = field.name
//        val attribute = SysMLFactory.getInstance().createProperty(clazz)
//        attribute.name = fieldName
//
//        val typeInfo = extractTypeInfoFromType(field.genericType)
//        if (typeInfo?.rawType == null) {
//            return null // Handle unresolved types
//        }
//
//        val typeElement = createTypeElement(typeInfo, packageName)
//        attribute.type = typeElement
//
//        // Handle multiplicity if needed
//
//        factory.setModifiers(attribute, field.modifiers)
//
//        return attribute
//    }





//    private fun processJavaMethods(
//        pluginClass: java.lang.Class<*>,
//        packageName: String,
//        clazz: Classifier?
//    ) {
//        for (method in pluginClass.declaredMethods) {
//            try {
//                val operation = createOperationFromJavaMethod(method, packageName, clazz)
//                clazz?.ownedElement?.add(operation)
//            } catch (e: Exception) {
//                OMFLogger.err(e)
//            }
//        }
//    }

//    private fun createOperationFromJavaMethod(
//        method: Method,
//        packageName: String,
//        clazz: Classifier?
//    ): Operation {
//        val methodName = method.name
//
//        val operation = PluginArchitectureFactory.createOperation(clazz, methodName)
//        profile.method().apply(operation)
//
//        setModifiers(operation, method.modifiers)
//
//        // Return type
//        val returnTypeInfo = extractTypeInfoFromType(method.genericReturnType)
//        if (returnTypeInfo?.rawType != null) {
//            val returnTypeElement = createTypeElement(returnTypeInfo, packageName)
//            PluginArchitectureFactory.createParameter(
//                operation,
//                "return",
//                returnTypeElement,
//                ParameterDirectionKindEnum.RETURN
//            )
//        }
//
//        // Parameters
//        for (parameter in method.parameters) {
//            val parameterName = parameter.name
//            val parameterTypeInfo = extractTypeInfoFromType(parameter.parameterizedType)
//            if (parameterTypeInfo?.rawType != null) {
//                val parameterTypeElement = createTypeElement(parameterTypeInfo, packageName)
//                PluginArchitectureFactory.createParameter(
//                    operation,
//                    parameterName,
//                    parameterTypeElement,
//                    ParameterDirectionKindEnum.IN
//                )
//            }
//        }
//
//        return operation
//    }
//
//    private fun processJavaSuperclass(
//        pluginClass: java.lang.Class<*>,
//        clazz: Classifier?
//    ) {
//        val superclass = pluginClass.superclass
//        if (superclass != null && superclass != Any::class.java) {
//            try {
//                val superclassName = superclass.simpleName
//                val superclassPackageName = superclass.packageName
//                val upperClassPackage = elementManager.findOrCreatePackage(superclassPackageName)
//                val upperClass = elementManager.findOrCreateClass(upperClassPackage!!, superclassName)
//                PluginArchitectureFactory.createGeneralization(clazz!!, upperClass)
//                // Apply stereotypes based on superclass
//                if (clazz is Class) elementManager.applyInheritedStereotypes(clazz, superclass)
//            } catch (e: Exception) {
//                OMFLogger.err(e)
//            }
//        }
//    }

    fun extractTypeInfoFromType(type: java.lang.reflect.Type): TypeInfo? {
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
//
//    private fun extractTypeInfoFromKType(kType: KType?): TypeInfo? {
//        if (kType == null) return null
//
//        val classifier = kType.classifier
//        return when (classifier) {
//            is KClass<*> -> {
//                val typeArgs = kType.arguments.mapNotNull { extractTypeInfoFromKType(it.type) }
//                TypeInfo(rawType = classifier.java, typeArguments = typeArgs)
//            }
//            is KTypeParameter -> {
//                // Handle type parameters if necessary
//                null // Or return a TypeInfo with a default type
//            }
//            else -> null
//        }
//    }
//
//
//    // Helper methods to extract type names and multiplicity
//    private fun getTypeNameAndMultiplicityFromKType(kType: KType): Triple<String, Multiplicity, String> {
//        val classifier = kType.classifier
//        var typeName = "Unknown"
//        var multiplicity = Multiplicity.ONE
//        var typePackageName = "default.package"
//
//        if (classifier is KClass<*>) {
//            typeName = classifier.simpleName ?: "Unknown"
//            typePackageName = classifier.java.packageName
//
//            if (Collection::class.java.isAssignableFrom(classifier.java)) {
//                multiplicity = Multiplicity.ZERO_OR_MORE
//
//                val typeArguments = kType.arguments
//                if (typeArguments.isNotEmpty()) {
//                    val argType = typeArguments[0].type
//                    val argClass = extractClassFromKType(argType)
//                    if (argClass != null) {
//                        typeName = argClass.simpleName ?: "Unknown"
//                        typePackageName = argClass.java.packageName
//                    } else {
//                        typeName = "Any"
//                        typePackageName = "default.package"
//                    }
//                }
//            }
//        }
//
//        return Triple(typeName, multiplicity, typePackageName)
//    }
//
//
//    private fun getTypeNameFromKType(kType: KType): String {
//        val classifier = kType.classifier
//        return if (classifier is KClass<*>) {
//            classifier.simpleName ?: "Unknown"
//        } else {
//            "Any"
//        }
//    }
//
//    private fun getTypeNameAndMultiplicityFromJavaType(
//        genericType: java.lang.reflect.Type,
//        rawType: java.lang.Class<*>
//    ): Triple<String, Multiplicity, String> {
//        var typeName = rawType.simpleName
//        var multiplicity = Multiplicity.ONE
//        var typePackageName = rawType.packageName
//
//        if (rawType.isArray) {
//            typeName = rawType.componentType.simpleName
//            typePackageName = rawType.componentType.packageName
//            multiplicity = Multiplicity.ZERO_OR_MORE
//        } else if (Collection::class.java.isAssignableFrom(rawType)) {
//            multiplicity = Multiplicity.ZERO_OR_MORE
//            if (genericType is ParameterizedType) {
//                val typeArguments = genericType.actualTypeArguments
//                if (typeArguments.isNotEmpty()) {
//                    val argType = typeArguments[0]
//                    val argClass = extractClassFromType(argType)
//                    if (argClass != null) {
//                        typeName = argClass.simpleName
//                        typePackageName = argClass.packageName
//                    } else {
//                        // Unresolved type variable; set default type and package
//                        typeName = "Any"
//                        typePackageName = "default.package"
//                    }
//                }
//            }
//        } else if (Map::class.java.isAssignableFrom(rawType)) {
//            multiplicity = Multiplicity.ZERO_OR_MORE
//            if (genericType is ParameterizedType) {
//                val typeArguments = genericType.actualTypeArguments
//                if (typeArguments.size >= 2) {
//                    val valueType = typeArguments[1]
//                    val valueClass = extractClassFromType(valueType)
//                    if (valueClass != null) {
//                        typeName = valueClass.simpleName
//                        typePackageName = valueClass.packageName
//                    } else {
//                        // Unresolved type variable; set default type and package
//                        typeName = "Any"
//                        typePackageName = "default.package"
//                    }
//                }
//            }
//        }
//
//        return Triple(typeName, multiplicity, typePackageName)
//    }
//
//    private fun getTypeNameFromJavaType(genericType: java.lang.reflect.Type, rawType: java.lang.Class<*>): String {
//        val typeName: String
//        if (genericType is ParameterizedType) {
//            val rawClass = extractClassFromType(genericType.rawType)
//            typeName = rawClass?.simpleName ?: "Unknown"
//        } else {
//            typeName = rawType.simpleName
//        }
//        return typeName
//    }
//
//    // Helper functions to extract Class<?> from Type
//
//    private fun extractClassFromType(type: java.lang.reflect.Type): java.lang.Class<*>? {
//        return when (type) {
//            is java.lang.Class<*> -> type
//            is ParameterizedType -> extractClassFromType(type.rawType)
//            is TypeVariable<*> -> {
//                // Return a default class or null
//                null // Or Object::class.java if you prefer
//            }
//            is WildcardType -> {
//                val upperBounds = type.upperBounds
//                if (upperBounds.isNotEmpty()) {
//                    extractClassFromType(upperBounds[0])
//                } else {
//                    null
//                }
//            }
//            else -> null
//        }
//    }

//    private fun extractClassFromKType(type: KType?): KClass<*>? {
//        if (type == null) return null
//
//        val classifier = type.classifier
//        return when (classifier) {
//            is KClass<*> -> classifier
//            is KTypeParameter -> null // Type parameter, cannot resolve
//            else -> null
//        }
//    }



    // Find or create class



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


//    private fun loadClassesFromLibraries(pluginClassLoader: ClassLoader?, classes: MutableList<java.lang.Class<*>>) {
//        // Load classes from associated libraries if needed
//        // Modify this section as per your project requirements
//        /*
//        val libraryDir = File("/path/to/library/dir") // Update this path accordingly
//        if (libraryDir.isDirectory) {
//            val libraryFiles = libraryDir.listFiles { _, name -> name.endsWith(".jar") }
//            if (libraryFiles != null) {
//                for (libraryFile in libraryFiles) {
//                    try {
//                        val libraryUrl = libraryFile.toURI().toURL()
//                        val libraryClassLoader = URLClassLoader(arrayOf(libraryUrl), pluginClassLoader)
//
//                        val libClassPath = ClassPath.from(libraryClassLoader)
//                        for (classInfo in libClassPath.allClasses) {
//                            val clazz = classInfo.load()
//                            if (clazz.name.startsWith(domain) && !shouldSkipClass(clazz)) {
//                                classes.add(clazz)
//                            }
//                        }
//                    } catch (e: MalformedURLException) {
//                        OMFLogger.err(e)
//                    } catch (e: IOException) {
//                        OMFLogger.err(e)
//                    }
//                }
//            }
//        }
//        */
//
//    }

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

        factory.applyClassTypeSTR(rawTypeClass, rawTypeElement)

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
        factory.createGeneralization(rawTypeElement, parameterizedTypeElement)

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