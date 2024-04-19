package com.samares_engineering.omf.omf_example_plugin.features.hooks

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.LifeCycleHook

class HookExampleFeature : SimpleFeature("Hook Example Feature" ){

    override fun initLifeCycleHooks(): MutableList<LifeCycleHook> {
        return listOf).toMutableList()
    }


    class OnProjectHookExample : OnProjectHook() {
        override fun onProjectOpened() {
            TODO("Not yet implemented")
        }

        override fun onProjectClosed() {
            TODO("Not yet implemented")
        }

        override fun onProjectCreated() {
            TODO("Not yet implemented")
        }

        override fun onProjectDeleted() {
            TODO("Not yet implemented")
        }

        override fun onProjectSaved() {
            TODO("Not yet implemented")
        }

    }
}