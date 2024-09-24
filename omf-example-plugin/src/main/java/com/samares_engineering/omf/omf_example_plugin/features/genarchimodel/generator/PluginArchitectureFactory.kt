package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.generator

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.nomagic.uml2.impl.ElementsFactory
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.PluginArchitectureProfile

object PluginArchitectureFactory {

    val factory: ElementsFactory
        get() = OMFUtils.getProject().elementsFactory


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

    fun createEnumeration(ownerPackage: Package?, enumName: String, enumLiterals:List<String> = emptyList()): Any {
        val enumeration = factory.createEnumerationInstance()
        enumeration.name = enumName
        enumeration.owner = ownerPackage

        // Add EnumerationLiterals
        for (constant in enumLiterals) {
            val literal = factory.createEnumerationLiteralInstance()
            literal.name = constant.toString()
            literal.enumeration = enumeration
        }

        return enumeration
    }


}
