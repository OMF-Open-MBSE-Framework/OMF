/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.featuredeactivation;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.properties.BooleanProperty;
import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.feature.AFeature;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.AOptionListener;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This feature allows to deactivate all the features registered in OMF.
 * It registers an option in the environment options to activate or deactivate all the features.
 * By default, the feature List is all the registered features in the plugin.
 * It is possible to override this list by overriding the method getAllFeaturesToRegister() and getAllFeaturesToUnregistered().
 */
public class FeaturesDeactivationFeature extends AFeature {

    private FeatureDeactivationOptionHelper featureDeactivationOptionHelper;

    public FeaturesDeactivationFeature(){
       super("FEATURE ACTIVATION MANAGEMENT");
    }

    @Override
    public void onRegistering() {
        super.onRegistering();
        Application.getInstance().insertActivityAfterStartup(() -> {
            boolean featureShallBeRegistered = ((FeatureDeactivationOptionHelper) getEnvOptionsHelper()).isActivateAutomationValue();
            if(!featureShallBeRegistered)
                activateDeactivateAllFeatures(false);

        });
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return null;
    }

    @Override
    public List<IUIAction> initFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IUIAction> initProjectOnlyFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    public List<IRuleEngine> initLiveActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IRuleEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    /**
     * Registering the option and its listener to activate or deactivate all the features.
     */
    public List<IOption> initOptions() {
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

        return Arrays.asList(
                activationDeactivationOption
        );
    }

    /**
     * Activate or deactivate all the features registered except this one.
     * @param featureShallBeRegistered
     */
    private void activateDeactivateAllFeatures(boolean featureShallBeRegistered) {
        AFeature deactivationFeature = this;
        Predicate<MDFeature> exceptThisFeature = feature -> !(deactivationFeature.equals(feature));
        if (featureShallBeRegistered) {
            List<MDFeature> features = getAllFeaturesToRegister().stream()
                    .filter(exceptThisFeature) // get all feature except this one
                    .collect(Collectors.toList());
            getPlugin().getFeatureRegister().registerFeatures(features);
        } else {
            List<MDFeature> unregisteredFeatures = getAllFeaturesToUnregistered().stream()
                    .filter(exceptThisFeature)
                    .collect(Collectors.toList());
            getPlugin().getFeatureRegister().unregisterFeatures(unregisteredFeatures);
        }
    }

    /**
     * Override this method to change the list of features to unregister when the option is deactivated.
     * @return the list of features to unregister when the option is deactivated.
     */
    private List<MDFeature> getAllFeaturesToUnregistered() {
        return getPlugin().getFeatureRegister().getRegisteredFeatures();
    }

    /**
     * Override this method to change the list of features to register when the option is activated.
     * @return the list of features to register when the option is activated.
     */
    private List<MDFeature> getAllFeaturesToRegister() {
        return getPlugin().getFeatures();
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.emptyList();
    }

    @Override
    public EnvOptionsHelper getEnvOptionsHelper() {
        return featureDeactivationOptionHelper;
    }
}
