/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.live.creation;

import com.nomagic.magicdraw.sysml.util.SysMLProfile;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.factory.SysMLFactory;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule.ARule;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.options.SysMLBasicOptionHelper;
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker;

import java.beans.PropertyChangeEvent;

public class CreateAutoInterface_OnPortCreation extends ARule {
    @Override
    protected boolean eventMatches(PropertyChangeEvent evt) {
        boolean isActivated = ((SysMLBasicOptionHelper) getFeature().getEnvOptionsHelper()).isAutoInterfaceCreationActivated();
        if(!isActivated) return false;
       return new EventChecker()
                .isElementCreated()
                .isPort()
                .hasStereotype(Profile._getSysml().proxyPort().getStereotype())
                .test(evt);
    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        try {
            Port port = (Port) e.getSource();
            Class interfaceBlock = SysMLFactory.getInstance().createInterfaceBlock(port.getOwner());
            interfaceBlock.setName("TO RENAME");
            port.setType(interfaceBlock);

            Property flowProperty = SysMLFactory.getInstance().createFlowProperty(interfaceBlock);
            flowProperty.setName("TO RENAME");
            Profile._getSysml().flowProperty().setDirection(flowProperty, SysMLProfile.FlowDirectionKindEnum.OUT);

        }catch (Exception uncheckedException){
            OMFErrorHandler.handleException(uncheckedException);
        }

        return e;
    }

    @Override
    public void debug(Object o) {

    }


    @Override
    public boolean isBlocking() {
        return false;
    }
}
