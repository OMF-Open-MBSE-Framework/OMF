/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.feature.registrables.options;

import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.errors.FeatureException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;

import java.util.List;

public class OptionRegisterer extends FeatureItemRegisterer<IOption> {
    public OptionRegisterer(FeatureRegisterer featureRegisterer) {
        super(featureRegisterer);
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

    protected void registerFeatureItem(IOption option) {
        try {
            if (option == null || !option.isActivated()) {
                return;
            }
            if (option.getKind() == OptionKind.Project && OMFUtils.currentProject == null) {
                return;
            }
            option.register();
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("[Feature] Could not register " + option.getKind().toString()
                    + " option for mdFeature: " + option.getFeature().getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }

    protected void unregisterFeatureItem(IOption option) {
        try {
            if (option == null) {
                return;
            }
            option.unregister();
        }catch (Exception e){
            OMFErrorHandler.handleException(new FeatureException("[Feature] Could not unregister " + option.getKind().toString()
                    + " option from mdFeature: " + option.getFeature().getName(), e, GenericException.ECriticality.CRITICAL), false);
        }
    }
}
