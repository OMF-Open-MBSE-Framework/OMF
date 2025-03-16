/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.sysmlv2_explo

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_example_plugin.features.display.actions.DisplayInnerLayer
import com.samares_engineering.omf.omf_example_plugin.features.sysmlv2_explo.actions.ExploSysMLV2


class SysMLV2Feature : SimpleFeature("SysMLv2 Explo Feature") {
    public override fun initFeatureActions(): List<UIAction> {
        return java.util.List.of<UIAction>(
            ExploSysMLV2()
        )
    }
}
