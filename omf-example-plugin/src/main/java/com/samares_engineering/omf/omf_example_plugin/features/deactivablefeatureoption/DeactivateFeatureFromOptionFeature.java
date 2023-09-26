/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.deactivablefeatureoption;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_example_plugin.features.deactivablefeatureoption.options.DeactivateFeatureFromOption_OptionHelper;
import com.samares_engineering.omf.omf_example_plugin.features.deactivablefeatureoption.options.FeatureManagerOptionGroup;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This feature is used to manage the features of the plugin.
 * It will register a dedicated group of options in the Environment Options.
 * It creates the options to activate/deactivate the features and update the feature status according to the option value.
 */
public class DeactivateFeatureFromOptionFeature extends SimpleFeature {
    private final FeatureManagerOptionGroup featureManagerOptionGroup;
    private final FeatureRegisteringListener featureRegisteringListener;


    public DeactivateFeatureFromOptionFeature() {
        super( "Deactivate Features from Options Feature");
        this.featureManagerOptionGroup = new FeatureManagerOptionGroup("Manage registered Features "
                , "Manage registered Features");
        this.featureRegisteringListener = new FeatureRegisteringListener(this);
    }



    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return new DeactivateFeatureFromOption_OptionHelper(this, getFeatureManagerOptionGroup());
    }

    /**
     * This method is called when the plugin is registered.
     * It will add the group of options in the Environment Options if it is not already present.
     * And call the method to initialize the synchronization between the options and the features.
     * see {@link #initSynchroWithEnvOptions()}
     */
    @Override
    public void onRegistering() {
        super.onRegistering();
        EnvironmentOptions options = Application.getInstance().getEnvironmentOptions();
        if(options.getGroup(featureManagerOptionGroup.ID) == null)
            options.addGroup(featureManagerOptionGroup);

        Application.getInstance().insertActivityAfterStartup(this::initSynchroWithEnvOptions);

    }

    /**
     * This method is used to initialize the synchronization between the options and the features.
     * It will actualise the feature status (registering or unregistered) them according to the option value.
     * And register a listener to the feature registering/unregistering to update the option value accordingly.
     */
    private void initSynchroWithEnvOptions() {
        refreshFeatureRegisteringFromEnvOptions();
        registerListener(featureRegisteringListener);
    }

    /**
     * This method is used to refresh the feature status (registering or unregistered) them according to the option value.
     */
    private void refreshFeatureRegisteringFromEnvOptions() {
        getEnvOptionsHelper().getAllFeatureOptions().forEach(optionProperty ->
            getEnvOptionsHelper().getFeatureFromOption(getPlugin(), optionProperty)
                    .ifPresent(feature -> setFeatureActivation(feature, (boolean) optionProperty.getValue())));


    }

    /**
     * This method is used to register a listener to the feature registering/unregistering to update the option value accordingly.
     * @param featureRegisteringListener the listener
     */
    private void registerListener(FeatureRegisteringListener featureRegisteringListener) {
        getFeatureRegister().getEventHandler().addPropertyChangeListener(featureRegisteringListener);
    }

    @Override
    protected List<IOption> initOptions() {
        DeactivateFeatureFromOption_OptionHelper envOptionsHelper = getEnvOptionsHelper();
        return getPlugin().getFeatures()
                .stream()
                .map(envOptionsHelper::createDeactivationOption)
                .collect(Collectors.toList());
    }


    @Override
    public DeactivateFeatureFromOption_OptionHelper getEnvOptionsHelper() {
        return (DeactivateFeatureFromOption_OptionHelper) super.getEnvOptionsHelper();
    }

    public FeatureManagerOptionGroup getFeatureManagerOptionGroup() {
        return featureManagerOptionGroup;
    }

    /**
     * This method is used to update the feature status (registering or unregistered) according to the option value.
     * @param feature the feature
     * @param shallBeRegistered true if the feature shall be registered, false otherwise
     */
    public void setFeatureActivation(MDFeature feature, boolean shallBeRegistered) {
        if(shallBeRegistered && !feature.isRegistered())
            getFeatureRegister().registerFeature(feature);
        else if(!shallBeRegistered && feature.isRegistered())
            getFeatureRegister().unregisterFeature(feature);
    }


    private FeatureRegisterer getFeatureRegister() {
        return getPlugin().getFeatureRegister();
    }

}
