/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.projectonly;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.List;

/**
 * Registerer/Unregister the 'ProjectOnly' Options of a feature.
 * All ProjectOnly Options will be configured when the project is opened, and removed when the project is closed.
 * @see IOption
 */
public class ProjectOnlyOptionRegisterer implements IProjectOnlyFeatureItemRegisterer<IOption> {
    private FeatureRegisterer featureRegister;

    public void init(FeatureRegisterer featureRegisterer) {
        setFeatureRegisterer(featureRegisterer);
    }

    /**
     * Register  all the 'ProjectOnly' Options of the feature depending on its kind.
     * By default, the removal will be delegated to the IOptions itself.
     * @param options
     */
    public void registerFeatureItems(List<IOption> options) {
        options.forEach(this::registerFeatureItem);
    }

    /**
     * unregister all the 'ProjectOnly' options of the feature depending on its kind.
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
            throw new OMFFeatureRegisteringException(
                    "[Feature] Could not register " + option.getKind().toString()
                    + " option for mdFeature: " + option.getFeature().getName());
        }
    }

    public void unregisterFeatureItem(IOption option) {
        try {
            if (option == null) return;
            option.unregister();
        }catch (Exception e){
            throw new OMFFeatureRegisteringException(
                    "[Feature] Could not unregister " + option.getKind().toString() + " option from mdFeature: " +
                            option.getFeature().getName(), e);
        }
    }

    @Override
    public void registerFeatureItems(MDFeature feature) {
        registerFeatureItems(feature.getProjectOnlyOptions());
    }

    @Override
    public void unregisterFeatureItems(MDFeature feature) {
        unregisterFeatureItems(feature.getProjectOnlyOptions());
    }

    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegister;
    }

    @Override
    public void setFeatureRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegister = featureRegisterer;
    }
}
