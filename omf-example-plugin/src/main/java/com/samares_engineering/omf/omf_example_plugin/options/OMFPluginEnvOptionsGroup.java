/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin.options;

import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;

public class OMFPluginEnvOptionsGroup extends OMFPropertyOptionsGroup {

    private static final String DEFAULT_OMF_PLUGIN_CATEGORY_NAME = "OMF Plugin";

    public OMFPluginEnvOptionsGroup() {
        this(DEFAULT_ID, DEFAULT_OMF_PLUGIN_CATEGORY_NAME);
    }
    public OMFPluginEnvOptionsGroup(String id, String omfPluginCategoryName) {
        super(id, omfPluginCategoryName);
    }

}
