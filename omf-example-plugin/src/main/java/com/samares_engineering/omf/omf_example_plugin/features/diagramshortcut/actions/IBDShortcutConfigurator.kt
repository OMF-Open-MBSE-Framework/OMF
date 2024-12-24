package com.samares_engineering.omf.omf_example_plugin.features.diagramshortcut.actions

import com.nomagic.actions.ActionsManager
import com.nomagic.magicdraw.actions.ActionsConfiguratorsManager
import com.nomagic.magicdraw.actions.ActionsProvider
import com.nomagic.magicdraw.ui.actions.BaseDiagramShortcutsConfigurator
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer
import com.samares_engineering.omf.omf_core_framework.feature.RegistrableFeatureItem
import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction
import java.lang.reflect.ParameterizedType

class IBDShortcutConfigurator(
    private val featureRegisterer: FeatureRegisterer,
    private val scope: String = "COMMON_DIAGRAMS"
) : BaseDiagramShortcutsConfigurator() {


    override fun configureCommonActions(actionsManager: ActionsManager) {
        super.configureCommonActions(actionsManager, null)
        val mdActionsManager = ActionsProvider.getInstance().creator.createGeneralActions()
        val diagramCategory = mdActionsManager.getCategory(scope)
            ?: throw IllegalStateException("COMMON_DIAGRAMS category not found")

        actionsManager.addCategory(diagramCategory)
        getUIActionFeatureRegister().stream()
            .map { it as UIAction }
            .forEach { uiAction -> diagramCategory.addAction(uiAction.diagramAction) }

    }

    private fun getUIActionFeatureRegister(): List<RegistrableFeatureItem> =
        featureRegisterer.featureItemRegisters
            .filter { implementsUIAction(it::class.java, UIAction::class.java) }
            .flatMap { it.registeredFeatureItems }


    override fun configure(var1: ActionsManager) {
        this.configureCommonActions(var1)
    }

    fun register() {
        val actionManager = ActionsConfiguratorsManager.getInstance()
        actionManager.addAnyDiagramShortcutsConfigurator(this)
    }

    fun implementsUIAction(classRegisterer: Class<*>, classFeatureItem: Class<*>): Boolean {
        return classRegisterer.genericInterfaces
            .filterIsInstance<ParameterizedType>()
            .any { parameterizedType ->
                parameterizedType.rawType == RegistrableFeatureItem::class.java &&
                        parameterizedType.actualTypeArguments[0] == classFeatureItem
            }
    }

}