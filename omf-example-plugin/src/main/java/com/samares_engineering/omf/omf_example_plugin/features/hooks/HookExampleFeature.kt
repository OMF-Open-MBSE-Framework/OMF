package com.samares_engineering.omf.omf_example_plugin.features.hooks

import com.nomagic.magicdraw.core.Project
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger2
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.BaseHookFeatureItem
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.IHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.IOnProjectCreatedHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.IOnProjectOpenedHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.IOnMagicDrawStartHook

class HookExampleFeature : SimpleFeature("Hook Example Feature" ){

    override fun initLifeCycleHooks(): MutableList<IHook> {
        return mutableListOf(OnProjectHookExample())
    }


    class OnProjectHookExample : BaseHookFeatureItem(),
        IOnProjectOpenedHook,
        IOnProjectCreatedHook,
        IOnMagicDrawStartHook{

        override fun onProjectCreated(project: Project) {
            OMFLogger2.warnToSystemConsole("Project created: ${project.name}")
        }

        override fun onProjectOpened(project: Project?) {
            OMFLogger2.warnToSystemConsole("Project opened: ${project?.name}")
        }

        override fun onMagicDrawStart() {
            OMFLogger2.warnToSystemConsole("MagicDraw started")
        }

    }
}