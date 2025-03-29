/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 */
package com.samares_engineering.omf.omf_public_features.open_project_onstart

import com.nomagic.magicdraw.core.Application
import com.nomagic.magicdraw.core.project.ProjectDescriptor
import com.nomagic.magicdraw.core.project.ProjectDescriptorsFactory
import com.nomagic.magicdraw.core.project.ProjectsManager
import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.Hook
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.AOnMagicDrawStartHook
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_public_features.apiserver.server.ExtHyperTextServerRouting.openProject
import java.io.File

/**
 * Feature to open a project on MagicDraw start, ONLY AVAILABLE IN DEV MODE
 */
class OpenProjectOnStartFeature(var projectPath:String) : SimpleFeature("Open Project On Start Feature") {

    override fun initLifeCycleHooks(): List<Hook> {
        return listOf<Hook>(object : AOnMagicDrawStartHook() {
            override fun onMagicDrawStart() {
                if(!OMFUtils.isDevMode()) return
                val projectsManager: ProjectsManager = Application.getInstance().getProjectsManager()
                val file = File(projectPath)

                val projectDescriptor: ProjectDescriptor =
                    ProjectDescriptorsFactory.createProjectDescriptor(file.toURI())!!
                projectsManager.loadProject(projectDescriptor, false)
            }
        })
    }
}
