/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.display

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_example_plugin.features.display.actions.DisplayInnerLayer

/**
 * Feature that can be used to display inner layers.
 */
class EnhancedDisplayFeature : SimpleFeature("Display Feature") {
    public override fun initFeatureActions(): List<UIAction> {
        return java.util.List.of<UIAction>(
            DisplayInnerLayer()
        )
    }
}
