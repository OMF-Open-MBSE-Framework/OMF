package com.samares_engineering.omf.omf_example_plugin.features.deactivablefeatureoption;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeactivateFeatureFromOption_OptionHelper extends EnvOptionsHelper {

    public static final String MANAGE_FEATURE_ACTIVATION = "Manage Feature Activation:";
    public static final String ACTIVATE_FEATURE_ = "Activate ";

    public DeactivateFeatureFromOption_OptionHelper(MDFeature feature, OMFPropertyOptionsGroup featureManagerOptionGroup) {
        super(feature, featureManagerOptionGroup);
    }

//    public boolean isAutoInterfaceCreationActivated() {
//        return (boolean) getPropertyByName(MANAGE_FEATURE_ACTIVATION).getValue();
//    }

    public OptionImpl createDeactivationOption(MDFeature feature) {
        BooleanProperty isInterfaceCreationActivated = new BooleanProperty(getFeatureActivationPropertyName(feature), feature.isRegistered());

        OptionImpl option = new OptionImpl(
                isInterfaceCreationActivated,
                MANAGE_FEATURE_ACTIVATION,
                getOptionGroup(),
                OptionKind.Environment
        );
//        option.setOptionCategory(featureManagerOptionGroup);

        option.addListenerToRegister(new AOptionListener() {
            @Override
            public void updateByEnvironmentProperties(List<Property> list) {
                super.updateByEnvironmentProperties(list);
                Property optionProperty = option.getProperty();
                Optional<Property> optOption = list.stream()
                        .filter(property -> property.getID().equals(optionProperty.getID()))
                        .findFirst();
                if(optOption.isEmpty()) return;

//                APlugin plugin = getFeature().getPlugin();
//                Optional<MDFeature> optFeature = getFeatureFromOption(plugin, optionProperty);

//                if(optFeature.isEmpty()) return;//TODO: throw exception

                boolean shallBeRegistered = (boolean) optOption.get().getValue();
//                MDFeature feature1 = optFeature.get();
                getFeature().setFeatureActivation(feature, shallBeRegistered);
            }
        });

        return option;
    }



    private String getFeatureActivationPropertyName(MDFeature feature) {
        return ACTIVATE_FEATURE_ + feature.getName();
    }

    Optional<MDFeature> getFeatureFromOption(APlugin plugin, Property optionProperty) {
        return plugin.getFeatures().stream()
                .filter(pluginFeature -> optionProperty.getID().contains(pluginFeature.getName()))
                .findFirst();
    }
    public List<Property> getAllFeatureOptions() {
        return getFeature().getPlugin().getFeatures().stream()
                .map(this::getFeatureActivationPropertyName)
                .map(this::getPropertyByName)
                .collect(Collectors.toList());
    }

    @Override
    public DeactivateFeatureFromOption getFeature() {
        return (DeactivateFeatureFromOption) super.getFeature();
    }
}
