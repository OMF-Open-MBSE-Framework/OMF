/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.deactivablefeatureoption.options;

import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;

/**
 * FeatureManagerOptionGroup is a group of options in the Environment Options
 * dedicated to the management of the features of the plugin.
 */
public class FeatureManagerOptionGroup extends OMFPropertyOptionsGroup {

    public FeatureManagerOptionGroup(String ID, String categoryName) {
        super(ID, categoryName);
    }
}
