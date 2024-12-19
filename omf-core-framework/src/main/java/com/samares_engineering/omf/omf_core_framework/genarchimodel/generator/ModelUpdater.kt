package com.samares_engineering.omf.omf_core_framework.genarchimodel.generator

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ParameterDirectionKindEnum
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Classifier
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Feature
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Operation
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.LiveAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option
import com.samares_engineering.omf.omf_core_framework.genarchimodel.generator.PluginArchitectureFactory.profile
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

class ModelUpdater(val generator: ModelArchitectureGenerator) {
    val elementManager: ModelElementManager
        get() = generator.elementManager

    val docImporter: JDocImporter
        get() = generator.docImporter

    val factory: PluginArchitectureFactory
        get() = PluginArchitectureFactory



    private val processedClasses: MutableSet<java.lang.Class<*>> = mutableSetOf()



    fun processClass(pluginClass: java.lang.Class<*>) {
         try {
             // Avoid processing the same class multiple times
             if (shouldSkipClass(pluginClass)) return
             processedClasses.add(pluginClass)

             val packageName = pluginClass.packageName
             val className = PluginArchitectureFactory.getClassName(pluginClass)
             val clazz = elementManager.findOrCreateClass(packageName, pluginClass)

             if(!pluginClass.name.contains(generator.domain)) return

             applyClassAttributes(pluginClass, clazz)

             // Process fields
             processFieldsClass(pluginClass, clazz)

             // Process methods
             processMethods(pluginClass, clazz)

             // Process inner classes
             processInnerClasses(pluginClass)

             // Process superclass
             processSuperClass(pluginClass, clazz)

             // Process interfaces
             processInterfaces(pluginClass, clazz)
         }catch (e: Throwable) {
//             OMFLogger.errorToSystemConsole(OMFLog().err("Error while treating class: ${pluginClass.name}").breakLine().err(OMFLog().text(e.message)))
         }
    }




    fun getGenericTypeFromField(instance: Any, fieldName: String): String {
        return try {
            val field = instance.javaClass.getDeclaredField(fieldName)
            val genericType = field.genericType
            if (genericType is ParameterizedType) {
                val actualTypeArguments = genericType.actualTypeArguments
                val typeNames = actualTypeArguments.joinToString(", ") { it.typeName }
                "${field.type.simpleName}<$typeNames>"
            } else {
                field.type.simpleName
            }
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
            ""
        }
    }

    private fun processInterfaces(
        pluginClass: Class<*>,
        clazz: Classifier
    ) {
        for (interfaceClass in pluginClass.interfaces) {
            processClass(interfaceClass)

            // Create realization
            val interfacePackage = interfaceClass.packageName
            val interfaceName = PluginArchitectureFactory.getClassName(interfaceClass)
            val interfaceElement = elementManager.findOrCreateClass(interfacePackage, interfaceClass)
            PluginArchitectureFactory.createGeneralization(clazz, interfaceElement)
        }
    }

    private fun processSuperClass(
        pluginClass: Class<*>,
        clazz: Classifier
    ) {
        val superclass = pluginClass.superclass
        if (superclass != null && superclass != Any::class.java) {
            processClass(superclass)

            // Create generalization
            val superclassPackage = superclass.packageName
            val superclassName = PluginArchitectureFactory.getClassName(superclass)
            val superclassElement = elementManager.findOrCreateClass(superclassPackage, superclass)
            PluginArchitectureFactory.createGeneralization(clazz, superclassElement)
        }
    }

    private fun processInnerClasses(pluginClass: Class<*>) {
        for (innerClass in pluginClass.declaredClasses) {
            processClass(innerClass)
            //TODO: use javaParser to get the inner class and documentation
        }
    }

    private fun processMethods(
        pluginClass: Class<*>,
        clazz: Classifier
    ) {
        for (method in pluginClass.declaredMethods) {
            processMethod(method, clazz)
        }
    }

    private fun processFieldsClass(
        pluginClass: Class<*>,
        clazz: Classifier
    ) {
        for (field in pluginClass.declaredFields) {
            processField(field, clazz)
        }
    }

    private fun shouldSkipClass(pluginClass: Class<*>): Boolean {
        if(isInvalidClass(pluginClass)) return true
        if (processedClasses.contains(pluginClass)) return true
        if (generator.shouldSkipClass(pluginClass)) return true

        return false
    }

    fun isInvalidClass(pluginClass: java.lang.Class<*>): Boolean {
        return try {
            pluginClass.declaringClass != null
            false
        } catch (e: NoClassDefFoundError) {
            true
        } catch (e: ClassNotFoundException) {
            true
        }
    }

    private fun applyClassAttributes(pluginClass: java.lang.Class<*>, clazz: Classifier) {
        try {
            // Apply stereotypes (Class, Interface, Enum, Annotation)
            PluginArchitectureFactory.applyClassTypeSTR(pluginClass, clazz)

            // Set isStatic if applicable
            PluginArchitectureFactory.setVisibility(clazz, pluginClass.modifiers)

            // Parse and set Javadoc
            parseClassJavaDoc(pluginClass, clazz)

            // Process annotations
            for (annotation in pluginClass.annotations) {
                processAnnotation(annotation, clazz)
            }

            // Apply stereotypes based on superclass
            val superclass = pluginClass.superclass
            if (superclass != null && superclass != Any::class.java) {
                applyInheritedStereotypes(clazz, superclass)
            }
        } catch (e: Throwable) {
            OMFLogger.errorToSystemConsole(OMFLog().err("Error while treating class attributes: ${pluginClass.name}").breakLine().err(OMFLog().text(e.message)))
        }
    }

