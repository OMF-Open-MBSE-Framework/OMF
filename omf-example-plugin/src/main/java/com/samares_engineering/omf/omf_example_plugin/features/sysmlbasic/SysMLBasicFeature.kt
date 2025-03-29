/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic

import com.samares_engineering.omf.omf_core_framework.feature.EnvOptionsHelper
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.Option
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.actions.DeactivableAction
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.live.creation.CreateAutoInterface_OnPortCreation
import com.samares_engineering.omf.omf_example_plugin.features.sysmlbasic.options.SysMLBasicOptionHelper

class SysMLBasicFeature : SimpleFeature("SysML Basic") {
    override fun initEnvOptionsHelper(): EnvOptionsHelper {
        return SysMLBasicOptionHelper(this)
    }

    override fun getEnvOptionsHelper(): SysMLBasicOptionHelper {
        return super.getEnvOptionsHelper() as SysMLBasicOptionHelper
    }

    public override fun initFeatureActions(): List<UIAction> {
        return listOf<UIAction>( //             new ResetListeners()
            DeactivableAction()
        )
    }

    public override fun initLiveActions(): List<LiveActionEngine<*>> {
        val creationRE = ALiveActionEngine(LiveActionType.CREATE)
        creationRE.addLiveAction(CreateAutoInterface_OnPortCreation())

        val modificationRE = ALiveActionEngine(LiveActionType.UPDATE)
        modificationRE.addLiveAction(CreateAutoInterface_OnPortCreation())

        return listOf(creationRE)
    }


    public override fun initOptions(): List<Option> {
        return envOptionsHelper.allOptions
    }
}
