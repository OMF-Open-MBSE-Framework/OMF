/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature;

import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.actions.IUIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class AFeature implements MDFeature {
    protected String name;
    protected boolean isRegistered;
    private boolean isFeatureInitialised = false;
    private boolean isProjectOnlyItemsInitialised = false;
    private EnvOptionsHelper envOptionsHelper;

    protected APlugin plugin;

    // Registrable items
    private final List<IUIAction> mdActions = new ArrayList<>();
    private final List<IRuleEngine> liveActions = new ArrayList<>();
    private final List<IOption> options = new ArrayList<>();

    // Delayed registrable items
    private final List<IOption> projectOnlyOptions = new ArrayList<>();
    private final List<IUIAction> projectOnlyMdActions = new ArrayList<>();
    private final List<IRuleEngine> projectOnlyLiveActions =  new ArrayList<>();

    protected AFeature(String name){
        this.name = name;
    }

    /*
    Instantiation methods
     */

    /**
     * Instantiates the features items (options, ui actions, live actions).
     * We separate this from the constructor as we want to delay the instantiation of feature items to the moment the
     * feature is first registered, as the
     * Note: this does not register the feature into magic draw/listeners.
     * @param plugin
     */
    public final void initFeature(APlugin plugin) {
        // We only need to initialise feature once
        if (isFeatureInitialised) return;
        this.plugin = plugin;
        try {
            this.envOptionsHelper = initEnvOptionsHelper();
        }catch (Exception e){
            OMFErrorHandler.handleException(e);
        }

        try {
            this.options.addAll(initOptions());
            options.forEach(this::initRegistrableItem);
        }catch (Exception e){
            OMFErrorHandler.handleException(e);
        }

        try {
            this.mdActions.addAll(initFeatureActions());
            mdActions.forEach(this::initRegistrableItem);
        }catch (Exception e){
            OMFErrorHandler.handleException(e);
        }
        try {
            this.liveActions.addAll(initLiveActions());
            liveActions.forEach(this::initRegistrableItem);
        }catch (Exception e){
            OMFErrorHandler.handleException(e);
        }

        isFeatureInitialised = true;
    }

    /**
     * Instantiates the feature items that depend on project to instantiate correctly
     */
    public final void initProjectOnlyFeatureItems() {
        // We only need to initialise project only items once
        if (isProjectOnlyItemsInitialised) return;

        try {
            this.projectOnlyOptions.addAll(initProjectOnlyOptions());
            projectOnlyOptions.forEach(this::initRegistrableItem);
        }catch (Exception e){
            OMFErrorHandler.handleException(e);
        }

        try {
            this.projectOnlyMdActions.addAll(initProjectOnlyFeatureActions());
            projectOnlyMdActions.forEach(this::initRegistrableItem);
        }catch (Exception e){
            OMFErrorHandler.handleException(e);
        }

        try {
            this.projectOnlyLiveActions.addAll(initProjectOnlyLiveActions());
            projectOnlyLiveActions.forEach(this::initRegistrableItem);
        }catch (Exception e){
            OMFErrorHandler.handleException(e);
        }

        isProjectOnlyItemsInitialised = true;
    }

    private void initRegistrableItem(RegistrableFeatureItem item) {
        item.initRegistrableItem(this);
    }

    /**
     * Instantiate the environment option helper to be automatically register with the feature
     * @return the initialised environment options helper for the feature
     */
    protected abstract EnvOptionsHelper initEnvOptionsHelper();

    /**
     * Define all the feature action there, it will be automatically registered with the feature.
     * @return list of MDAction to register
     */
    protected abstract List<IUIAction> initFeatureActions();

    /**
     * UI Actions that need to wait for a project to be loaded to be instantiated
     * @return
     */
    protected abstract List<IUIAction> initProjectOnlyFeatureActions();

    /**
     * Define all the feature live actions (RuleEngines) there, it will be automatically registered with the feature.
     * @return list of IRuleEngine to register
     */
    protected abstract List<IRuleEngine> initLiveActions();

    protected abstract List<IRuleEngine> initProjectOnlyLiveActions();

    /**
     * Define all the feature options (Environment and Project) there, it will be automatically registered with the feature.
     * @return list of IOption to register
     */
    protected abstract List<IOption> initOptions();

    protected abstract List<IOption> initProjectOnlyOptions();

    /*
     Lifecycle hooks
     */

    /**
     * Override this to inject code to be run on feature activation
     */
    public void onRegistering() {}

    /**
     * Override this to inject code to be run on feature deactivation
     */
    public void onUnregistering() {}

    /**
     * Override this to inject code to be run on project opening
     */
    public void onProjectOpen() {}

    /**
     * Override this to inject code to be run on project closing
     */
    public void onProjectClose() {}

    /*
    Helpers
     */

    protected OptionImpl createEnvOption(Property property, String groupName) {
        return new OptionImpl(
            property,
            groupName,
            plugin.getEnvironmentOptionsGroup(),
            OptionKind.Environment
        );
    }

    /*
    Accessors
     */

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the feature as registered or not.
     * @param isRegistered
     */
    public final void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
        // Call corresponding lifecycle hook (code to be executed on registering/unregistering)
        if (isRegistered) {
            onRegistering();
        } else {
            onUnregistering();
        }
    }

    public final boolean isRegistered() {
        return isRegistered;
    }

    public APlugin getPlugin() {
        return plugin;
    }

    public List<IOption> getOptions() {
        return options;
    }

    public List<IRuleEngine> getRuleEngines() {
        return liveActions;
    }

    public List<IUIAction> getUIActions() {
        return mdActions;
    }

    public List<IOption> getProjectOnlyOptions() {
        return projectOnlyOptions;
    }

    public List<IRuleEngine> getProjectOnlyRuleEngines() {
        return projectOnlyLiveActions;
    }

    public List<IUIAction> getProjectOnlyUIActions() {
        return projectOnlyMdActions;
    }

    public EnvOptionsHelper getEnvOptionsHelper() {
        return envOptionsHelper;
    }
}
