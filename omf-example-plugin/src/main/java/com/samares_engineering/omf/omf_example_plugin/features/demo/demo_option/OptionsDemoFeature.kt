package com.samares_engineering.omf.omf_example_plugin.features.demo.demo_option

import com.nomagic.magicdraw.properties.BooleanProperty
import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionImpl
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.OptionKind
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.actions.DeactivableAction

class OptionsDemoFeature: SimpleFeature("Options Demo") {
    override fun initEnvOptionsHelper(): EnvOptionsHelper {
        return OptionsDemoOptionHelper(this)
    }

    override fun initOptions(): List<Option> {
        return envOptionsHelper.allOptions
    }

    override fun getEnvOptionsHelper(): OptionsDemoOptionHelper {
        return super.getEnvOptionsHelper() as OptionsDemoOptionHelper
    }

    public override fun initFeatureActions(): List<UIAction> {
        return listOf<UIAction>(
            DeactivableAction()
        )
    }
}


//Helper class to manage the options (create, get, set)
class OptionsDemoOptionHelper(feature: OMFFeature): EnvOptionsHelper(feature) {
    //Create the option "DEMO.is DEMO UI Action Available" with default value "false"
    val GROUP = "DEMO"
    val IS_DEMO_UI_ACTION_AVAILABLE = "is DEMO UI Action Available"

    val isDemoUIActionAvailable : Boolean //ACCESSOR
        get() = getPropertyByName(IS_DEMO_UI_ACTION_AVAILABLE).value as Boolean

    val allOptions:List<Option> //LIST OF ALL OPTIONS
        get(){
            val isUIActionAvailable = OptionImpl(
                BooleanProperty(IS_DEMO_UI_ACTION_AVAILABLE, false),
                GROUP,
                feature.plugin.environmentOptionsGroup.get(),
                OptionKind.Environment
            )
            return listOf(isUIActionAvailable)
        }

}








