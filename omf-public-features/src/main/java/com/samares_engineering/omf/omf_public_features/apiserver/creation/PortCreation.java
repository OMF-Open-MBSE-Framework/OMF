/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.apiserver.creation;

import com.nomagic.magicdraw.core.options.ProjectOptions;
import com.nomagic.magicdraw.properties.Property;
import com.nomagic.uml2.ext.magicdraw.compositestructures.mdports.Port;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule.ARule;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_public_features.apiserver.APIServerFeature;
import com.samares_engineering.omf.omf_public_features.stereotypes.utils.EventChecker;

import java.beans.PropertyChangeEvent;

public class PortCreation extends ARule {
    @Override
    protected boolean eventMatches(PropertyChangeEvent evt) {
       return new EventChecker()
                .isInstanceCreated()
                .isPort()
                .test(evt);
    }

    @Override
    public void debug(Object o) {

    }

    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        Port port = (Port) e.getSource();
        Property nameProperty = OMFUtils.currentProject.getOptions().
                getProperty(ProjectOptions.PROJECT_GENERAL_PROPERTIES, APIServerFeature.HELLO_PORT_ID);

        String name = nameProperty != null? (String) nameProperty.getValue() : "UNDEFINED";
        port.setName(name);
        return e;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
