/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_example_plugin.options;

import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFEnvOptionResources;
import com.samares_engineering.omf.omf_core_framework.ui.environmentoptions.OMFPropertyOptionsGroup;

public class OMFPluginEnvOptionsGroup extends OMFPropertyOptionsGroup {
    public static final String ID = "env.options.omf.plugin";

    private static final String OMF_PLUGIN_CATEGORY_NAME = "OMF Plugin";

    public OMFPluginEnvOptionsGroup() {
        super(ID, OMF_PLUGIN_CATEGORY_NAME);
    }

    @Override
    public String getName() {
        return OMFEnvOptionResources.getString(OMF_PLUGIN_CATEGORY_NAME);
    }
}
