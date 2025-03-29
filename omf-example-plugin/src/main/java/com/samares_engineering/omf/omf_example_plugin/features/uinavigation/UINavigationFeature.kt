package com.samares_engineering.omf.omf_example_plugin.features.uinavigation

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_example_plugin.features.excel_to_parametric.actions.ImportExcelToParametricAction
import com.samares_engineering.omf.omf_example_plugin.features.uinavigation.action.GoToType

class UINavigationFeature: SimpleFeature("UI Navigation") {
    override fun initFeatureActions(): List<UIAction> {
        return listOf(
            GoToType()
        )
    }
}