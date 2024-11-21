package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.importer

import com.nomagic.magicdraw.actions.ActionsID
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.LayoutManager

class ParametricGenerator(
    private val importer: ExcelParametricImporter,
    private val owner: Class
) {
    lateinit var diagram: Diagram

    fun generateParametric() {
        // Step 1: Generate ConstraintProperties
        generateConstraintProperties()

        // Step 2: Generate Connectors
        generateConnectors()

        // Step 3: Generate Diagram
        diagram = generateDiagram()


    }

    fun displayElements(): LayoutManager {
        // Step 4: Display Elements
        return displayElementsOnDiagram(diagram)
    }

    private fun generateConstraintProperties() {
        // For each ConstraintBean, create a ConstraintProperty in the owner context
        for (constraintBean in importer.constraintBeans) {
            val constraintBlock = constraintBean.concreteElement as? Class
            if (constraintBlock != null) {
                // Create ConstraintProperty
                val constraintProperty = SysMLFactory.getInstance().createConstraintProperty(owner)
                constraintProperty.name = constraintBean.name
                constraintProperty.type = constraintBlock

                // Update concreteElement in the bean
                constraintBean.constraintPropertyElement = constraintProperty
            }
        }
    }

    private fun generateConnectors() {
        // For each ConstraintBean, create connectors between its parameters and the corresponding properties
        for (constraintBean in importer.constraintBeans) {
            val constraintProperty = constraintBean.constraintPropertyElement as? Property
            val constraintBlock = constraintBean.concreteElement as? Class

            if (constraintProperty == null || constraintBlock == null) {
                OMFLogger.errorToSystemConsole("ConstraintProperty or ConstraintBlock not found for ${constraintBean.name}")
                continue
            }
                // Get constraint parameters
            val constraintParameters = constraintBlock.ownedAttribute
                .filter { Profile._getSysmlAdditionalStereotypes().constraintParameter().`is`(it)
            }

            for (parameter in constraintParameters) {
                val parameterName = parameter.name
                val equationParameter = constraintBean.mapParameterNameToEquationParameter[parameterName]
                if (equationParameter == null){
                   if(parameterName != constraintBean.name) { // skip output parameter, it is not in the map
                       OMFLogger.infoToSystemConsole("EquationParameter not found for $parameterName")
                   }
                    continue
                }

                val targetName = (equationParameter.concreteElement as NamedElement).name
                val targetParameterBean = constraintBean.mapParameterNameToEquationParameter[targetName]
                val targetElementConstraintProperty: Property?

                val targetElement:Property?

                if (targetParameterBean is ConstraintBean){
                    targetElementConstraintProperty = targetParameterBean.constraintPropertyElement
                    targetElement = targetParameterBean.outputParameter
                } else {
                    targetElementConstraintProperty = null
                    targetElement = targetParameterBean?.concreteElement as Property
                }
                if (targetElement == null){
                    OMFLogger.errorToSystemConsole("TargetElement not found for $targetName")
                    continue
                }

                // Create binding connector between constraint parameter and target element
                createBindingConnector(
                    owner,
                    constraintProperty,
                    parameter,
                    targetElementConstraintProperty,
                    targetElement
                )
            }

        }
    }

    private fun createBindingConnector(
        context: Classifier,
        constraintProperty: Property,
        constraintParameter: Property,
        targetElementConstraintProperty: Property?,
        targetElement: Property
    ) {
        val connector = SysMLFactory.getInstance().createConnector(context)

        // Create ConnectorEnds
        val connectorEnd1 = connector.end[0]
        val connectorEnd2 = connector.end[1]

        // Set roles and parts
        connectorEnd1.role = constraintParameter
        connectorEnd1.partWithPort = constraintProperty
        Profile._getSysml().nestedConnectorEnd().apply(connectorEnd1)
        Profile._getSysml().nestedConnectorEnd().setPropertyPath(connectorEnd1, listOf(constraintProperty))

        connectorEnd2.role = targetElement as? Property
        if (targetElementConstraintProperty != null) {
            connectorEnd2.partWithPort = targetElementConstraintProperty
            Profile._getSysml().nestedConnectorEnd().apply(connectorEnd2)
            Profile._getSysml().nestedConnectorEnd().setPropertyPath(connectorEnd2, listOf(targetElementConstraintProperty))
        }

        // Set the connector type to 'binding connector' stereotype
        Profile._getSysml().bindingConnector().apply(connector)
    }



    private fun generateDiagram(): Diagram {
        val diagramType = "SysML Parametric Diagram" // Use appropriate diagram type
        val diagramName = "Parametric Diagram"

        val diagram = ModelElementsManager.getInstance().createDiagram(diagramType, owner)
        diagram.name = diagramName

        return diagram
    }

    private fun displayElementsOnDiagram(diagram: Diagram): LayoutManager {
        val presentationElementsManager = PresentationElementsManager.getInstance()
        val diagramPresentationElement = OMFUtils.getProject().getDiagram(diagram) as DiagramPresentationElement
        val layoutManager = LayoutManager(diagramPresentationElement)
        // Add properties to the diagram
        for (valuePropertyBean in importer.valuePropertyBeans) {
            val valueProperty = valuePropertyBean.concreteElement as? Property
            if (valueProperty != null) {
                layoutManager.refreshPart(valueProperty)
            }
        }

        // Add constraint properties to the diagram
        for (constraintBean in importer.constraintBeans) {
            val constraintProperty = constraintBean.constraintPropertyElement as? Property

            if (constraintProperty != null) {
                val partPresentationElement = layoutManager.refreshPart(constraintProperty)
                layoutManager.refreshAllPorts(constraintProperty.type as Class, constraintProperty)
                ActionsID.QUICK_DIAGRAM_LAYOUT
            }


        }

        // Add connectors to the diagram
//        for (connector in owner.ownedConnector) {
//            presentationElementsManager.createConnectorElement(connector, diagramPresentationElement)
//        }

        // Refresh the diagram to display all elements
        layoutManager.displayAllPaths()
        diagramPresentationElement.open()

        return layoutManager
//        diagramPresentationElement.diagramSurface.update()
    }
}
