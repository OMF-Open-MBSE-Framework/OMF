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
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;

import java.util.List;
import java.util.stream.Collectors;

public class DeactivateFeatureFromOption extends SimpleFeature {
    private final FeatureManagerOptionGroup featureManagerOptionGroup;


    public DeactivateFeatureFromOption() {
        super( "Deactivate Features from Options Feature");
        this.featureManagerOptionGroup = new FeatureManagerOptionGroup("Manage registered Features "
                , "Manage registered Features");
    }



    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return new DeactivateFeatureFromOption_OptionHelper(this, getFeatureManagerOptionGroup());
    }

    @Override
    public void onRegistering() {
        super.onRegistering();
        EnvironmentOptions options = Application.getInstance().getEnvironmentOptions();
        if(options.getGroup(featureManagerOptionGroup.ID) == null)
            options.addGroup(featureManagerOptionGroup);

        Application.getInstance().insertActivityAfterStartup(this::refreshFeatureRegisteringFromEnvOptions);

    }

    private void refreshFeatureRegisteringFromEnvOptions() {
        getEnvOptionsHelper().getAllFeatureOptions().forEach(optionProperty ->
            getEnvOptionsHelper().getFeatureFromOption(getPlugin(), optionProperty)
                    .ifPresent(feature -> setFeatureActivation(feature, (boolean) optionProperty.getValue())));


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
    void setFeatureActivation(MDFeature feature, boolean shallBeRegistered) {
        if(shallBeRegistered && !feature.isRegistered())
            plugin.getFeatureRegisterer().registerFeature(feature);
        else if(!shallBeRegistered && feature.isRegistered())
            plugin.getFeatureRegisterer().unregisterFeature(feature);
    }
}
