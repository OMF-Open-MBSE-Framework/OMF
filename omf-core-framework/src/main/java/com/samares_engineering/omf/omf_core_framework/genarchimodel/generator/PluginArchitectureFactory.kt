package com.samares_engineering.omf.omf_core_framework.genarchimodel.generator

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.nomagic.uml2.impl.ElementsFactory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_core_framework.genarchimodel.OMFMBSWProfile
import com.samares_engineering.omf.omf_core_framework.genarchimodel.PluginArchitectureProfile
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
    fun createGeneralization(specificClass: Classifier, generalClass: Classifier): Generalization? {
        if(specificClass == generalClass) return null
        if (mapGeneralizationSrcTarget[generalClass] == specificClass) return null
        val generalization = factory.createGeneralizationInstance()
        generalization.specific = specificClass
        generalization.general = generalClass
        generalization.owner = specificClass // Set the owner of the generalization
        mapGeneralizationSrcTarget[specificClass] = generalClass
        return generalization
    }

    fun createEnumeration(owner: Element, enumName: String, enumLiterals: List<String> = emptyList()): Enumeration {
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
        val nameSpaceSTR = profile.withNameSpace()
        //if the owner is a nameSpaceSTR,the namespace is the ownerNameSpace + owner name else the namespace is the owner name
        val namespace = computeNameSpace(owner)

        nameSpaceSTR.setNamespace(element, namespace)
    }

    fun areNamespacesEqual(owner: Element, owner1: Element): Boolean {
        val nameSpaceSTR = profile.withNameSpace()
        val namespace = nameSpaceSTR.getNamespace(owner)
        val namespace1 = nameSpaceSTR.getNamespace(owner1)
        return namespace == namespace1
    }

    fun areNamespacesEqual(owner: Element, nameSpaceClazz: String): Boolean {
        val nameSpaceSTR = profile.withNameSpace()
        val namespace = nameSpaceSTR.getNamespace(owner)
        return namespace == nameSpaceClazz
    }

    fun computeNameSpace(
        owner: NamedElement
    ): String {
        val nameSpaceSTR = profile.withNameSpace()
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

    fun createInterface(owner: Element, className: String): Classifier {
        val interfaceElement = factory.createInterfaceInstance().apply {
            name = className
            profile._interface().apply(this)
            setNameSpace(this, owner as NamedElement)
        }
        interfaceElement.owner = owner
        return interfaceElement

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


    fun createOption(featureElement: Classifier, option: Option): Property? {
        val optionElement = factory.createPropertyInstance().apply {
            name = option.property.name
            profile.option().apply(this)
            setNameSpace(this, featureElement as NamedElement)
        }
        optionElement.owner = featureElement
        return optionElement
    }

    fun applyUIAction(uiAction: Classifier) {
        profile.uiAction().apply(uiAction)
        val optActionPerformed = uiAction.ownedElement.filterIsInstance<Operation>().find { it.name == "actionToPerform" }
        if(optActionPerformed != null) profile.uiAction().setActionToPerformed(uiAction, optActionPerformed)
        val optCheckAvailability = uiAction.ownedElement.filterIsInstance<Operation>().find { it.name == "checkAvailability" }
        if(optCheckAvailability != null) profile.uiAction().setCheckAvaibility(uiAction, optCheckAvailability)

    }

    fun createHook(featureElement: Classifier, hook: Hook): Property? {
        val hookElement = factory.createPropertyInstance().apply {
            name = getClassName(hook.javaClass)
            profile.hook().apply(this)
            setNameSpace(this, featureElement as NamedElement)
        }
        hookElement.owner = featureElement
        return hookElement
    }
}

