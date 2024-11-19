/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.featuredeactivation;

import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.AOnMagicDrawStartHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This feature allows to deactivate all the features registered in OMF.
 * It registers an option in the environment options to activate or deactivate all the features.
 * By default, the feature List is all the registered features in the plugin.
 * It is possible to override this list by overriding the method getAllFeaturesToRegister() and getAllFeaturesToUnregistered().
 */
public class FeaturesDeactivationFeature extends SimpleFeature {

    private FeatureDeactivationOptionHelper featureDeactivationOptionHelper;

    public FeaturesDeactivationFeature(){
       super("FEATURE ACTIVATION MANAGEMENT");
    }

    @Override
    protected List<Hook> initLifeCycleHooks() {
        return List.of(new AOnMagicDrawStartHook() {
            @Override
            public void onMagicDrawStart() {
                boolean featureShallBeRegistered = ((FeatureDeactivationOptionHelper) getEnvOptionsHelper()).isActivateAutomationValue();
                if(!featureShallBeRegistered)
                    activateDeactivateAllFeatures(false);
            }
        });
    }


    /**
     * Registering the option and its listener to activate or deactivate all the features.
     */
    @Override
    public List<Option> initOptions() {
        featureDeactivationOptionHelper = new FeatureDeactivationOptionHelper(this);
        FeatureDeactivationOptionHelper envOptionsHelper = (FeatureDeactivationOptionHelper) getEnvOptionsHelper();
        AOption activationDeactivationOption = envOptionsHelper.getActivationFeatureOption();
        activationDeactivationOption.addListenerToRegister(new AOptionListener() {
            @Override
            public void updateByEnvironmentProperties(List<Property> list) {
                list.stream()
                        .filter(BooleanProperty.class::isInstance)
                        .map(BooleanProperty.class::cast)
                        .filter(opt -> opt.getID().equals(((FeatureDeactivationOptionHelper) getEnvOptionsHelper()).getID_ACTIVATE_AUTOMATION()))
                        .findFirst()
                        .ifPresent(opt -> activateDeactivateAllFeatures((Boolean) opt.getValue()));
            }
        });

        return List.of(
                activationDeactivationOption
        );
    }

    /**
     * Activate or deactivate all the features registered except this one.
     * @param featureShallBeRegistered true if the features shall be registered, false otherwise.
     */
    private void activateDeactivateAllFeatures(boolean featureShallBeRegistered) {
        AFeature deactivationFeature = this;
        Predicate<OMFFeature> exceptThisFeature = feature -> !(deactivationFeature.equals(feature));
        if (featureShallBeRegistered) {
            List<OMFFeature> features = getAllFeaturesToRegister().stream()
                    .filter(exceptThisFeature) // get all feature except this one
                    .collect(Collectors.toList());
            getPlugin().getFeatureRegisterer().registerFeatures(features);
        } else {
            List<OMFFeature> unregisteredFeatures = getAllFeaturesToUnregistered().stream()
                    .filter(exceptThisFeature)
                    .collect(Collectors.toList());
            getPlugin().getFeatureRegisterer().unregisterFeatures(unregisteredFeatures);
        }
    }

    /**
     * Override this method to change the list of features to unregister when the option is deactivated.
     * @return the list of features to unregister when the option is deactivated.
     */
    private List<OMFFeature> getAllFeaturesToUnregistered() {
        return getPlugin().getFeatureRegisterer().getRegisteredFeatures();
    }

    /**
     * Override this method to change the list of features to register when the option is activated.
     * @return the list of features to register when the option is activated.
     */
    private List<OMFFeature> getAllFeaturesToRegister() {
        return getPlugin().getFeatures();
    }



    @Override
    public EnvOptionsHelper getEnvOptionsHelper() {
        return featureDeactivationOptionHelper;
    }
}
