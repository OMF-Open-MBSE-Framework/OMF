/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature;

import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IRuleEngine;
import com.samares.omf.core.plugin.APlugin;

import java.util.List;

public abstract class AFeature implements MDFeature{
    protected String name;
    protected boolean isRegistered;
    private boolean isFeatureInitialised = false;
    private boolean isProjectOnlyItemsInitialised = false;
    private EnvOptionsHelper envOptionsHelper;

    protected APlugin plugin;

    // Registrable items

    private List<IUIAction> mdActions;
    private List<IRuleEngine> liveActions;
    private List<IOption> options;
    // Delayed registrable items

    private List<IOption> projectOnlyOptions;
    private List<IUIAction> projectOnlyMdActions;
    private List<IRuleEngine> projectOnlyLiveActions;
    protected AFeature(String name){
        this.name = name;
    }

    /**
     * Instantiates the features items (options, ui actions, live actions).
     * We separate this from the constructor as we want to delay the instantiation of feature items to the moment the
     * feature is first registered, as the
     * Note: this does not register the feature into magic draw/listeners.
     * @param plugin
     */
    public final void initFeature(APlugin plugin) {
        // We only need to initialise feature once
        if (isFeatureInitialised) {
            return;
        }
        this.plugin = plugin;

        this.options = initOptions();
        options.forEach(this::initRegistrableItem);

        this.mdActions = initFeatureActions();
        mdActions.forEach(this::initRegistrableItem);

        this.liveActions = initLiveActions();
        liveActions.forEach(this::initRegistrableItem);

        this.envOptionsHelper = initEnvOptionsHelper();

        isFeatureInitialised = true;
    }

    /**
     * Instantiates the feature items that depend on project to instantiate correctly
     */
    public final void initProjectOnlyFeatureItems() {
        // We only need to initialise project only items once
        if (isProjectOnlyItemsInitialised) {
            return;
        }

        this.projectOnlyOptions = initProjectOnlyOptions();
        projectOnlyOptions.forEach(this::initRegistrableItem);

        this.projectOnlyMdActions = initProjectOnlyFeatureActions();
        projectOnlyMdActions.forEach(this::initRegistrableItem);

        this.projectOnlyLiveActions = initProjectOnlyLiveActions();
        projectOnlyLiveActions.forEach(this::initRegistrableItem);

        isProjectOnlyItemsInitialised = true;
    }

    private void initRegistrableItem(RegistrableFeatureItem item) {
        item.initRegisterableItem(this);
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
     * Define all the feature options (Environment && Project) there, it will be automatically registered with the feature.
     * @return list of IOption to register
     */
    protected abstract List<IOption> initOptions();

    protected abstract List<IOption> initProjectOnlyOptions();

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
    Accessors
     */

    @Override
    public String getName() {
        return name;
    }
    public final void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
        // Call corresponding lifecycle hook (code to be executed on registering/unregistering)
        if (isRegistered) {
            onRegistering();
        }
        onUnregistering();
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
