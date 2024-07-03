/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature;

import com.nomagic.magicdraw.properties.Property;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;

import java.util.ArrayList;
import java.util.List;

/**
 * AFeature is the base class for all features. It provides the basic implementation of the MDFeature interface.
 * Features are the main way to extend MagicDraw with OMF. They are composed of:
 * - Options: Options are used to store data in the project or in the environment.
 * - UI Actions: UI Actions are actions that can be triggered by the user from the UI (browser, menu, diagram, etc.).
 * - Live Actions: Live Actions are actions that are triggered by the system on Model changes (e.g. element creation).
 * Feature are registered in the OMFPlugin class, and are initialised when the plugin is loaded.
 * Features can be registered as project only, meaning that they will only be available in the current project.
 * see {@link OMFPlugin}
 * see {@link com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer}
 * see {@link UIAction}
 * see {@link Option}
 * see {@link LiveActionEngine}
 */
public abstract class AFeature implements OMFFeature {
    protected String name;
    protected boolean isRegistered;
    private boolean isFeatureInitialised = false;
    private boolean isProjectOnlyItemsInitialised = false;
    private boolean isFeatureItemsInitialised = false;
    private EnvOptionsHelper envOptionsHelper;

    protected OMFPlugin plugin;

    // Registrable items
    private final List<UIAction> mdActions = new ArrayList<>();
    private final List<LiveActionEngine> liveActions = new ArrayList<>();
    private final List<Option> options = new ArrayList<>();
    private final List<Hook> hooksHolders = new ArrayList<>();

    // Delayed registrable items
    private final List<Option> projectOnlyOptions = new ArrayList<>();
    private final List<UIAction> projectOnlyMdActions = new ArrayList<>();
    private final List<LiveActionEngine> projectOnlyLiveActionEngines = new ArrayList<>();

