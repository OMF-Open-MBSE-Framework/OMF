/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.errorexample.creation;

import com.nomagic.magicdraw.properties.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule.ARule;
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.utils.options.OptionsHelper;
import com.samares_engineering.omf.omf_example_plugin.features.errorexample.ErrorManagementFeatureExample;

import java.beans.PropertyChangeEvent;
import java.util.Optional;

public class LiveActionErrorExample extends ARule {
    @Override
    protected boolean eventMatches(PropertyChangeEvent evt) {
        if (isOptionDeactivated()) return false;
        return new EventChecker()
                .isElementCreated()
                .isBlock()
                .test(evt);
    }

    private boolean isOptionDeactivated() {
        Optional<OMFPropertyOptionsGroup> optOptionGroupID = getFeature().getPlugin()
                .getEnvironmentOptionsGroup();
        String optionGroupID = optOptionGroupID.isPresent()?
                optOptionGroupID.get().getID()
                : "NOT FOUND";

        Optional<Property> optOption = OptionsHelper.getEnvironmentOptionByID(optionGroupID, ErrorManagementFeatureExample.ACTIVATE_ERROR_LIVE_ACTION);
        if(optOption.isEmpty()) return true;
        if (!(boolean) optOption.get().getValue()) return true;
        return false;
    }


    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        try {
            Class block = (Class) e.getSource();
            block.setName("succeed");
        }catch (Exception uncheckedException){
            OMFErrorHandler.handleException(uncheckedException);
        }

        return e;
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
