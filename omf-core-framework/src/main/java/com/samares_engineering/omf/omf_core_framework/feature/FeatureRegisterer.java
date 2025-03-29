/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.errors.LegacyErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.HookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.feature.FeatureHookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature.FeatureLifeCycleHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.ProjectOnlyFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.listener.FeatureRegisteringEventHandler;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.ArrayList;
import java.util.List;

public class FeatureRegisterer {
    private List<FeatureItemRegisterer<?>> featureItemRegisters;
    private List<ProjectOnlyFeatureItemRegisterer<?>> projectOnlyFeatureItemRegisters;

    private final FeatureRegisteringEventHandler eventHandler;

    private List<OMFFeature> registeredFeatures = new ArrayList<>();
    private final OMFPlugin plugin;
    private HookExecutor<FeatureLifeCycleHook> featureHookExecutor;

    //TODO: Create a class regrouping all Configurators
    public FeatureRegisterer(OMFPlugin plugin) {
        this.plugin = plugin;
        this.featureItemRegisters = new ArrayList<>();
        this.projectOnlyFeatureItemRegisters = new ArrayList<>();
        this.eventHandler = new FeatureRegisteringEventHandler(this);
        this.featureHookExecutor = new FeatureHookExecutor(this);
    }

    /**
     * Register a feature using delegation to register all its items using the according item registerer
     * ProjectOnly items are registered only if the project is opened
     *
     * @param feature the feature to register
     */
    public void registerFeature(OMFFeature feature) {
        try {
            if (isAlreadyRegistered(feature)) {
                LegacyErrorHandler.handleException(new FeatureRegisteringException("Trying to register feature " + feature.getName() +
                        " which is already registered."));
            }
            feature.initFeature(plugin);
            feature.register();

            registerFeatureItems(feature);
            if (OMFUtils.isProjectOpened()) {
                registerProjectOnlyFeatureItems(feature);
            }
            registeredFeatures.add(feature);
            eventHandler.fireFeatureRegistered(feature);
        } catch (Exception e) {
            LegacyErrorHandler.handleException(new FeatureRegisteringException("Error while registering feature " + feature.getName(),
                    e), false);
        }
    }

    private void registerFeatureItems(OMFFeature feature) {
        feature.initFeatureItems();
        for (FeatureItemRegisterer registerer : featureItemRegisters) {
            registerer.registerFeatureItems(feature);
        }
    }

    /**
     * Registers a list of features, see {@link FeatureRegisterer#registerFeature(OMFFeature)}
     *
     * @param features the features to register
     */
    public void registerFeatures(List<OMFFeature> features) {
        features.forEach(this::registerFeature);
    }

    /**
     * Registers project only items of a list of features, see {@link FeatureRegisterer#registerProjectOnlyFeatureItems(OMFFeature)}
     *
     * @param features the features to register
     */
    public void registerProjectOnlyItemsOfFeatures(List<OMFFeature> features) {
        features.forEach(this::registerProjectOnlyFeatureItems);
    }

    /**
     * Registers feature items that are declared as "project only" until project is opened. This method is then called
     * every time the project opens.
     * On the first registration, the items are also initialised. We wait until the project to be opened to initialise
     * the items in order to avoid instances where the items need the project to be opened to function, for example if
     * you need to set a default value from the Sysml profile in an Option
     *
     * @param feature the feature to register
     */
    private void registerProjectOnlyFeatureItems(OMFFeature feature) {
        feature.initProjectOnlyFeatureItems();
        projectOnlyFeatureItemRegisters.forEach(registerer -> {
            try {
                registerer.registerFeatureItems(feature);
            } catch (Exception e) {
                LegacyErrorHandler.handleException(
                        new FeatureRegisteringException("Error while registering project only items for feature " +
                                feature.getName(), e));
            }
        });

    }

