/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.genarchimodel;

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_example_plugin.features.genarchimodel.actions.GenerateArchiModel;

import java.util.Arrays;
import java.util.List;

public class GeneratePluginModelArchi extends SimpleFeature {

    public GeneratePluginModelArchi(){
       super("GeneratePluginModelArchi");
    }

    @Override
    public List<UIAction> initFeatureActions() {
        return Arrays.asList(
                new GenerateArchiModel()
        );
    }
}
