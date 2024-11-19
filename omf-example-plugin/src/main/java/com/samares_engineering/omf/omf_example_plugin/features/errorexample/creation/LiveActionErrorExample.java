/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.errorexample.creation;

import com.nomagic.magicdraw.properties.Property;
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Class;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction;
import com.samares_engineering.omf.omf_core_framework.listeners.EventChecker;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_core_framework.utils.options.OptionsHelper;
import com.samares_engineering.omf.omf_example_plugin.features.errorexample.ErrorManagementFeatureExample;

import java.beans.PropertyChangeEvent;
import java.util.Optional;
import java.util.function.Predicate;

public class LiveActionErrorExample extends ALiveAction {
    @Override
    protected boolean eventMatches(PropertyChangeEvent evt) {
        if (isOptionDeactivated()) return false;
        Predicate<PropertyChangeEvent> isElementNamedFail = e -> e.getPropertyName().equals("fail");

        return new EventChecker()
                .isElementCreated()
                .isBlock()
                .isTrue(isElementNamedFail)
                .test(evt);
    }

    private boolean isOptionDeactivated() {
        Optional<OMFPropertyOptionsGroup> optOptionGroupID = getFeature().getPlugin()
                .getEnvironmentOptionsGroup();
        String optionGroupID = optOptionGroupID.isPresent()?
                optOptionGroupID.get().getID()
                : "NOT FOUND";

        Optional<Property> optOption = OptionsHelper.getEnvironmentOptionByID(optionGroupID, ErrorManagementFeatureExample.ACTIVATE_ERROR_LIVE_ACTION);
        return optOption.map(property -> !(boolean) property.getValue()).orElse(true);
    }


    @Override
    public PropertyChangeEvent process(PropertyChangeEvent e) {
        Class block = (Class) e.getSource();
        block.setName("succeed");
        throw new RuntimeException("This is a test error");
    }

    @Override
    public boolean isBlocking() {
        return false;
    }
}
