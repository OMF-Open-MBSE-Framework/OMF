package com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.options;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;

import java.util.List;

/**
 * This class is used to manage the options of the SysML Basic feature
 * It declares the options and their default values, in the Group "OMF Features":
 * - [OMF FEATURES] - Activate auto InterfaceBlock creation when port created
 * And all getters and setters to manage these options:
 * - isAutoInterfaceCreationActivated()
 */
public class SysMLBasicOptionHelper extends EnvOptionsHelper {

    public static final String ACTIVATE_AUTO_INTERFACE_BLOCK_CREATION = "Activate auto InterfaceBlock creation when port created :";
    private String GROUP = "OMF Features";

    public SysMLBasicOptionHelper(MDFeature feature) {
        super(feature);
    }


    public List<IOption> getAllOptions() {
        BooleanProperty isInterfaceCreationActivated = new BooleanProperty(ACTIVATE_AUTO_INTERFACE_BLOCK_CREATION, false);

        OptionImpl isInterfaceCreationActivatedOption = new OptionImpl(
                isInterfaceCreationActivated,
                GROUP,
                getFeature().getPlugin().getEnvironmentOptionsGroup()
                        .orElseThrow(() -> new OMFFeatureRegisteringException("Environment options group not registered" +
                                "for plugin")),
                OptionKind.Environment
        );
        return List.of(isInterfaceCreationActivatedOption);
    }

    public boolean isAutoInterfaceCreationActivated() {
        return (boolean) getPropertyByName(ACTIVATE_AUTO_INTERFACE_BLOCK_CREATION).getValue();
    }
}
