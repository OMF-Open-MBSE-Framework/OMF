/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_public_features.activablefeatureoption.options;

import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;

/**
 * FeatureManagerOptionGroup is a group of options in the Environment Options
 * dedicated to the management of the features of the plugin.
 */
public class FeatureActivationManagerOptionGroup extends OMFPropertyOptionsGroup {

    public FeatureActivationManagerOptionGroup(String ID, String categoryName) {
        super(ID, categoryName);
    }
}