    /**
     * Unregisters a feature using delegation to unregister all its items using the according item registerer
     * On Failure it will continue to unregister the feature items and then throw an exception for each item that failed
     *
     * @param feature the feature to unregister
     */
    public void unregisterFeature(OMFFeature feature) throws FeatureRegisteringException {
        if (!isAlreadyRegistered(feature)) {
            LegacyErrorHandler.handleException(new FeatureRegisteringException("Trying to unregister feature "
                    + feature.getName() + " which is not registered."));
        }

        registeredFeatures.remove(feature);
        for (FeatureItemRegisterer<?> registerer : featureItemRegisters) {
            try {
                registerer.unregisterFeatureItems(feature);
            } catch (Exception e) {
                throw new FeatureRegisteringException(
                        "Error while unregistering items for feature " + feature.getName(), e);
            }
        }

        for (FeatureItemRegisterer<?> registerer : projectOnlyFeatureItemRegisters) {
            try {
                registerer.unregisterFeatureItems(feature);
            } catch (Exception e) {
                throw new FeatureRegisteringException("Error while unregistering Project only items for feature " +
                        feature.getName(), e);
            }
        }

        feature.unregister();
        eventHandler.fireFeatureUnregistered(feature);
    }

    /**
     * Unregisters a list of features, see {@link FeatureRegisterer#unregisterFeature(OMFFeature)}
     *
     * @param features the features to unregister
     */
    public void unregisterFeatures(List<OMFFeature> features) {
        new ArrayList<>(features).forEach(feature -> {
            try {
                unregisterFeature(feature);
            } catch (Exception e) {
                LegacyErrorHandler.handleException(new FeatureRegisteringException("Error while unregistering feature " +
                        feature.getName(), e), false);
            }
        });
    }

    /**
     * Unregisters project only items of a list of features, see {@link FeatureRegisterer#unregisterProjectOnlyFeatureItems(OMFFeature)}
     *
     * @param features the features to unregister
     */
    public void unregisterProjectOnlyItemsOfFeatures(List<OMFFeature> features) {
        new ArrayList<>(features).forEach(this::unregisterProjectOnlyFeatureItems);//New Arraylist to manage List modifications while iterating
    }

    /**
     * Unregisters project only items of a feature using delegation to unregister all its items using the according item registerer
     *
     * @param feature the feature to unregister
     */
    public void unregisterProjectOnlyFeatureItems(OMFFeature feature) {
        projectOnlyFeatureItemRegisters.forEach(registerer -> {
            try {
                registerer.unregisterFeatureItems(feature);
            } catch (Exception e) {
                LegacyErrorHandler.handleException(new FeatureRegisteringException("Error while unregistering project only items for" +
                        " feature " + feature.getName(), e), false);
            }
        });
    }

    /**
     * Checks if a feature is already registered
     * @param OMFFeature the feature to check
     * @return true if the feature is already registered
     */
    public boolean isAlreadyRegistered(OMFFeature OMFFeature) {
        return registeredFeatures.stream().anyMatch(OMFFeature.getClass()::isInstance);
    }

    /**
     * Adds a feature item registerer to the list of item registerers.
     * The registerer is initialised with the current instance of the FeatureRegisterer
     * FeatureItemRegisterer are used to register/unregister feature items of a feature
     * and will be called when a feature is registered/unregistered
     * @param featureItemRegisterer the registerer to add
     */
    public void addIFeatureItemRegisterer(FeatureItemRegisterer featureItemRegisterer) {
        try {
            this.featureItemRegisters.add(featureItemRegisterer);
            featureItemRegisterer.init(this);
        } catch (Exception e) {
            LegacyErrorHandler.handleException(new FeatureRegisteringException("Error while adding feature item registerer " +
                    featureItemRegisterer.getClass().getName(), e), false);
        }
    }

    /**
     * Adds a list of feature item registerers to the list of item registerers,
     * see {@link FeatureRegisterer#addIFeatureItemRegisterer(FeatureItemRegisterer)}
     * @param featureItemRegisterers the registerers to add
     */
    public void addAllIFeatureItemRegisterer(List<? extends FeatureItemRegisterer> featureItemRegisterers) {
        featureItemRegisterers.forEach(this::addIFeatureItemRegisterer);
    }

    /**
     * Removes a feature item registerer from the list of item registerers.
     * The registerer is initialised with the current instance of the FeatureRegisterer
     * FeatureItemRegisterer are used to register/unregister feature items of a feature
     * and will be called when a feature is registered/unregistered
     * @param featureItemRegisterer the registerer to remove
     */
    public void removeIFeatureItemRegisterer(FeatureItemRegisterer featureItemRegisterer) {
        this.featureItemRegisters.remove(featureItemRegisterer);
    }

