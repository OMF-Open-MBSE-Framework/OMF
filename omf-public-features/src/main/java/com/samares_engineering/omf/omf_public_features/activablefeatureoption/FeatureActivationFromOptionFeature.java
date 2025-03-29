/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.activablefeatureoption;

import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.options.EnvironmentOptions;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2;
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.AOnMagicDrawStartHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_public_features.activablefeatureoption.listener.FeatureRegisteringListener;
import com.samares_engineering.omf.omf_public_features.activablefeatureoption.options.FeatureActivationFromOption_OptionHelper;
import com.samares_engineering.omf.omf_public_features.activablefeatureoption.options.FeatureActivationManagerOptionGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This feature is used to manage the features of the plugin.
 * It will register a dedicated group of options in the Environment Options.
 * It creates the options to activate/deactivate the features and update the feature status according to the option value.
 * It also can be used to deactivate some features by default on startup.
 * NOTE! Please register this after all other features.
 */
public class FeatureActivationFromOptionFeature extends SimpleFeature {
    private final FeatureActivationManagerOptionGroup featureManagerOptionGroup;
    private final FeatureRegisteringListener featureRegisteringListener;
    private List<OMFFeature> featuresToDeactivateByDefaultOnStartUp;

    public FeatureActivationFromOptionFeature() {
        this("Manage Features", List.of());
    }

    public FeatureActivationFromOptionFeature(List<OMFFeature> featuresToDeactivateByDefaultOnStartUp) {
        this("Manage Features", featuresToDeactivateByDefaultOnStartUp);
    }

    public FeatureActivationFromOptionFeature(String environmentOptionGroupName) {
        this(environmentOptionGroupName, new ArrayList<>());
    }

    public FeatureActivationFromOptionFeature(String environmentOptionGroupName, List<OMFFeature> featuresToDeactivateByDefaultOnStartUp) {
        super("Deactivate Features from Options Feature");
        this.featureManagerOptionGroup = new FeatureActivationManagerOptionGroup("Manage registered Features "
                , environmentOptionGroupName);
        this.featureRegisteringListener = new FeatureRegisteringListener(this);
        this.featuresToDeactivateByDefaultOnStartUp = featuresToDeactivateByDefaultOnStartUp;
    }

    /**
     * This method is used to set the features to deactivate by default on startup.
     *
     * @param featuresToDeactivateByDefaultOnStartUp the features to deactivate by default on startup
     * @return this
     */
    public FeatureActivationFromOptionFeature onStartupDeactivate(List<OMFFeature> featuresToDeactivateByDefaultOnStartUp) {
        this.featuresToDeactivateByDefaultOnStartUp = featuresToDeactivateByDefaultOnStartUp;
        return this;
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {
        return new FeatureActivationFromOption_OptionHelper(this, getFeatureManagerOptionGroup());
    }

    /**
     * This method is called when the plugin is registered.
     * It will add the group of options in the Environment Options if it is not already present.
     * And call the method to initialize the synchronization between the options and the features.
     * see {@link #initSynchroWithEnvOptions()}
     */
    @Override
    public void onRegistering() {
        EnvironmentOptions options = Application.getInstance().getEnvironmentOptions();
        if (options.getGroup(featureManagerOptionGroup.ID) == null)
            options.addGroup(featureManagerOptionGroup);
    }

    @Override
    protected List<Hook> initLifeCycleHooks() {
        return List.of(new AOnMagicDrawStartHook() {
            @Override
            public void onMagicDrawStart() {
                initSynchroWithEnvOptions();
            }
        });
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
        for (var optionProperty : getEnvOptionsHelper().getAllFeatureOptions()) {
            var featureOptional = getEnvOptionsHelper().getFeatureFromOption(getPlugin(), optionProperty);
            if (featureOptional.isPresent()) {
                var feature = featureOptional.get();
                var deactivateOnStartup = featuresToDeactivateByDefaultOnStartUp.contains(feature);
                var activatedInOptions = (boolean) optionProperty.getValue();
                setFeatureActivation(feature, !deactivateOnStartup && activatedInOptions);
                if (deactivateOnStartup) {
                    optionProperty.setValue(false);
                }
            } else {
                OMFLogger2.toSystem().warning("Feature not found for option " + optionProperty.getName());
            }
        }
    }

    /**
     * This method is used to register a listener to the feature registering/unregistering to update the option value accordingly.
     *
     * @param featureRegisteringListener the listener
     */
    private void registerListener(FeatureRegisteringListener featureRegisteringListener) {
        getFeatureRegister().getEventHandler().addPropertyChangeListener(featureRegisteringListener);
    }

    @Override
    protected List<Option> initOptions() {
        FeatureActivationFromOption_OptionHelper envOptionsHelper = getEnvOptionsHelper();
        List<OMFFeature> features = getPlugin().getFeatures();
        features.remove(this); // remove this feature from the list (we don't want to create an option for this feature
        return features
                .stream()
                .map(envOptionsHelper::createDeactivationOption)
                .collect(Collectors.toList());
    }


    @Override
    public FeatureActivationFromOption_OptionHelper getEnvOptionsHelper() {
        return (FeatureActivationFromOption_OptionHelper) super.getEnvOptionsHelper();
    }

    public FeatureActivationManagerOptionGroup getFeatureManagerOptionGroup() {
        return featureManagerOptionGroup;
    }

    /**
     * This method is used to update the feature status (registering or unregistered) according to the option value.
     *
     * @param feature           the feature
     * @param shallBeRegistered true if the feature shall be registered, false otherwise
     */
    public void setFeatureActivation(OMFFeature feature, boolean shallBeRegistered) {
        if (shallBeRegistered && !feature.isRegistered())
            getFeatureRegister().registerFeature(feature);
        else if (!shallBeRegistered && feature.isRegistered())
            getFeatureRegister().unregisterFeature(feature);
    }

    private FeatureRegisterer getFeatureRegister() {
        return getPlugin().getFeatureRegisterer();
    }

    public List<OMFFeature> getFeaturesToDeactivateByDefaultOnStartUp() {
        return featuresToDeactivateByDefaultOnStartUp;
    }
}
