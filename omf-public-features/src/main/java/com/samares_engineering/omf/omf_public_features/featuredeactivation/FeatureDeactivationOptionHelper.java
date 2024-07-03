package com.samares_engineering.omf.omf_public_features.featuredeactivation;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;

import java.util.Objects;

public class FeatureDeactivationOptionHelper extends EnvOptionsHelper{


    private final String GROUP_NAME;
    private String ID_ACTIVATE_AUTOMATION;

    protected FeatureDeactivationOptionHelper(OMFFeature feature) {
        super(feature);
        OMFPlugin plugin = feature.getPlugin();
        ID_ACTIVATE_AUTOMATION = "activate automations " + plugin.getName() + " :";
        GROUP_NAME = plugin.getName() + "Features Configurations";
    }

    public OptionImpl getActivationFeatureOption() {
        OMFPlugin plugin = getFeature().getPlugin();

        return new OptionImpl(
                new BooleanProperty(ID_ACTIVATE_AUTOMATION, true),
                GROUP_NAME,
                plugin.getEnvironmentOptionsGroup()
                        .orElseThrow(() -> new FeatureRegisteringException(
                                "No environment options groups have been declared for this plugin")),
                OptionKind.Environment
        );
    }

    /**
     * @return true if the automations shall be activated, false otherwise
     */
    public boolean isActivateAutomationValue() {
        BooleanProperty p = (BooleanProperty) Objects.requireNonNull(getPropertyByName(ID_ACTIVATE_AUTOMATION), "");
        return p.getBoolean();
    }

    public void setAutomationsActivated(boolean isAutomationsActivated) {
        getPropertyByName(ID_ACTIVATE_AUTOMATION).setValue(isAutomationsActivated);
    }

    public void setDeactivateAutomationValue(boolean shallWizardBeTriggered) {
        getPropertyByName(ID_ACTIVATE_AUTOMATION).setValue(shallWizardBeTriggered);
    }

    public String getGROUP_NAME() {
        return GROUP_NAME;
    }

    public String getID_ACTIVATE_AUTOMATION() {
        return ID_ACTIVATE_AUTOMATION;
    }
}

