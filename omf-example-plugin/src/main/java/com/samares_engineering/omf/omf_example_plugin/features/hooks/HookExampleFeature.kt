package com.samares_engineering.omf.omf_example_plugin.features.hooks

import com.nomagic.magicdraw.core.Project
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.BaseHookFeatureItem
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature.OnFeatureRegisteringHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature.OnFeatureUnregisteringHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.OnMagicDrawStartHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.OnProjectCreatedHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.OnProjectOpenedHook

class HookExampleFeature : SimpleFeature("Hook Example Feature") {

    override fun initLifeCycleHooks(): MutableList<Hook> {
        return mutableListOf(OnProjectHookExample())
    }

    override fun onRegistering() {
        super.onRegistering()
    }

    class OnProjectHookExample : BaseHookFeatureItem(),
            OnProjectOpenedHook,
            OnProjectCreatedHook,
            OnMagicDrawStartHook,
            OnFeatureRegisteringHook,
            OnFeatureUnregisteringHook {

        override fun onProjectCreated(project: Project) {
            OMFLogger.warnToSystemConsole("Project created: ${project.name}")
        }

        override fun onProjectOpened(project: Project?) {
            OMFLogger.warnToSystemConsole("Project opened: ${project?.name}")
        }

        override fun onMagicDrawStart() {
            OMFLogger.warnToSystemConsole("MagicDraw started")
        }

        override fun onFeatureRegistering(feature: OMFFeature?) {
            if (feature == getFeature()) {
                return
            }
            OMFLogger.warnToSystemConsole("Feature registering: ${feature?.name}")
        }

        override fun onFeatureUnregistering(feature: OMFFeature?) {
            OMFLogger.warnToSystemConsole("Feature unregistering: ${feature?.name}")
        }
    }
}