/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature;

import com.nomagic.ci.persistence.local.a.I;
import com.samares.omf.core.feature.registrables.actions.actions.IUIAction;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.rule_engines.rule_engine.IFeatureRuleEngine;
import com.samares.omf.core.plugin.APlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AFeature implements MDFeature{

    protected APlugin plugin;
    private boolean isFeatureInitialised = false;
    private boolean delayedItemsInitialised = false;

    protected String name;
    protected boolean isRegistered;
    private List<IUIAction> mdActions;
    private List<IFeatureRuleEngine> liveActions;
    private List<IOption> options;

    private List<IOption> delayedOptions;

    private List<IUIAction> delayedMdActions;
    private List<IFeatureRuleEngine> delayedLiveActions;

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
    }

    /**
     * Instantiates the feature items that depend on project to instantiate correctly
     */
    public final void initDelayedFeatureItems() {
        // We only need to initialise delayed items once
        if (delayedItemsInitialised) {
            return;
        }

        this.delayedOptions = initDelayedOptions();
        delayedOptions.forEach(this::initRegistrableItem);

        this.delayedMdActions = initDelayedFeatureActions();
        delayedMdActions.forEach(this::initRegistrableItem);

        this.delayedLiveActions = initDelayedLiveActions();
        delayedLiveActions.forEach(this::initRegistrableItem);
    }

    private void initRegistrableItem(RegistrableFeatureItem item) {
        item.initRegisterableItem(this);
    }

    /**
     * Define all the feature action there, it will be automatically registered with the feature.
     * @return list of MDAction to register
     */
    protected abstract List<IUIAction> initFeatureActions();

    /**
     * UI Actions that need to wait for a project to be loaded to be instantiated
     * @return
     */
    protected abstract List<IUIAction> initDelayedFeatureActions();

    /**
     * Define all the feature live actions (RuleEngines) there, it will be automatically registered with the feature.
     * @return list of IFeatureRuleEngine to register
     */
    protected abstract List<IFeatureRuleEngine> initLiveActions();

    protected abstract List<IFeatureRuleEngine> initDelayedLiveActions();

    /**
     * Define all the feature options (Environment && Project) there, it will be automatically registered with the feature.
     * @return list of IOption to register
     */
    protected abstract List<IOption> initOptions();

    protected abstract List<IOption> initDelayedOptions();

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

    @Override
    public List<IUIAction> getUIActions() {
        return mdActions;
    }

    public final void setRegistered(boolean isRegistered) {
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

    @Override
    public List<IFeatureRuleEngine> getRuleEngines() {
        return liveActions;
    }

    @Override
    public List<IOption> getOptions() {
        return options;
    }

    public APlugin getPlugin() {
        return plugin;
    }

    public List<IOption> getDelayedOptions() {
        return delayedOptions;
    }

    public List<IUIAction> getDelayedUIActions() {
        return delayedMdActions;
    }

    public List<IFeatureRuleEngine> getDelayedRuleEngines() {
        return delayedLiveActions;
    }
}
