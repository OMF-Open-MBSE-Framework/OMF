/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_example_plugin.features.display;

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_example_plugin.features.display.actions.DisplayInnerLayer;

import java.util.List;

/**
 *  Feature that can be used to display inner layers.
 */
public class EnhancedDisplayFeature extends SimpleFeature {

    public EnhancedDisplayFeature(){
       super("Display Feature");
    }


    @Override
    public List<UIAction> initFeatureActions() {
        return List.of(
                new DisplayInnerLayer()
        );
    }


}
