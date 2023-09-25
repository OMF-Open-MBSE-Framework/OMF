/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.List;

public class OptionFeatureItemRegisterer implements FeatureItemRegisterer<IOption> {
    FeatureRegisterer featureRegisterer;

    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        setFeatureRegisterer(featureRegisterer);
    }

    /**
     * Register all the options of the feature depending on its kind.
     * By default, the removal will be delegated to the IOptions itself.
     * @param options
     */
    public void registerFeatureItems(List<IOption> options) {
        options.forEach(this::registerFeatureItem);
    }

    /**
     * unregister all the options of the feature depending on its kind.
     * By default, the removal will be delegated to the IOptions itself.
     * @param options
     */
    public void unregisterFeatureItems(List<IOption> options){
        options.forEach(this::unregisterFeatureItem);
    }

    public void registerFeatureItem(IOption option) {
        try {
            if (option == null || !option.isActivated()) return;
            if (option.getKind() == OptionKind.Project && OMFUtils.currentProject == null) return;

            option.register();
        }catch (Exception e){
            throw new OMFFeatureRegisteringException("Could not register " + option.getKind().toString()
                    + " option for mdFeature: " + option.getFeature().getName());
        }
    }

    public void unregisterFeatureItem(IOption option) {
        try {
            if (option == null) return;
            option.unregister();
        }catch (Exception e){
            throw new OMFFeatureRegisteringException("Could not unregister " + option.getKind().toString()
                    + " option from mdFeature: " + option.getFeature().getName());
        }
    }

    @Override
    public void registerFeatureItems(MDFeature feature) {
        registerFeatureItems(feature.getOptions());
    }

    @Override
    public void unregisterFeatureItems(MDFeature feature) {
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
}