    protected AFeature(String name) {
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
     */
    public final void initFeature(OMFPlugin plugin) {
        // We only need to initialise feature once
        if (isFeatureInitialised) return;
        this.plugin = plugin;
        try {
            this.envOptionsHelper = initEnvOptionsHelper();
        } catch (Exception e) {
            throw new FeatureRegisteringException("Error while instantiating environment options helper for feature " + name, e);
        }
        isFeatureInitialised = true;
    }

    public void initFeatureItems() {
        // We only need to initialise feature items once
        if (isFeatureItemsInitialised) return;

        try {
            this.options.addAll(initOptions());
            options.forEach(this::initRegistrableItem);
        } catch (Exception e) {
            throw new FeatureRegisteringException("Error while instantiating options for feature " + name, e);
        }

        try {
            this.mdActions.addAll(initFeatureActions());
            mdActions.forEach(this::initRegistrableItem);
        } catch (Exception e) {
            throw new FeatureRegisteringException("Error while instantiating ui actions for feature " + name, e);
        }

        try {
            this.liveActions.addAll(initLiveActions());
            liveActions.forEach(this::initRegistrableItem);
        } catch (Exception e) {
            throw new FeatureRegisteringException("Error while instantiating live actions for feature " + name, e);
        }

        try {
            this.hooksHolders.addAll(initLifeCycleHooks());
            hooksHolders.forEach(this::initRegistrableItem);
        } catch (Exception e) {
            throw new FeatureRegisteringException("Error while instantiating lifecycle hooks for feature " + name, e);
        }

        isFeatureItemsInitialised = true;
    }

    /**
     * Instantiates the feature items that depend on project to instantiate correctly
     */
    public final void initProjectOnlyFeatureItems() {
        // We only need to initialise project only feature items once
        if (isProjectOnlyItemsInitialised) return;

        try {
            this.projectOnlyOptions.addAll(initProjectOnlyOptions());
            projectOnlyOptions.forEach(this::initRegistrableItem);
        } catch (Exception e) {
            throw new FeatureRegisteringException("Error while instantiating project only options for feature " + name, e);
        }

        try {
            this.projectOnlyLiveActionEngines.addAll(initProjectOnlyLiveActions());
            projectOnlyLiveActionEngines.forEach(this::initRegistrableItem);
        } catch (Exception e) {
            throw new FeatureRegisteringException("Error while instantiating project only live actions for feature " + name, e);
        }

        isProjectOnlyItemsInitialised = true;
    }

    private void initRegistrableItem(RegistrableFeatureItem item) {
        item.initRegistrableItem(this);
    }

    /**
     * Instantiate the environment option helper to be automatically register with the feature
     *
     * @return the initialised environment options helper for the feature
     */
    protected abstract EnvOptionsHelper initEnvOptionsHelper();

    /**
     * Define all the feature action there, it will be automatically registered with the feature.
     *
     * @return list of MDAction to register
     */
    protected abstract List<UIAction> initFeatureActions();

    /**
     * Define all the feature live actions (LiveActionEngines) there, it will be automatically registered with the feature.
     *
     * @return list of ILiveActionEngine to register
     */
    protected abstract List<LiveActionEngine> initLiveActions();
    /**
     * Define all the project only live actions (LiveActionEngines) there, it will be automatically registered with the feature.
     *
     * @return list of ILiveActionEngine to register
     */
    protected abstract List<LiveActionEngine> initProjectOnlyLiveActions();

    /**
     * Define all the feature options (Environment and Project) there, it will be automatically registered with the feature.
     *
     * @return list of IOption to register
     */
    protected abstract List<Option> initOptions();
    /**
     * Define all the project only options (Environment and Project) there, it will be automatically registered with the feature.
     *
     * @return list of IOption to register
     */
    protected abstract List<Option> initProjectOnlyOptions();

    /**
     * Define all the lifecycle hooks there, it will be automatically registered with the feature.
     * @return list of LifeCycleHook to register
     */
    protected abstract List<Hook> initLifeCycleHooks();

    /*
     Lifecycle hooks
     */

    public final void triggerOnRegisteringHook() {
        OMFBarrierExecutor.executeWithinBarrier(this::onRegistering, this);
    }

    public final void triggerOnUnregisteringHook() {
        OMFBarrierExecutor.executeWithinBarrier(this::onUnregistering, this);
    }

    /**
     * Override this to inject code to be run on feature activation
     */
    public void onRegistering() {}

    /**
     * Override this to inject code to be run on feature deactivation
     */
    public void onUnregistering() {}



    /*
    Helpers
     */
    protected OptionImpl createEnvOption(Property property, String groupName) {
        return new OptionImpl(
                property,
                groupName,
                plugin.getEnvironmentOptionsGroup()
                        .orElseThrow(() -> new FeatureRegisteringException("Can't create environment option as no" +
                                " environment options groups have been declared for this plugin")),
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
     * Set the feature as registered only if it's not already the case then calls the corresponding lifecycle hook.
     * see{@link AFeature#setIsRegistered(boolean)}
     */
    @Override
    public void register() {
        if (isRegistered) return;
        setIsRegistered(true);
        triggerOnRegisteringHook();
    }

    /**
     * Set the feature as unregistered only if it's not already the case then calls the corresponding lifecycle hook.
     * see{@link AFeature#setIsRegistered(boolean)}
     */
    @Override
    public void unregister() {
        if (!isRegistered) return;
        setIsRegistered(false);
        triggerOnUnregisteringHook();
    }

    /**
     * Actual set of the feature as registered or not, and calls the corresponding lifecycle hook.
     * Please use the see{@link AFeature#register()} and see{@link AFeature#unregister()} methods instead of this one.
     *
     * @param isRegistered true if the feature is registered, false otherwise
     */
    public final void setIsRegistered(boolean isRegistered) {
        this.isRegistered = isRegistered;
    }

    public final boolean isRegistered() {
        return isRegistered;
    }

    public OMFPlugin getPlugin() {
        return plugin;
    }

    @Override
    public List<Option> getOptions() {
        return options;
    }
    @Override
    public List<LiveActionEngine> getLiveActionEngines() {
        return liveActions;
    }
    @Override
    public List<UIAction> getUIActions() {
        return mdActions;
    }
    @Override
    public List<Option> getProjectOnlyOptions() {
        return projectOnlyOptions;
    }
    @Override
    public List<LiveActionEngine> getProjectOnlyLiveActionEngines() {
        return projectOnlyLiveActionEngines;
    }
    @Override
    public List<UIAction> getProjectOnlyUIActions() {
        return projectOnlyMdActions;
    }
    @Override
    public EnvOptionsHelper getEnvOptionsHelper() {
        return envOptionsHelper;
    }

    @Override
    public List<Hook> getLifeCycleHooks() {
        return hooksHolders;
    }
}
