package com.samares_engineering.omf.omf_example_plugin.features.optionsexample

import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option
import com.samares_engineering.omf.omf_example_plugin.features.optionsexample.options.OptionsExampleOptionHelper

/**
 * Example feature showcasing every MagicDraw property type as both an
 * environment option (global, Options > Environment) and a project option
 * (per-project, Options > Project).
 *
 * See [OptionsExampleOptionHelper] for the full list of supported types and
 * how to read/write each one from code.
 */
class OptionsExampleFeature : SimpleFeature("Options Example") {

    override fun initEnvOptionsHelper(): EnvOptionsHelper = OptionsExampleOptionHelper(this)

    override fun getEnvOptionsHelper(): OptionsExampleOptionHelper =
        super.getEnvOptionsHelper() as OptionsExampleOptionHelper

    override fun initOptions(): List<Option> = getEnvOptionsHelper().allOptions
}
