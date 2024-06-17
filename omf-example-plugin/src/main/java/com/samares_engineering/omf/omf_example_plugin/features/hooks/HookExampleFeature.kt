package com.samares_engineering.omf.omf_example_plugin.features.hooks

import com.nomagic.magicdraw.core.Project
import com.samares_engineering.omf.omf_core_framework.errormanagement2.logging.OMFLogger
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.BaseHookFeatureItem
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.IHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature.IOnFeatureRegisteringHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature.IOnFeatureUnregisteringHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.IOnMagicDrawStartHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.IOnProjectCreatedHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.IOnProjectOpenedHook

class HookExampleFeature : SimpleFeature("Hook Example Feature" ) {

    override fun initLifeCycleHooks(): MutableList<IHook> {
        return mutableListOf(OnProjectHookExample())
    }

    override fun onRegistering() {
        super.onRegistering()
    }

    class OnProjectHookExample : BaseHookFeatureItem(),
        IOnProjectOpenedHook,
        IOnProjectCreatedHook,
        IOnMagicDrawStartHook,
    IOnFeatureRegisteringHook,
    IOnFeatureUnregisteringHook{

        override fun onProjectCreated(project: Project) {
            OMFLogger.warnToSystemConsole("Project created: ${project.name}")
        }

        override fun onProjectOpened(project: Project?) {
            OMFLogger.warnToSystemConsole("Project opened: ${project?.name}")
        }

        override fun onMagicDrawStart() {
            OMFLogger.warnToSystemConsole("MagicDraw started")
        }

        override fun onFeatureRegistering(feature: MDFeature?) {
            if(feature == getFeature()){
                return
            }
            OMFLogger.warnToSystemConsole("Feature registering: ${feature?.name}")
        }

        override fun onFeatureUnregistering(feature: MDFeature?) {
            OMFLogger.warnToSystemConsole("Feature unregistering: ${feature?.name}")
        }
    }
}