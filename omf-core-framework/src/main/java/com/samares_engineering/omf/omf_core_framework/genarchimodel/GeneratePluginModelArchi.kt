/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.genarchimodel

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_core_framework.genarchimodel.actions.GenerateArchiModel_V3
import java.util.*

class GeneratePluginModelArchi : SimpleFeature("GeneratePluginModelArchi") {
    public override fun initFeatureActions(): List<UIAction> {
        return Arrays.asList<UIAction>(
            GenerateArchiModel_V3()
        )
    }
}
