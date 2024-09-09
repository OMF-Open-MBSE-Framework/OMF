/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author: Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since 0.0.0
 */
package com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFBarrierExecutor
import com.samares_engineering.omf.omf_core_framework.feature.OMFAutomationManager
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.exceptions.ErrorWhileEvaluationLiveActionException
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.LiveAction
import com.samares_engineering.omf.omf_core_framework.listeners.IListenerManager
import java.util.*
import java.util.concurrent.Callable
import java.util.function.Consumer

class LiveActionEngineImpl(category: LiveActionType, priority: Int = -1) :
    LiveActionEngine<CharacterizedEvent> {
    var listenerManager: IListenerManager? = null
    private var liveActions: MutableList<LiveAction<CharacterizedEvent, CharacterizedEvent>> = ArrayList()
    var id: String = ""
    private var priority = -1
    private var category = ""
    private var feature: OMFFeature? = null
    private var activated = true


    init {
        this.category = category.toString()
        this.priority = priority
    }

    override fun initRegistrableItem(feature: OMFFeature) {
        this.feature = feature
        listenerManager = feature.plugin.listenerManager
    }

    override fun activate() {
        this.activated = true
    }

    override fun deactivate() {
        this.activated = false
    }

    override fun isActivated(): Boolean {
        return activated
    }

    /**
     * Find the highest priority liveAction (if it exists) matching the provided event
     *
     * @param characterizedEvent event to process
     * @return the liveAction found
     */
    override fun getMatchingLiveAction(characterizedEvent: CharacterizedEvent): Optional<LiveAction<CharacterizedEvent, CharacterizedEvent>> {
        if (skipLiveActions(characterizedEvent)) {
            return Optional.empty()
        }
        return liveActions.stream()
            .filter { liveAction: LiveAction<CharacterizedEvent, CharacterizedEvent> ->
                isLiveActionMatching(
                    characterizedEvent,
                    liveAction
                )
            }
            .findFirst() ?: Optional.empty()
    }



    override fun getAllMatchingLiveActions(characterizedEvent: CharacterizedEvent): List<LiveAction<CharacterizedEvent, CharacterizedEvent>> {
        if (skipLiveActions(characterizedEvent)) return ArrayList()

        val liveActionsToExecute: MutableList<LiveAction<CharacterizedEvent, CharacterizedEvent>> = ArrayList()

        for (liveAction in liveActions) {  //return all matching liveActions until the first Blocking liveAction is found
            if (isLiveActionMatching(characterizedEvent, liveAction)) {
                liveActionsToExecute.add(liveAction)

                if (liveAction.isBlocking) break
            }
        }
        return liveActionsToExecute
    }


    private fun isLiveActionMatching(
        characterizedEvent: CharacterizedEvent,
        liveAction: LiveAction<CharacterizedEvent, CharacterizedEvent>
    ): Boolean {
        val isMatching = OMFBarrierExecutor.executeWithinBarrier<Boolean>({
            try {
                return@executeWithinBarrier liveAction.isActivated && liveAction.matches(characterizedEvent)
            } catch (e: Exception) {
                throw ErrorWhileEvaluationLiveActionException(liveAction, e)
            }
        }, getFeature())
        return isMatching != null && isMatching
    }

    /**
     * Finds and processes the highest priority liveAction (if it exists) matching the provided event
     * In case of a blocking liveAction, the processing stops after the first blocking liveAction has been processed
     * In case of error, the error is handled by the ErrorHandler2, which may throw a RollbackException
     *
     * @param CharacterizedEvent event to process
     * @return true if a matching liveAction has been found and processed, false otherwise
     */
    override fun processAllMatchingLiveActions(CharacterizedEvent: CharacterizedEvent): Boolean {
        val matchingLiveActions = getAllMatchingLiveActions(CharacterizedEvent)
        if (matchingLiveActions.isEmpty()) return false


        matchingLiveActions.forEach(Consumer { liveAction: LiveAction<CharacterizedEvent, CharacterizedEvent> ->
            OMFBarrierExecutor.executeInSessionWithinBarrier(
                { liveAction.process(CharacterizedEvent) }, getFeature(), !liveAction.keepListenerActivated()
            )
        })

        OMFAutomationManager.getInstance().automationTriggered()
        return true
    }

    /*
    Accessors
     */
    override fun getPriority(): Int {
        return priority
    }

    override fun setPriority(priority: Int) {
        this.priority = priority
    }

    override fun getType(): String {
        return category
    }

    override fun setType(category: String) {
        this.category = category
    }

    override fun getFeature(): OMFFeature {
        return feature!!
    }

    override fun skipLiveActions(CharacterizedEvent: CharacterizedEvent): Boolean {
        return false
    }

    override fun addLiveAction(liveAction: LiveAction<CharacterizedEvent, CharacterizedEvent>) {
        liveAction.liveActionEngine = this
        liveActions.add(liveAction)
    }

    override fun addAllLiveActions(liveActions: List<LiveAction<CharacterizedEvent, CharacterizedEvent>>) {
        liveActions.forEach(Consumer { liveAction: LiveAction<CharacterizedEvent, CharacterizedEvent> ->
            this.addLiveAction(
                liveAction
            )
        })
    }

    override fun removeLiveAction(liveAction: LiveAction<CharacterizedEvent, CharacterizedEvent>) {
        liveActions.remove(liveAction)
    }

    override fun removeLiveActions(liveActions: List<LiveAction<CharacterizedEvent, CharacterizedEvent>>) {
        this.liveActions.removeAll(liveActions)
    }

    override fun removeAllLiveActions() {
        liveActions.clear()
    }

    override fun getLiveActions(): MutableList<LiveAction<CharacterizedEvent, CharacterizedEvent>> {
        return liveActions
    }

    fun setLiveActions(liveActions: MutableList<LiveAction<CharacterizedEvent, CharacterizedEvent>>) {
        this.liveActions = liveActions
    }
}
