package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.nomagic.uml2.impl.ElementsFactory
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.log.OMFLog
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.OMFMBSWProfile
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.PluginArchitectureProfile
import java.lang.reflect.Modifier
import java.lang.reflect.ParameterizedType

object PluginArchitectureFactory {

    val factory: ElementsFactory
        get() = OMFUtils.getProject().elementsFactory
    val profile: OMFMBSWProfile
        get() = OMFMBSWProfile.getInstance()


    fun createFunctionCall(classSrc: Class?, operation: Operation, parameterName: String?, parameterType: String?) {
        val functionCall = factory.createPropertyInstance()
        functionCall.owner = classSrc
        functionCall.name = operation.name
        PluginArchitectureProfile.getInstance().methods().apply(functionCall)
    }

    fun createParameter(operation: Operation?, parameterName: String?, kind: ParameterDirectionKind?) {
        createParameter(operation, parameterName, null, kind)
    }

    fun createParameter(
        operation: Operation?,
        parameterName: String?,
        parameterType: Type?,
        kind: ParameterDirectionKind?
    ) {
        val returnParameter = factory.createParameterInstance()
        returnParameter.name = parameterName
        returnParameter.direction = kind
        returnParameter.operation = operation
        returnParameter.owner = operation
        if (parameterType != null) returnParameter.type = parameterType
    }

    fun createOperation(owner: Element?, functionName: String): Operation {
        val operation = factory.createOperationInstance()
        operation.name = functionName
        operation.owner = owner
        profile.method().apply(operation)
        return operation
    }

    val mapGeneralizationSrcTarget: MutableMap<Classifier, Classifier> = mutableMapOf()
    fun createGeneralization(specificClass: Classifier, generalClass: Classifier): Generalization {
        val generalization = factory.createGeneralizationInstance()
        generalization.specific = specificClass
        generalization.general = generalClass
        generalization.owner = specificClass // Set the owner of the generalization
        mapGeneralizationSrcTarget[specificClass] = generalClass
        return generalization
    }

    fun createEnumeration(owner: Element, enumName: String, enumLiterals:List<String> = emptyList()): Enumeration {
        val enumeration = factory.createEnumerationInstance()
        enumeration.name = enumName
        enumeration.owner = owner
       profile.enumClass().apply(enumeration)

        setNameSpace(enumeration, owner as NamedElement)

        // Add EnumerationLiterals
        for (constant in enumLiterals) {
            val literal = factory.createEnumerationLiteralInstance()
            literal.name = constant
            literal.enumeration = enumeration
        }

        return enumeration
    }

    fun setNameSpace(
        element: Element,
        owner: NamedElement
    ) {
        val nameSpaceSTR = profile.wwithNameSpace()
        //if the owner is a nameSpaceSTR,the namespace is the ownerNameSpace + owner name else the namespace is the owner name
        val namespace = computeNameSpace(owner)

        nameSpaceSTR.setNamespace(element, namespace)
    }

    fun areNamespacesEqual(owner: Element, owner1: Element): Boolean {
        val nameSpaceSTR = profile.wwithNameSpace()
        val namespace = nameSpaceSTR.getNamespace(owner)
        val namespace1 = nameSpaceSTR.getNamespace(owner1)
        return namespace == namespace1
    }
    fun areNamespacesEqual(owner: Element, className: String): Boolean {
        val nameSpaceSTR = profile.wwithNameSpace()
        val namespace = nameSpaceSTR.getNamespace(owner)
        return namespace == className
    }

    private fun computeNameSpace(
        owner: NamedElement
    ): String {
        val nameSpaceSTR = profile.wwithNameSpace()
        val namespace = ("${nameSpaceSTR.getNamespace(owner)}.".takeIf { nameSpaceSTR.`is`(owner) } ?: "") + owner.name
        return namespace
    }

    fun createCodeClass(owner: Element, className: String): Class {
        val codeClass = factory.createClassInstance().apply {
            name = className
            profile.codeClass().apply(this)
            setNameSpace(this, owner as NamedElement)
        }
        codeClass.owner = owner
        return codeClass
    }


    fun createAnnotation(owner: Element, className: String): Class {
        val annotation = factory.createClassInstance().apply {
            name = className
            profile.annotation().apply(this)
            setNameSpace(this, owner as NamedElement)
        }
        annotation.owner = owner
        return annotation
    }

    fun setModifiers(operation: Operation, modifiers: Int) {
        operation.isStatic = Modifier.isStatic(modifiers)
        operation.isLeaf = Modifier.isFinal(modifiers)
        operation.isAbstract = Modifier.isAbstract(modifiers)
        setVisibility(operation, modifiers)

    }

    fun setVisibility(element: NamedElement, modifiers: Int) {
        element.visibility = when {
            Modifier.isPublic(modifiers) -> VisibilityKindEnum.PUBLIC
            Modifier.isProtected(modifiers) -> VisibilityKindEnum.PROTECTED
            Modifier.isPrivate(modifiers) -> VisibilityKindEnum.PRIVATE
            else -> VisibilityKindEnum.PACKAGE
        }
    }

    fun setModifiers(property: Property, modifiers: Int) {
        property.isStatic = Modifier.isStatic(modifiers)
        property.isReadOnly = Modifier.isFinal(modifiers)
        property.isDerived = Modifier.isAbstract(modifiers)
        property.isLeaf = Modifier.isFinal(modifiers)
        property.isOrdered = Modifier.isVolatile(modifiers)
        property.isUnique = Modifier.isTransient(modifiers)
    }

    fun applyClassTypeSTR(
        rawTypeClass: java.lang.Class<*>,
        rawTypeElement: Classifier
    ) {
        when {
            rawTypeClass.isEnum -> {
                profile.enumClass().apply(rawTypeElement)
            }

            rawTypeClass.isAnnotation -> {
                profile.annotation().apply(rawTypeElement)
            }

            else -> {
                profile.codeClass().apply(rawTypeElement)
            }
        }
    }

    fun getClassName(clazz: java.lang.Class<*>): String = clazz.simpleName + getGenericSimpleType(clazz)
    fun <T> getGenericSimpleType(clazz: java.lang.Class<T>): String {
        try {
            val genericSuperclass = clazz.genericSuperclass
            if (genericSuperclass is ParameterizedType) {
                val actualTypeArguments = genericSuperclass.actualTypeArguments
                val typeNames = actualTypeArguments.joinToString(", ") {
                    it.typeName.substringAfterLast('.')
                }
                return "<$typeNames>"
            }
            return ""
        } catch (e: Throwable) {
//            OMFLogger.errorToSystemConsole(
//                OMFLog().err("Error while getting generic simple type: ${clazz.name}").breakLine().err(
//                    OMFLog().text(e.message)
//                )
//            )
            return ""
        }
    }
}
