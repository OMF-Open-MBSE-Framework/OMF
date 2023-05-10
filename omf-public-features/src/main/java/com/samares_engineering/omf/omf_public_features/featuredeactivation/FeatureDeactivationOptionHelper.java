package com.samares_engineering.omf.omf_public_features.featuredeactivation;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

import java.util.Objects;

public class FeatureDeactivationOptionHelper extends EnvOptionsHelper {


    private final String GROUP_NAME;
    private String ID_ACTIVATE_AUTOMATION;

    protected FeatureDeactivationOptionHelper(MDFeature feature) {
        super(feature);
        APlugin plugin = feature.getPlugin();
        ID_ACTIVATE_AUTOMATION = "is Activate " + plugin.getName() + " Features:";
        GROUP_NAME = plugin.getName() + " Features";
    }

    public OptionImpl getActivationFeatureOption() {
        APlugin plugin = getFeature().getPlugin();

        return new OptionImpl(
                new BooleanProperty(ID_ACTIVATE_AUTOMATION, true),
                GROUP_NAME,
                plugin.getEnvironmentOptionsGroup(),
                OptionKind.Environment
        );
    }


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
}
