package com.samares_engineering.omf.omf_example_plugin.features.miscergo

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_example_plugin.features.miscergo.action.OpenSpecificationFromPart
import java.util.List.*

class MiscErgonomicFeature : SimpleFeature("MISC Ergonomic feature") {
    override fun initFeatureActions(): List<UIAction> {
        return listOf<UIAction>(OpenSpecificationFromPart())
    }
}
