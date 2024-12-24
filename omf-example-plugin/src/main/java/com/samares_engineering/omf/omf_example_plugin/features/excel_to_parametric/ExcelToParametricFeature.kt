/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.actions.ImportExcelToParametricAction

/**
 *
 */
class ExcelToParametricFeature : SimpleFeature("Excel to Parametric") {
    public override fun initFeatureActions(): List<UIAction> {
        return listOf(
            ImportExcelToParametricAction()
        )
    }
}
