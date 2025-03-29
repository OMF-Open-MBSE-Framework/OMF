/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.connection.actions;

import com.nomagic.magicdraw.openapi.uml.ModelElementsManager;
import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Element;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdinternalstructures.Connector;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.AUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.annotations.*;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.connections.DelegationConnection;

import java.util.List;

@DiagramAction
@BrowserAction
@MenuAction
@DeactivateListener
@MDAction(actionName = "Delegate", category = "", keyStroke = "alt D")
public class DelegateConnection extends AUIAction {
    @Override
    public boolean checkAvailability(List<Element> selectedElements) {
        return OMFUtils.getProject() != null && selectedElements.size() == 1 && selectedElements.get(0) instanceof Connector;
    }

    /**
     * Delegate the selected connection with the default algorithm.
     * @param selectedElements selected elements
     */
    @Override
    public void actionToPerform(List<Element> selectedElements) {
        try {
            Connector connector = (Connector) selectedElements.get(0);
            Class type = SysMLFactory.getInstance().createInterfaceBlock(connector.getOwner());
            type.setName("Speed");
            Property flowProperty = SysMLFactory.getInstance().createFlowProperty(type);
            flowProperty.setName("speed");
            new DelegationConnection(){
                @Override
                public Class createDefaultInterfaceBlock(Port port, Element srcInterfaceOwner, SysMLProfile.FlowDirectionKindEnum direction) {
                    return type;
                }

                @Override
                public Port createPort(String name, Class portOwner) {
                    Port port = SysMLFactory.getInstance().createProxyPort(portOwner);
                    port.setName("p1");
                    port.setType(type);
                    return port;
                }

                @Override
                public Class cloneConjugatedInterfaceBlockForDelegation(Port port, Class originalInterfaceBlock, Element interfaceOwner) {
                    return type;
                }

                @Override
                public Class cloneInterfaceBlockForDelegation(Port port, Class originalInterfaceBlock, Element interfaceOwner) {
                    return type;
                }

                @Override
                public void conjugatePortAndInterface(Port port, Class targetInterface) {
                    port.setConjugated(!port.isConjugated());
                }

                @Override
                public Port clonePortForDelegation(Port originalPort, Element Owner) {
                    Port delegatedPort = SysMLFactory.getInstance().createProxyPort(Owner);
                    delegatedPort.setConjugated(originalPort.isConjugated());
                    delegatedPort.setType(type);
                    return delegatedPort;
                }
            }.createConnectionWithDelegation(connector);

            ModelElementsManager.getInstance().removeElement(connector);
        } catch (Exception e) {
            OMFLogger.err(e);
        }
    }

}