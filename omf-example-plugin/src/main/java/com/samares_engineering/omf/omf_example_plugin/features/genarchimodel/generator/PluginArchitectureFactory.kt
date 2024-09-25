package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.nomagic.uml2.impl.ElementsFactory
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.OMFMBSWProfile
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.PluginArchitectureProfile
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.PluginArchitectureFactory.factory
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.PluginArchitectureFactory.profile
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator.PluginArchitectureFactory.setNameSpace

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
        return operation
    }

    fun createGeneralization(source: Classifier, upperClass: Classifier): Generalization? {
        val generalization = factory.createGeneralizationInstance()
        generalization.specific = source
        generalization.general = upperClass
        generalization.owner = source // Set the owner of the generalization
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
        val namespace = ("${ nameSpaceSTR.getNamespace(owner)}.".takeIf { nameSpaceSTR.`is`(owner) } ?: "") + owner.name

        nameSpaceSTR.setNamespace(element, namespace)
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


}