    /**
     * Removes a list of feature item registerers from the list of item registerers,
     * see {@link FeatureRegisterer#removeIFeatureItemRegisterer(FeatureItemRegisterer)}
     * @param featureItemRegisterers the registerers to remove
     */
    public void removeAllIFeatureItemRegisterer(List<? extends FeatureItemRegisterer> featureItemRegisterers) {
        featureItemRegisterers.forEach(this::removeIFeatureItemRegisterer);
    }

    /**
     * Adds a project only feature item registerer to the list of item registers.
     * The registerer is initialised with the current instance of the FeatureRegisterer
     * ProjectOnlyFeatureItemRegisterer are used to register/unregister project only feature items of a feature
     * and will be called when a feature is registered/unregistered
     * @param featureItemRegisterer the registerer to add
     */
    public void addProjectOnlyFeatureItemRegisterer(ProjectOnlyFeatureItemRegisterer featureItemRegisterer) {
        try {
            this.projectOnlyFeatureItemRegisters.add(featureItemRegisterer);
            featureItemRegisterer.init(this);
        } catch (Exception e) {
            LegacyErrorHandler.handleException(new FeatureRegisteringException("Error while adding project only feature item registerer " +
                    featureItemRegisterer.getClass().getName(), e), false);
        }
    }

    /**
     * Adds a list of project only feature item registerers to the list of item registerers,
     * see {@link FeatureRegisterer#addProjectOnlyFeatureItemRegisterer(ProjectOnlyFeatureItemRegisterer)}
     * @param featureItemRegisterers the registerers to add
     */
    public void addAllProjectOnlyFeatureItemRegisterer(List<? extends ProjectOnlyFeatureItemRegisterer> featureItemRegisterers) {
        featureItemRegisterers.forEach(this::addProjectOnlyFeatureItemRegisterer);
    }

    /**
     * Removes a project only feature item registerer from the list of item registerers.
     * The registerer is initialised with the current instance of the FeatureRegisterer
     * ProjectOnlyFeatureItemRegisterer are used to register/unregister project only feature items of a feature
     * and will be called when a feature is registered/unregistered
     * @param featureItemRegisterer the registerer to remove
     */
    public void removeProjectOnlyFeatureItemRegisterer(ProjectOnlyFeatureItemRegisterer featureItemRegisterer) {
        this.projectOnlyFeatureItemRegisters.remove(featureItemRegisterer);
    }

    /**
     * Removes a list of project only feature item registerers from the list of item registerers,
     * see {@link FeatureRegisterer#removeProjectOnlyFeatureItemRegisterer(ProjectOnlyFeatureItemRegisterer)}
     * @param featureItemRegisterers the registerers to remove
     */
    public void removeAllProjectOnlyFeatureItemRegisterer(List<? extends ProjectOnlyFeatureItemRegisterer> featureItemRegisterers) {
        featureItemRegisterers.forEach(this::removeProjectOnlyFeatureItemRegisterer);
    }

    //-------------------------------- GETTER / SETTER --------------------------------------------

    /**
     * Returns all registered features
     * @return the registered features
     */
    public List<OMFFeature> getRegisteredFeatures() {
        return registeredFeatures;
    }

    public void setRegisteredFeatures(List<OMFFeature> registeredFeatures) {
        this.registeredFeatures = registeredFeatures;
    }

    public List<FeatureItemRegisterer<?>> getFeatureItemRegisters() {
        return featureItemRegisters;
    }

    public List<ProjectOnlyFeatureItemRegisterer<?>> getProjectOnlyFeatureItemRegisters() {
        return projectOnlyFeatureItemRegisters;
    }

    /**
     * Get the plugin instance
     * @return the plugin
     */
    public OMFPlugin getPlugin() {
        return plugin;
    }

    public FeatureRegisteringEventHandler getEventHandler() {
        return eventHandler;
    }

    public HookExecutor<FeatureLifeCycleHook> getFeatureHookExecutor() {
        return featureHookExecutor;
    }
}
