/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares.omf.core.feature.registrables.options;

import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.exceptions.GenericException;
import com.samares.omf.core.feature.FeatureItemRegisterer;
import com.samares.omf.core.feature.FeatureRegisterer;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.feature.errors.FeatureException;
import com.samares.omf.core.feature.registrables.options.option.IOption;
import com.samares.omf.core.feature.registrables.options.option.OptionKind;
import com.samares.omf.core.utils.OMFUtils;

import java.util.List;
import java.util.Objects;

public class OptionRegisterer extends FeatureItemRegisterer<IOption> {
    public OptionRegisterer(FeatureRegisterer featureRegisterer) {
        super(featureRegisterer);
    }

    /**
     * Register all the options of the feature depending on its kind.
     * By default, the removal will be delegated to the IOptions itself.
     * @param mdFeature
     */
    public void register(MDFeature mdFeature) {
        registerEnvOptions(mdFeature);
        if(OMFUtils.currentProject != null)
            registerProjectOptions(mdFeature);
    }

    /**
     * unregister all the options of the feature depending on its kind.
     * By default, the removal will be delegated to the IOptions itself.
     * @param feature
     */
    public void unregister(MDFeature feature){
        unregisterEnvOptions(feature);
        unregisterProjectOptions(feature);
    }


    /**
     * Register all the Environment mdFeature of the mdFeature.
     * By default, the registration will be delegated to the IOptions itself.
     * @param mdFeature
     */
    private void registerEnvOptions(MDFeature mdFeature){
        try {
            mdFeature.getOptions().stream()
                    .filter(IOption::isActivated)
                    .filter(opt -> opt.getKind() == OptionKind.Environment)
                    .filter(Objects::nonNull)
                    .forEach(IOption::register);
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("[Feature] Could not register Environment option for mdFeature: " + mdFeature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }
    /**
     * Unregister all the Environment options of the feature.
     * By default, the removal will be delegated to the IOptions itself.
     * @param feature
     */
    private void unregisterEnvOptions(MDFeature feature){
        try {
            feature.getOptions().stream()
                    .filter(opt -> opt.getKind() == OptionKind.Environment)
                    .filter(Objects::nonNull)
                    .forEach(IOption::unregister);
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("[Feature] Could not unregister Environment option for feature: " + feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    /**
     * Register all the Project options of the feature.
     * By default, the removal will be delegated to the IOptions itself.
     * @param feature
     */
    private void registerProjectOptions(MDFeature feature){
        try {
            feature.getOptions().stream()
                    .filter(IOption::isActivated)
                    .filter(opt -> opt.getKind() == OptionKind.Project)
                    .forEach(IOption::register);
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("[Feature] Could not register Project option for feature: " + feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    /**
     * Unregister all the Project options of the feature.
     * By default, the removal will be delegated to the IOptions itself.
     * @param feature
     */
    private void unregisterProjectOptions(MDFeature feature){
        try {
            feature.getOptions().stream()
                    .filter(opt -> opt.getKind() == OptionKind.Project)
                    .forEach(IOption::unregister);
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("[Feature] Could not register Project option for feature: " + feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }
}
