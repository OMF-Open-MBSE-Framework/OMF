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
     * Register a feature using delegation to register MDActions and RuleEngines. Return true if the feature is already registered;
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

    public void registerFeatures(List<MDFeature> features){
        features.forEach(this::registerFeature);
    }

    /**
     * Registers feature items that have declared as "project only" until project is opened. This method is then called
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

    public void registerProjectOnlyItemsOfFeatures(List<MDFeature> features) {
        features.forEach(this::registerProjectOnlyFeatureItems);
    }

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
            feature.setIsRegistered(false);

        }catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while unregistering items for feature " +
                    feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    public void unregisterFeatures(List<MDFeature> features){
        new ArrayList<>(features).forEach(this::unregisterFeature); //New Arraylist to manage List modifications while iterating
    }

    public void unregisterDelayedItemsOfFeatures(List<MDFeature> features){
        new ArrayList<>(features).forEach(this::unregisterDelayedItemsOfFeature);//New Arraylist to manage List modifications while iterating
    }

    public void unregisterDelayedItemsOfFeature(MDFeature feature){
        projectOnlyFeatureItemRegisters.forEach(registerer -> {
            try {
                registerer.unregisterFeature(feature);
            }catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while unregistering project only items for feature " +
                    feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }});
    }

    public boolean isAlreadyRegistered(MDFeature mdFeature) {
        return registeredFeatures.stream().anyMatch(mdFeature.getClass()::isInstance);
    }

    public void addIFeatureItemRegisterer(IFeatureItemRegisterer featureItemRegisterer){
       try {
           this.featureItemRegisters.add(featureItemRegisterer);
           featureItemRegisterer.init(this);
       }catch (FeatureException e) {
           OMFErrorHandler.handleException(new FeatureException("Error while adding feature item registerer " +
                   featureItemRegisterer.getClass().getName(), e, GenericException.ECriticality.CRITICAL), false);
       }
    }
    public void addAllIFeatureItemRegisterer(List<? extends IFeatureItemRegisterer> featureItemRegisterers){
        featureItemRegisterers.forEach(this::addIFeatureItemRegisterer);
    }

    public void removeIFeatureItemRegisterer(IFeatureItemRegisterer featureItemRegisterer){
        this.featureItemRegisters.remove(featureItemRegisterer);
    }
    public void removeAllIFeatureItemRegisterer(List<? extends IFeatureItemRegisterer> featureItemRegisterers){
        featureItemRegisterers.forEach(this::removeIFeatureItemRegisterer);
    }


    public void addProjectOnlyFeatureItemRegisterer(IProjectOnlyFeatureItemRegisterer featureItemRegisterer){
        try{
            this.projectOnlyFeatureItemRegisters.add(featureItemRegisterer);
            featureItemRegisterer.init(this);
        }catch (FeatureException e) {
            OMFErrorHandler.handleException(new FeatureException("Error while adding project only feature item registerer " +
                    featureItemRegisterer.getClass().getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }
    public void addAllProjectOnlyFeatureItemRegisterer(List<? extends IProjectOnlyFeatureItemRegisterer> featureItemRegisterers){
        featureItemRegisterers.forEach(this::addProjectOnlyFeatureItemRegisterer);
    }

    public void removeProjectOnlyFeatureItemRegisterer(IProjectOnlyFeatureItemRegisterer featureItemRegisterer){
        this.projectOnlyFeatureItemRegisters.remove(featureItemRegisterer);
    }
    public void removeAllProjectOnlyFeatureItemRegisterer(List<? extends IProjectOnlyFeatureItemRegisterer> featureItemRegisterers){
        featureItemRegisterers.forEach(this::removeProjectOnlyFeatureItemRegisterer);
    }

    //-------------------------------- GETTER / SETTER --------------------------------------------

    public List<MDFeature> getRegisteredFeatures() {
        return registeredFeatures;
    }
    public void setRegisteredFeatures(List<MDFeature> registeredFeatures) {
        this.registeredFeatures = registeredFeatures;
    }

    public APlugin getPlugin() {
        return plugin;
    }
}
