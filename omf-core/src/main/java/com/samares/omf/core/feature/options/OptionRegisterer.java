package com.samares.omf.core.feature.options;

import com.samares.omf.core.feature.IFeatureRegisterer;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.feature.errors.FeatureException;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.errors.OMFErrorHandler;
import com.samares.omf.core.errors.exceptions.GenericException;

import java.util.Objects;

public class OptionRegisterer implements IFeatureRegisterer {


    /**
     * Register all the Environment options of the feature.
     * By default, the registration will be delegated to the IOptions itself.
     * @param feature
     */
    public void registerEnvOptions(MDFeature feature){
        try {
            feature.getOptions().stream()
                    .filter(IOption::isActivated)
                    .filter(opt -> opt.getKind() == OptionKind.Environment)
                    .filter(Objects::nonNull)
                    .forEach(IOption::register);
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("[Feature] Could not register Environment option for feature: " + feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }
    /**
     * Unregister all the Environment options of the feature.
     * By default, the removal will be delegated to the IOptions itself.
     * @param feature
     */
    public void unregisterEnvOptions(MDFeature feature){
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
    public void registerProjectOptions(MDFeature feature){
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
    public void unregisterProjectOptions(MDFeature feature){
        try {
            feature.getOptions().stream()
                    .filter(opt -> opt.getKind() == OptionKind.Project)
                    .forEach(IOption::unregister);
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("[Feature] Could not register Project option for feature: " + feature.getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    /**
     * Register all the options of the feature depending on its kind.
     * By default, the removal will be delegated to the IOptions itself.
     * @param feature
     */
    public void registerAllOptions(MDFeature feature){
        registerEnvOptions(feature);
        if(OMFUtils.currentProject != null)
            registerProjectOptions(feature);
    }

    /**
     * unregister all the options of the feature depending on its kind.
     * By default, the removal will be delegated to the IOptions itself.
     * @param feature
     */
    public void unregisterALLOptions(MDFeature feature){
        unregisterEnvOptions(feature);
        unregisterProjectOptions(feature);
    }


    @Override
    public void registerFeature(MDFeature mdFeature) {
        registerAllOptions(mdFeature);
    }

    @Override
    public void unregisterFeature(MDFeature mdFeature) {
        unregisterALLOptions(mdFeature);

    }
}