    private fun processField(field: Field, clazz: Classifier) {
        val fieldName = field.name
        val attribute = SysMLFactory.getInstance().createProperty(clazz)
        attribute.name = fieldName

        // Process field type
        val typeInfo = generator.extractTypeInfoFromType(field.genericType)
        if (typeInfo?.rawType != null) {
            val typeElement = generator.createTypeElement(typeInfo, field.type.packageName)
            attribute.type = typeElement

            // Process the field type class recursively
            processClass(typeInfo.rawType)
        }

        // Set isStatic
        val isStatic = Modifier.isStatic(field.modifiers)
        profile.codeClass().apply(attribute)
        profile.codeClass().setIsStatic(attribute, isStatic)

        // Parse and set Javadoc
        parseFieldJavaDoc(field, attribute)

        // Process annotations
        for (annotation in field.annotations) {
            processAnnotation(annotation, attribute)
        }

        clazz.ownedElement?.add(attribute)
    }

    private fun processMethod(method: Method, clazz: Classifier) {
        val methodName = method.name
        val operation = PluginArchitectureFactory.createOperation(clazz, methodName)

        // Apply Method stereotype
        profile.method().apply(operation)

        // Set isStatic
        PluginArchitectureFactory.setModifiers(operation, method.modifiers)

        // Parse and set Javadoc
        parseMethodJavaDoc(clazz, method, operation)

        // Process annotations
        for (annotation in method.annotations) {
            processAnnotation(annotation, operation)
        }

        // Process return type
        val returnTypeInfo = generator.extractTypeInfoFromType(method.genericReturnType)
        if (returnTypeInfo?.rawType != null) {
            val returnTypeElement = generator.createTypeElement(returnTypeInfo, method.returnType.packageName)
            PluginArchitectureFactory.createParameter(
                operation,
                "return",
                returnTypeElement,
                ParameterDirectionKindEnum.RETURN
            )

            // Process the return type class recursively
            processClass(returnTypeInfo.rawType)
        }

        // Process parameters
        for (parameter in method.parameters) {
            val parameterName = parameter.name
            val parameterTypeInfo = generator.extractTypeInfoFromType(parameter.parameterizedType)
            if (parameterTypeInfo?.rawType != null) {
                val parameterTypeElement = generator.createTypeElement(parameterTypeInfo, parameter.type.packageName)
                PluginArchitectureFactory.createParameter(
                    operation,
                    parameterName,
                    parameterTypeElement,
                    ParameterDirectionKindEnum.IN
                )

                // Process the parameter type class recursively
                processClass(parameterTypeInfo.rawType)
            }

            // Process annotations on parameters
            for (annotation in parameter.annotations) {
                // Process the annotation
                processAnnotation(annotation, operation)
            }
        }

        clazz.ownedElement?.add(operation)
    }

    private fun processAnnotation(annotation: Annotation, element: Element) {

        val annotationClass = annotation.annotationClass.java
        val annotationName = annotationClass.simpleName

        // Process the annotation class recursively
        processClass(annotationClass)

        // Create a property typed by the annotation
        val annotationElement = elementManager.findOrCreateClass(annotationClass.packageName, annotationClass)
        val property = SysMLFactory.getInstance().createProperty(if((element is Classifier)) element else element.owner)
        property.name = "annotation:${annotationName}"
        property.type = annotationElement

        // Add the property to the element
        when (element) {
            is Classifier -> element.ownedElement.add(property)
            is Operation -> element.owner!!.ownedElement.add(property)
            is Property -> element.owner!!.ownedElement.add(property)
        }
    }
    private fun applyInheritedStereotypes(clazz: Classifier?, superclass: java.lang.Class<*>) {

           val superclassName = superclass.simpleName
            // Based on the superclass name, apply the appropriate stereotypes
        when {
            Hook::class.java.isAssignableFrom(superclass) -> profile.hook().apply(clazz)
            UIAction::class.java.isAssignableFrom(superclass) -> profile.uiAction().apply(clazz)
            LiveAction::class.java.isAssignableFrom(superclass) -> profile.liveAction().apply(clazz)
            Option::class.java.isAssignableFrom(superclass) -> profile
            OMFFeature::class.java.isAssignableFrom(superclass) -> profile.feature().apply(clazz)
        }

    }

    private fun parseClassJavaDoc(
        pluginClass: Class<*>,
        clazz: Classifier
    ) {
        val javadoc = docImporter.getClassJavadoc(pluginClass, generator.pathToFiles, generator.domain)
        if (javadoc != "") {
            profile.codeDoc().setCodedocumentation(clazz, javadoc)
        }
    }

    fun parseMethodJavaDoc(
        clazz: Classifier,
        method: Method,
        operation: Operation
    ) {
        val javadoc = docImporter.getMethodJavadoc(clazz, method, generator.pathToFiles, generator.domain)
        if (javadoc != "") {
            profile.codeDoc().setCodedocumentation(operation, javadoc)
//        }
        }
    }

    fun parseFieldJavaDoc(
        field: Field,
        attribute: Property?
    ) {
        val javadoc = getFieldJavadoc(field)
        if (javadoc != "") {
            profile.codeDoc().setCodedocumentation(attribute, javadoc)
        }
    }


    fun getFieldJavadoc(field: Field): String {
        // TODO: Implement method to get Javadoc comments of the field
        return ""
    }
}
