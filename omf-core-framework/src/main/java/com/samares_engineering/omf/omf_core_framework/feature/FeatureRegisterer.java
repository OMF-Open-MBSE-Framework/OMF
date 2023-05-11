/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.feature.errors.FeatureException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly.IProjectOnlyFeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.ArrayList;
import java.util.List;

public class FeatureRegisterer {


    private List<IFeatureItemRegisterer> featureItemRegisters;
    private List<IProjectOnlyFeatureItemRegisterer> projectOnlyFeatureItemRegisters;

    private List<MDFeature> registeredFeatures = new ArrayList<>();
    private final APlugin plugin;

    //TODO: Create a class regrouping all Configurators
    public FeatureRegisterer(APlugin plugin){
        this.plugin = plugin;
        this.featureItemRegisters = new ArrayList<>();
        this.projectOnlyFeatureItemRegisters = new ArrayList<>();

   }

    /**
     * Register a feature using delegation to register all its items using the according item registerer
     * ProjectOnly items are registered only if the project is opened
     * @param feature
     */
    public void registerFeature(MDFeature feature) {
        try {
            if(isAlreadyRegistered(feature)) {
                throw new FeatureException("Trying to register feature " + feature.getName() +
                        " which is already registered.", GenericException.ECriticality.ALERT);
            }

            feature.initFeature(plugin);
            feature.setIsRegistered(true);

            registeredFeatures.add(feature);

            for (IFeatureItemRegisterer registerer : featureItemRegisters) {
                registerer.registerFeature(feature);
                if (OMFUtils.currentProject != null) {
                    registerProjectOnlyFeatureItems(feature);
                }
            }
        } catch (FeatureException e) { //TODO: Act if feature need to be unregistered
            OMFErrorHandler.handleException(new FeatureException("Error while registering feature " + feature.getName(),
                    e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    /**
     * Registers a list of features, see {@link FeatureRegisterer#registerFeature(MDFeature)}
     * @param features
     */
    public void registerFeatures(List<MDFeature> features){
        features.forEach(this::registerFeature);
    }

    /**
     * Registers feature items that are declared as "project only" until project is opened. This method is then called
     * every time the project opens.
     * On the first registration, the items are also initialised. We wait until the project to be opened to initialise
     * the items in order to avoid instances where the items need the project to be opened to function, for example if
     * you need to set a default value from the Sysml profile in an Option
     * @param feature
     */
    private void registerProjectOnlyFeatureItems(MDFeature feature) {
        feature.initProjectOnlyFeatureItems();

        projectOnlyFeatureItemRegisters.forEach(registerer -> {
            try {
                registerer.registerFeature(feature);
            }catch (FeatureException e) {
                OMFErrorHandler.handleException(new FeatureException("Error while registering project only items for feature " +
                        feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
            }
        });

    }

    /**
     * Registers project only items of a list of features, see {@link FeatureRegisterer#registerProjectOnlyFeatureItems(MDFeature)}
     * @param features
     */
    public void registerProjectOnlyItemsOfFeatures(List<MDFeature> features) {
        features.forEach(this::registerProjectOnlyFeatureItems);
    }

    /**
     * Unregisters a feature using delegation to unregister all its items using the according item registerer
     * On Failure it will continue to unregister the feature items and then throw an exception for each item that failed
     * @param feature
     */
    public void unregisterFeature(MDFeature feature){
        try {
            if (!isAlreadyRegistered(feature)) {
                throw new FeatureException("Trying to unregister feature " + feature.getName() +
                        " which is not registered.", GenericException.ECriticality.ALERT);
            }

            registeredFeatures.remove(feature);
            for (IFeatureItemRegisterer registerer : featureItemRegisters) {
               try {
                   registerer.unregisterFeature(feature);
               }catch (FeatureException e) {
                   OMFErrorHandler.handleException(new FeatureException("Error while unregistering items for feature " +
                           feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
               }
            }

            for (IFeatureItemRegisterer registerer : projectOnlyFeatureItemRegisters) {
               try {
                   registerer.unregisterFeature(feature);
               }catch (FeatureException e) {
                   OMFErrorHandler.handleException(new FeatureException("Error while unregistering Project only items for feature " +
                           feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
               }
            }

            feature.setIsRegistered(false);

        }catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while unregistering items for feature " +
                    feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    /**
     * Unregisters a list of features, see {@link FeatureRegisterer#unregisterFeature(MDFeature)}
     * @param features
     */
    public void unregisterFeatures(List<MDFeature> features){
        new ArrayList<>(features).forEach(this::unregisterFeature); //New Arraylist to manage List modifications while iterating
    }

    /**
     * Unregisters project only items of a list of features, see {@link FeatureRegisterer#unregisterDelayedItemsOfFeature(MDFeature)}
     * @param features
     */
    public void unregisterProjectOnlyItemsOfFeatures(List<MDFeature> features){
        new ArrayList<>(features).forEach(this::unregisterDelayedItemsOfFeature);//New Arraylist to manage List modifications while iterating
    }

    /**
     * Unregisters project only items of a feature using delegation to unregister all its items using the according item registerer
     * @param feature
     */
    public void unregisterDelayedItemsOfFeature(MDFeature feature){
        projectOnlyFeatureItemRegisters.forEach(registerer -> {
            try {
                registerer.unregisterFeature(feature);
            }catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while unregistering project only items for feature " +
                    feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }});
    }

    /** Checks if a feature is already registered
     * @param mdFeature
     * @return
     */
    public boolean isAlreadyRegistered(MDFeature mdFeature) {
        return registeredFeatures.stream().anyMatch(mdFeature.getClass()::isInstance);
    }

    /**
     * Adds a feature item registerer to the list of item registerers.
     * The registerer is initialised with the current instance of the FeatureRegisterer
     * FeatureItemRegisterer are used to register/unregister feature items of a feature
     * and will be called when a feature is registered/unregistered
     * @param featureItemRegisterer
     */
    public void addIFeatureItemRegisterer(IFeatureItemRegisterer featureItemRegisterer){
       try {
           this.featureItemRegisters.add(featureItemRegisterer);
           featureItemRegisterer.init(this);
       }catch (FeatureException e) {
           OMFErrorHandler.handleException(new FeatureException("Error while adding feature item registerer " +
                   featureItemRegisterer.getClass().getName(), e, GenericException.ECriticality.CRITICAL), false);
       }
    }

    /**
     * Adds a list of feature item registerers to the list of item registerers,
     * see {@link FeatureRegisterer#addIFeatureItemRegisterer(IFeatureItemRegisterer)}
     * @param featureItemRegisterers
     */
    public void addAllIFeatureItemRegisterer(List<? extends IFeatureItemRegisterer> featureItemRegisterers){
        featureItemRegisterers.forEach(this::addIFeatureItemRegisterer);
    }

    /**
     * Removes a feature item registerer from the list of item registerers.
     * The registerer is initialised with the current instance of the FeatureRegisterer
     * FeatureItemRegisterer are used to register/unregister feature items of a feature
     * and will be called when a feature is registered/unregistered
     * @param featureItemRegisterer
     */
    public void removeIFeatureItemRegisterer(IFeatureItemRegisterer featureItemRegisterer){
        this.featureItemRegisters.remove(featureItemRegisterer);
    }
    /**
     * Removes a list of feature item registerers from the list of item registerers,
     * see {@link FeatureRegisterer#removeIFeatureItemRegisterer(IFeatureItemRegisterer)}
     * @param featureItemRegisterers
     */
    public void removeAllIFeatureItemRegisterer(List<? extends IFeatureItemRegisterer> featureItemRegisterers){
        featureItemRegisterers.forEach(this::removeIFeatureItemRegisterer);
    }

    /**
     * Adds a project only feature item registerer to the list of item registerers.
     * The registerer is initialised with the current instance of the FeatureRegisterer
     * ProjectOnlyFeatureItemRegisterer are used to register/unregister project only feature items of a feature
     * and will be called when a feature is registered/unregistered
     * @param featureItemRegisterer
     */
    public void addProjectOnlyFeatureItemRegisterer(IProjectOnlyFeatureItemRegisterer featureItemRegisterer){
        try{
            this.projectOnlyFeatureItemRegisters.add(featureItemRegisterer);
            featureItemRegisterer.init(this);
        }catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while adding project only feature item registerer " +
                    featureItemRegisterer.getClass().getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }
    /**
     * Adds a list of project only feature item registerers to the list of item registerers,
     * see {@link FeatureRegisterer#addProjectOnlyFeatureItemRegisterer(IProjectOnlyFeatureItemRegisterer)}
     * @param featureItemRegisterers
     */
    public void addAllProjectOnlyFeatureItemRegisterer(List<? extends IProjectOnlyFeatureItemRegisterer> featureItemRegisterers){
        featureItemRegisterers.forEach(this::addProjectOnlyFeatureItemRegisterer);
    }
    /**
     * Removes a project only feature item registerer from the list of item registerers.
     * The registerer is initialised with the current instance of the FeatureRegisterer
     * ProjectOnlyFeatureItemRegisterer are used to register/unregister project only feature items of a feature
     * and will be called when a feature is registered/unregistered
     * @param featureItemRegisterer
     */
    public void removeProjectOnlyFeatureItemRegisterer(IProjectOnlyFeatureItemRegisterer featureItemRegisterer){
        this.projectOnlyFeatureItemRegisters.remove(featureItemRegisterer);
    }
    /**
     * Removes a list of project only feature item registerers from the list of item registerers,
     * see {@link FeatureRegisterer#removeProjectOnlyFeatureItemRegisterer(IProjectOnlyFeatureItemRegisterer)}
     * @param featureItemRegisterers
     */
    public void removeAllProjectOnlyFeatureItemRegisterer(List<? extends IProjectOnlyFeatureItemRegisterer> featureItemRegisterers){
        featureItemRegisterers.forEach(this::removeProjectOnlyFeatureItemRegisterer);
    }

    //-------------------------------- GETTER / SETTER --------------------------------------------
    /**
     * Returns all registered features
     * @return
     */
    public List<MDFeature> getRegisteredFeatures() {
        return registeredFeatures;
    }
    public void setRegisteredFeatures(List<MDFeature> registeredFeatures) {
        this.registeredFeatures = registeredFeatures;
    }

    /**
     * Get the plugin instance
     * @return
     */
    public APlugin getPlugin() {
        return plugin;
    }
}
