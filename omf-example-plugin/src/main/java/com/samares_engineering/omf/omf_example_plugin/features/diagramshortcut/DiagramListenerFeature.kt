/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_example_plugin.features.diagramshortcut

import com.nomagic.magicdraw.core.Project
import com.nomagic.magicdraw.uml.symbols.DiagramListenerAdapter
import com.nomagic.uml2.ext.magicdraw.classes.mdkernel.Diagram
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.AOnProjectOpenedHook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction.ALiveAction
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.LiveActionEngine
import com.samares_engineering.omf.omf_core_framework.utils.utils.diagrams.DiagramListenerConstants
import com.samares_engineering.omf.omf_example_plugin.features.diagramshortcut.diagramlistener.OnDiagramOpeningListener
import java.beans.PropertyChangeEvent
import java.util.stream.Collectors


/**
 *
 */
class DiagramListenerFeature : SimpleFeature("DIAGRAM_LISTENER_FEATURE") {
    private val diagramListener = OnDiagramOpeningListener()
    private val diagramListenerAdapter: DiagramListenerAdapter
    private val DIAGRAM_OPENED = DiagramListenerConstants.DIAGRAM_OPENED

    init {
        registerShortcutLiveAction()
        diagramListenerAdapter = DiagramListenerAdapter(diagramListener)
    }

    private fun registerShortcutLiveAction() {
        val onDiagramOpenedLiveActions = liveActionEngines.stream()
            .filter { liveActionEngine: LiveActionEngine<*> -> liveActionEngine.type == DIAGRAM_OPENED }
            .collect(Collectors.toList())

        diagramListener.liveActionEngineMap[DIAGRAM_OPENED] = onDiagramOpenedLiveActions
    }

    override fun initLifeCycleHooks(): List<Hook> {
        return listOf<Hook>(
            object : AOnProjectOpenedHook() {
                override fun onProjectOpened(project: Project) {
                    diagramListenerAdapter.install(project)
                }
            }
        )
    }

    override fun initLiveActions(): List<LiveActionEngine<*>> {
        val liveActionEngine: LiveActionEngine<*> = ALiveActionEngine(DIAGRAM_OPENED).apply {
            addLiveAction(object : ALiveAction() {
                override fun eventMatches(evt: PropertyChangeEvent): Boolean {
                    return evt.propertyName == DIAGRAM_OPENED && evt.source is Diagram
                }

                override fun process(e: PropertyChangeEvent): PropertyChangeEvent {
                    return e
                }

                override fun isBlocking(): Boolean {
                    return false
                }
            })
        }
        diagramListener.liveActionEngineMap[DIAGRAM_OPENED] = listOf(liveActionEngine)
        return emptyList()
    }
}
