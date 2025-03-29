package com.samares_engineering.omf.omf_public_features.activablefeatureoption.options;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OptionNotFound;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;
import com.samares_engineering.omf.omf_public_features.activablefeatureoption.FeatureActivationFromOptionFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class is used to manage the options of the feature
 * It will create the options and update the feature status according to the option value
 */
public class FeatureActivationFromOption_OptionHelper extends EnvOptionsHelper {
    public static final String MANAGE_FEATURE_ACTIVATION = "Manage Feature Activation:";
    public static final String ACTIVATE_FEATURE_ = "Activate ";

    public FeatureActivationFromOption_OptionHelper(OMFFeature feature, OMFPropertyOptionsGroup featureManagerOptionGroup) {
        super(feature, featureManagerOptionGroup);
    }

    /**
     * This method is used to create the option to activate/deactivate a feature.
     * A listener is added to the option to trigger the feature registering/unregistering when the option value is changed.
     * @param feature the feature
     * @return the option
     */
    public OptionImpl createDeactivationOption(OMFFeature feature) {
        BooleanProperty isInterfaceCreationActivated = new BooleanProperty(
                getFeatureActivationPropertyName(feature),true);
        isInterfaceCreationActivated.setValue(feature.isRegistered());

        OptionImpl option = new OptionImpl(
                isInterfaceCreationActivated,
                MANAGE_FEATURE_ACTIVATION,
                getOptionGroup(),
                OptionKind.Environment
        );

        option.addListenerToRegister(new AOptionListener() {
            @Override
            public void updateByEnvironmentProperties(List<Property> list) {
                super.updateByEnvironmentProperties(list);
                Property optionProperty = option.getProperty();
                Optional<Property> optOption = list.stream()
                        .filter(property -> property.getID().equals(optionProperty.getID()))
                        .findFirst();
                if(optOption.isEmpty()) return;

                boolean shallBeRegistered = (boolean) optOption.get().getValue();
                getFeature().setFeatureActivation(feature, shallBeRegistered);
            }
        });


        return option;
    }


    /**
     * Get the registered option name for a feature
     * @param feature the feature
     * @return the option name
     */
    private String getFeatureActivationPropertyName(OMFFeature feature) {
        return ACTIVATE_FEATURE_ + feature.getName() + ":";
    }

    /**
     * Get the feature from an option
     * @param plugin the plugin
     * @param optionProperty the option
     * @return the feature
     */
    public Optional<OMFFeature> getFeatureFromOption(OMFPlugin plugin, Property optionProperty) {
        return plugin.getFeatures().stream()
                .filter(pluginFeature -> optionProperty.getID().contains(pluginFeature.getName()))
                .findFirst();
    }

    /**
     * Get all the options to register: one for each feature declared in the plugin except the current one
     * @return the options
     */
    public List<Property> getAllFeatureOptions() {
        List<OMFFeature> features = new ArrayList<>(getFeature().getPlugin().getFeatures());
        features.remove(getFeature());
        return features.stream()
                .map(this::getFeatureActivationPropertyName)
                .map(this::getPropertyByName)
                .collect(Collectors.toList());
    }

    @Override
    public FeatureActivationFromOptionFeature getFeature() {
        return (FeatureActivationFromOptionFeature) super.getFeature();
    }

    /**
     * Get the option from a feature
     * @param feature the feature
     * @return the option
     * @throws OptionNotFound if the option is not found
     */
    public Property getOptionFromFeature(OMFFeature feature) throws OptionNotFound {
        String optionName = getFeatureActivationPropertyName(feature);
        Property optionProperty = getPropertyByName(optionName);
        if (optionProperty == null) throw new OptionNotFound(optionName);
        return optionProperty;
    }
}
