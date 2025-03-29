/*
 * Copyright (c) 2014 NoMagic, Inc. All Rights Reserved.
 */
package com.samares_engineering.omf.omf_example_plugin.features.elementspecification.actions

import com.nomagic.magicdraw.ui.dialogs.specifications.ISpecificationComponent
import com.nomagic.magicdraw.ui.dialogs.specifications.configurator.ISpecificationNodeConfigurator
import com.nomagic.magicdraw.ui.dialogs.specifications.tree.node.ConfigurableNodeFactory
import com.nomagic.magicdraw.ui.dialogs.specifications.tree.node.IConfigurableNode
import com.nomagic.magicdraw.ui.dialogs.specifications.tree.node.ISpecificationNode
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element
import java.beans.PropertyChangeEvent
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JLabel

/**
 * @author Martynas Lelevicius
 */
class SpecificationNodeConfigurator : ISpecificationNodeConfigurator {
    override fun configure(node: IConfigurableNode, element: Element) {
        val myNode = ConfigurableNodeFactory.createConfigurableNode(MyNode())
        node.insertNode(IConfigurableNode.DOCUMENTATION_HYPERLINKS, IConfigurableNode.Position.BEFORE, myNode)
        myNode.addNode(ConfigurableNodeFactory.createConfigurableNode(MyInnerSpecificationNode()))
    }

    private class MyNode : ISpecificationNode {
        override fun getID(): String {
            return "MY_NODE"
        }

        override fun getIcon(): Icon? {
            return null
        }

        override fun getText(): String {
            return "My Node"
        }

        override fun dispose() {
        }

        override fun createSpecificationComponent(element: Element): ISpecificationComponent {
            return MySpecificationComponent()
        }

        override fun propertyChanged(element: Element, event: PropertyChangeEvent) {
        }

        override fun updateNode(): Boolean {
            return false
        }
    }

    private class MySpecificationComponent : ISpecificationComponent {
        override fun getComponent(): JComponent {
            return JLabel("My Specification Component")
        }

        override fun propertyChanged(element: Element, event: PropertyChangeEvent) {
        }

        override fun updateComponent() {
        }

        override fun dispose() {
        }
    }

    private class MyInnerSpecificationNode : ISpecificationNode {
        override fun getID(): String {
            return "MY_INNER_NODE"
        }

        override fun getIcon(): Icon? {
            return null
        }

        override fun getText(): String {
            return "My Inner Node"
        }

        override fun dispose() {
        }

        override fun createSpecificationComponent(element: Element): ISpecificationComponent {
            return MyInnerSpecificationComponent()
        }

        override fun propertyChanged(element: Element, event: PropertyChangeEvent) {
        }

        override fun updateNode(): Boolean {
            return false
        }
    }

    private class MyInnerSpecificationComponent : ISpecificationComponent {
        override fun getComponent(): JComponent {
            return JLabel("My Inner Specification Component")
        }

        override fun propertyChanged(element: Element, event: PropertyChangeEvent) {
        }

        override fun updateComponent() {
        }

        override fun dispose() {
        }
    }
}
