/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFErrorHandler
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.DevelopmentException
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionType
import com.samares_engineering.omf.omf_core_framework.listeners.IElementListener
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager


class LiveActionEngineFeatureItemRegisterer : FeatureItemRegisterer<LiveActionEngine<*>?> {
    /**
     * Use the IListenerManager to get the different listeners (Analyse, Creation, Update, Delete, AfterAutomation).
     */
    private var listenerManager: IListenerManager? = null
    private var featureRegisterer: FeatureRegisterer? = null
    var registeredFeatureItems1: MutableList<LiveActionEngine<*>> = ArrayList()

    override fun init(featureRegisterer: FeatureRegisterer) {
        this.featureRegisterer = featureRegisterer
        this.listenerManager = featureRegisterer.plugin.listenerManager
    }

    /**
     * Will allow to register a list of LiveActionEngine in the listener.
     * @param liveActionEngines List of LiveActionEngine to register
     */
    override fun registerFeatureItems(liveActionEngines: List<LiveActionEngine<*>?>?) {
        try {
            liveActionEngines!!.forEach{liveActionEngine ->
                this.registerFeatureItem(
                    liveActionEngine
                )
            }
        } catch (e: Exception) {
            throw FeatureRegisteringException("Unable to register LiveActions", e)
        }
    }

    override fun unregisterFeatureItems(liveActionEngines: List<LiveActionEngine<*>?>?) {
        try {
            liveActionEngines!!.forEach{liveActionEngine ->
                this.unregisterFeatureItem(
                    liveActionEngine
                )
            }
        } catch (e: Exception) {
            throw FeatureRegisteringException(" Unable to unregister liveActions", e)
        }
    }

    /**
     * Allow LiveActionEngine registration in the listener. Depending on the Category the LiveActionEngine will be triggered and LiveActions will be evaluated.
     * Category: based on LiveActionEngineUsage it will be used to register the LiveActionEngine in the right place by default (Analyze, Create, Update, Delete, AfterAutomation).
     * @param liveActionEngine: The LiveActionEngine to register
     */
    override fun registerFeatureItem(liveActionEngine: LiveActionEngine<*>?) {
        val category = liveActionEngine!!.type
        val listener = getListenerFromCategory(category)
        val liveActionEngineMap = listener!!.liveActionEngineMap

        liveActionEngineMap.computeIfAbsent(category) { LiveActionEngines: String? -> ArrayList() } //If category absent -> create a new ArrayList

        liveActionEngineMap[category]!!.add(liveActionEngine)
        registeredFeatureItems1.add(liveActionEngine)
    }

    /**
     * Remove a specific LiveActionEngine if registered.
     * Category: based on LiveActionEngineUsage it will be used to register the LiveActionEngine in the right place by default (Analyze, Create, Update, Delete, AfterAutomation).
     * @param LiveActionEngine: The LiveActionEngine to remove
     */
    override fun unregisterFeatureItem(LiveActionEngine: LiveActionEngine<*>?) {
        val category = LiveActionEngine!!.type
        val listener = getListenerFromCategory(category)
        val LiveActionEngineMap = listener!!.liveActionEngineMap
        if (LiveActionEngineMap.containsKey(category)) LiveActionEngineMap[category]!!.remove(LiveActionEngine)
        registeredFeatureItems1.remove(LiveActionEngine)
    }

    /**
     * Allow LiveActionEngine registration in the listener with a specific Priority. Depending on the Category the LiveActionEngine will be triggered and LiveActions will be evaluated.
     * Category: based on LiveActionEngineUsage it will be used to register the LiveActionEngine in the right place by default (Analyze, Create, Update, Delete, AfterAutomation).
     * @param LiveActionEngine: The LiveActionEngine to register
     * @param featurePriority: will help to order the LiveActionEngine execution by its priority.
     */
    private fun addLiveActionEngine(LiveActionEngine: LiveActionEngine<*>, featurePriority: Int) {
        val category = LiveActionEngine.type
        val listener = getListenerFromCategory(category)
        val LiveActionEngineMap = listener!!.liveActionEngineMap

        LiveActionEngineMap.computeIfAbsent(category) { LiveActionEngines: String? -> ArrayList() } //If category absent -> create a new ArrayList

        LiveActionEngineMap[category]!!.add(featurePriority, LiveActionEngine)
    }

    //TODO: Rethink priority management: does the priority is guaranteed ? Priority shall be linked to the RE/Feature
    /**
     * Move LiveActionEngine registration in the listener with to specific, RE will be removed, then add again in the list decreasing the priority of all the other features.
     * Category: based on LiveActionEngineUsage it will be used to register the LiveActionEngine in the right place by default (Analyze, Create, Update, Delete, AfterAutomation).
     * @param LiveActionEngine: The LiveActionEngine to register
     * @param featurePriority: The new pr.
     */
    private fun moveLiveActionEngine(LiveActionEngine: LiveActionEngine<*>, featurePriority: Int) {
        unregisterFeatureItem(LiveActionEngine)
        addLiveActionEngine(LiveActionEngine, featurePriority)
    }

    /**
     * Will return the listener instance
     * @param category the category of the listener
     * @return the listener instance
     */
    private fun getListenerFromCategory(category: String): IElementListener? {
        val liveActionType = LiveActionType.valueOf(category)

        when (liveActionType) {
            LiveActionType.ANALYSE -> return listenerManager!!.analysisListener
            LiveActionType.CREATE -> return listenerManager!!.creationListener
            LiveActionType.UPDATE -> return listenerManager!!.updateListener
            LiveActionType.HISTORY -> return listenerManager!!.historyListener
            LiveActionType.DELETE -> return listenerManager!!.deletionListener
            LiveActionType.AFTER_AUTOMATION -> return listenerManager!!.afterAutomationListener
            LiveActionType.ANALYSE_UNDO_REDO -> return listenerManager!!.analysisListener
            LiveActionType.CREATE_UNDO_REDO -> return listenerManager!!.undoRedoCreationListener
            LiveActionType.UPDATE_UNDO_REDO -> return listenerManager!!.undoRedoUpdateListener
            LiveActionType.HISTORY_UNDO_REDO -> return listenerManager!!.undoRedoHistoryListener
            LiveActionType.DELETE_UNDO_REDO -> return listenerManager!!.undoRedoDeletionListener
            LiveActionType.AFTER_AUTOMATION_UNDO_REDO -> return listenerManager!!.afterAutomationListener


            else -> {
                OMFErrorHandler.getInstance().handleException(DevelopmentException("No Listener found for this category"))
                return null
            }
        }
    }

    override fun registerFeatureItems(feature: OMFFeature) {
        registerFeatureItems(feature.liveActionEngines)
    }

    override fun unregisterFeatureItems(feature: OMFFeature) {
        unregisterFeatureItems(feature.liveActionEngines)
    }

    override fun getFeatureRegisterer(): FeatureRegisterer {
        return featureRegisterer!!
    }

    override fun setFeatureRegisterer(featureRegisterer: FeatureRegisterer) {
        this.featureRegisterer = featureRegisterer
    }

    override fun getRegisteredFeatureItems(): List<LiveActionEngine<*>> {
        return registeredFeatureItems1
    }
}
