package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric

import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.*
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.nomagic.magicdraw.openapi.uml.ModelElementsManager
import com.nomagic.magicdraw.openapi.uml.PresentationElementsManager
import com.nomagic.magicdraw.uml.symbols.DiagramPresentationElement
import com.nomagic.magicdraw.uml.DiagramTypeConstants
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile

class ParametricGenerator(
    private val importer: ExcelParametricImporter,
    private val owner: Class
) {

    fun generateParametric() {
        // Step 1: Generate ConstraintProperties
        generateConstraintProperties()

        // Step 2: Generate Connectors
        generateConnectors()

        // Step 3: Generate Diagram
        val diagram = generateDiagram()

        // Step 4: Display Elements
        displayElementsOnDiagram(diagram)
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

            if (constraintProperty != null && constraintBlock != null) {
                // Get constraint parameters
                val constraintParameters = constraintBlock.ownedAttribute.filter { Profile._getSysmlAdditionalStereotypes().constraintProperty().`is`(it)
                }

                for (parameter in constraintParameters) {
                    val parameterName = parameter.name
                    val equationParameter = constraintBean.mapParameterNameToEquationParameter[parameterName]

                    if (equationParameter != null) {
                        val targetElement = equationParameter.concreteElement

                        if (targetElement != null) {
                            // Create binding connector between constraint parameter and target element
                            createBindingConnector(
                                owner,
                                constraintProperty,
                                parameter,
                                targetElement
                            )
                        }
                    }
                }
            }
        }
    }

    private fun createBindingConnector(
        context: Classifier,
        constraintProperty: Property,
        constraintParameter: Property,
        targetElement: NamedElement
    ) {
        val connector = SysMLFactory.getInstance().createConnector(context)

        // Create ConnectorEnds
        val connectorEnd1 = connector.end.get(0)
        val connectorEnd2 = connector.end.get(1)

        // Set roles and parts
        connectorEnd1.role = constraintParameter
        connectorEnd1.partWithPort = constraintProperty

        connectorEnd2.role = targetElement as? Property

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

    private fun displayElementsOnDiagram(diagram: Diagram) {
        val presentationElementsManager = PresentationElementsManager.getInstance()
        val diagramPresentationElement = OMFUtils.getProject().getDiagram(diagram) as DiagramPresentationElement

        // Add properties to the diagram
        for (valuePropertyBean in importer.valuePropertyBeans) {
            val valueProperty = valuePropertyBean.concreteElement as? Property
            if (valueProperty != null) {
                presentationElementsManager.createShapeElement(valueProperty, diagramPresentationElement)
            }
        }

        // Add constraint properties to the diagram
        for (constraintBean in importer.constraintBeans) {
            val constraintProperty = constraintBean.constraintPropertyElement as? Property
            if (constraintProperty != null) {
                presentationElementsManager.createShapeElement(constraintProperty, diagramPresentationElement)
            }
        }

        // Add connectors to the diagram
//        for (connector in owner.ownedConnector) {
//            presentationElementsManager.createConnectorElement(connector, diagramPresentationElement)
//        }

        // Refresh the diagram to display all elements
        diagramPresentationElement.open()
//        diagramPresentationElement.diagramSurface.update()
    }
}
