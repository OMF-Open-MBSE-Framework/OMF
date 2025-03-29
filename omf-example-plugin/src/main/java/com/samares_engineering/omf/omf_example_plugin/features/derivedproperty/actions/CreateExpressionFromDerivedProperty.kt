package com.samares_engineering.omf.omf_example_plugin.features.derivedproperty.actions


import com.nomagic.magicdraw.evaluation.Evaluator
import com.nomagic.magicdraw.uml2.UML2MetaTypes
import com.nomagic.magicdraw.validation.ExpressionEvaluationHelper
import com.nomagic.uml2.UML2Constants
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ParameterDirectionKind
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.ParameterDirectionKindEnum
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Type
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.SysoutColorPrinter
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.BrowserAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DeactivateListener
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.DiagramAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.MDAction
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import com.samares_engineering.omf.omf_core_framework.utils.utils.UMLUtils

@DiagramAction
@BrowserAction
@DeactivateListener
@MDAction(actionName = "Create Expression from derivedProperty", category = "EXPLO")
class CreateExpressionFromDerivedProperty : AUIAction() {
    override fun checkAvailability(selectedElements: List<Element>): Boolean {
        if (OMFUtils.isProjectVoid()) return false

        return selectedElements.size == 1
    }

    override fun actionToPerform(selectedElements: List<Element>) {
        val element = selectedElements[0]
        val _getMagicDraw = Profile._getMagicDraw()
//        val factory = SysMLFactory.getInstance().magicDrawFactory
//        val behavior = factory.createOpaqueBehaviorInstance()
//        behavior.owner = element.owner
        val derivedProperties = element.appliedStereotype[1]._elementTaggedValue
            .map { it.owner }
            .filter { _getMagicDraw.customization().`is`(it) }
            .filterNotNull()
            .map { it.ownedElement }
            .flatten()
            .filter { _getMagicDraw.derivedPropertySpecification().`is`(it) }

        val expressions = derivedProperties
            .map { _getMagicDraw.derivedPropertySpecification().getExpression(it) }
            .flatten()
            .map { it as String }

//        val expression = factory.createOpaqueExpressionInstance()
//        expression.owner = behavior
//        behavior.body.add(expressions[1])
//        behavior.language.add("StructuredExpression")
//        factory.createParameterInstance().let {
//            it.name = "THIS_ELEMENT"
//            it.type = OMFUtils.getProject().getElementByID("_9_0_62a020a_1105704884807_371561_7741") as Type
//            it.direction = ParameterDirectionKindEnum.IN
//            it.owner = behavior
//        }
        val refGetValue = element.refGetValue((derivedProperties[0] as Property).name)

        SysoutColorPrinter.success(refGetValue.toString())
//        val result =
//            ExpressionEvaluationHelper.evaluate(behavior._opaqueExpressionOfBehavior as Collection<Any>?, element)

//        SysoutColorPrinter.print(result.toString())

    }


}
