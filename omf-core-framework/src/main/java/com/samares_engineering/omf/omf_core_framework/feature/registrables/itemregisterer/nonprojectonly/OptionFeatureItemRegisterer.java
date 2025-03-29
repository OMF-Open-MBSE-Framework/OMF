/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.ArrayList;
import java.util.List;

public class OptionFeatureItemRegisterer implements FeatureItemRegisterer<Option> {
    FeatureRegisterer featureRegisterer;
    final List<Option> registeredFeatureItems = new ArrayList<>();

    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        setFeatureRegisterer(featureRegisterer);
    }

    /**
     * Register all the options of the feature depending on its kind.
     * By default, the removal will be delegated to the IOptions itself.
     * @param options the options to register
     */
    public void registerFeatureItems(List<Option> options) {
        options.forEach(this::registerFeatureItem);
    }

    /**
     * Unregister all the options of the feature depending on its kind.
     * By default, the removal will be delegated to the IOptions itself.
     * @param options the options to unregister
     */
    public void unregisterFeatureItems(List<Option> options){
        options.forEach(this::unregisterFeatureItem);
    }

    public void registerFeatureItem(Option option) {
        try {
            if (option == null || !option.isActivated()) return;
            if (option.getKind() == OptionKind.Project && OMFUtils.isProjectVoid()) return;

            option.register();
            registeredFeatureItems.add(option);
        }catch (Exception e){
            throw new FeatureRegisteringException("Could not register " + option.getKind().toString()
                    + " option for mdFeature: " + option.getFeature().getName());
        }
    }

    public void unregisterFeatureItem(Option option) {
        try {
            if (option == null) return;
            option.unregister();
            registeredFeatureItems.remove(option);
        }catch (Exception e){
            throw new FeatureRegisteringException("Could not unregister " + option.getKind().toString()
                    + " option from mdFeature: " + option.getFeature().getName());
        }
    }

    @Override
    public void registerFeatureItems(OMFFeature feature) {
        registerFeatureItems(feature.getOptions());
    }

    @Override
    public void unregisterFeatureItems(OMFFeature feature) {
        unregisterFeatureItems(feature.getOptions());
    }

    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegisterer;
    }

    @Override
    public void setFeatureRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }
    @Override
    public List<Option> getRegisteredFeatureItems() {
        return registeredFeatureItems;
    }
}
