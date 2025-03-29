package com.samares_engineering.omf.omf_core_framework.utils.twc


import com.nomagic.magicdraw.core.Project
import com.nomagic.magicdraw.core.project.ProjectDescriptor
import com.nomagic.magicdraw.esi.EsiUtils
import com.nomagic.magicdraw.teamwork2.ITeamworkService
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils
import com.samares_engineering.omf.omf_core_framework.utils.twc.exceptions.NotConnectedToTWC
import com.samares_engineering.omf.omf_core_framework.utils.twc.exceptions.ProjectNotFoundInTWC
import com.samares_engineering.omf.omf_core_framework.utils.twc.exceptions.TWCServicesNotAvailable

/**
 * TWCUtils is a utility class that provides methods to interact with Teamwork Cloud.
 */
object TWCUtils {

    private val iTeamworkService: ITeamworkService get() = EsiUtils.getTeamworkService() ?: throw TWCServicesNotAvailable()

    /**
     * Commits the current project to TWC.
     * @param commitMessage the commit message
     * @param project the project to commit
     * @param unlockedAfterCommit if the project should be unlocked after the commit
     * @throws NotConnectedToTWC if the user is not connected to TWC
     * @throws ProjectNotFoundInTWC if the project is not found in TWC
     * @throws TWCServicesNotAvailable if TWC services are not available
     */
    @JvmStatic
    fun commit(commitMessage: String,
               project: Project = OMFUtils.getProject(),
               unlockedAfterCommit:Boolean = false) {

        checkTWOProjectAvailability(project)

        val lockService = EsiUtils.getLockService(project)
            ?: throw NotConnectedToTWC()

        val lockedElements = lockService.lockedByMe
        val lockedModules = lockService.modulesLockedByMe

        EsiUtils.commitProject(
            project,
            commitMessage,
            lockedElements,
            lockedModules,
            unlockedAfterCommit,
            emptyList()
        )
    }

    /**
     * Checks if the current project is available in TWC.
     * @param project the project to check
     * @throws NotConnectedToTWC if the user is not connected to TWC
     * @throws ProjectNotFoundInTWC if the project is not found in TWC
     * @throws TWCServicesNotAvailable if TWC services are not available
     */
    @JvmStatic
    fun checkIsItTWCProject(project:Project = OMFUtils.getProject()) {
        checkIfTWCIsAvailable()
        checkTWOProjectAvailability(project)
    }
    /**
     * Checks if the current project is available in TWC.
     * @param project the project to check
     * @throws NotConnectedToTWC if the user is not connected to TWC
     * @throws ProjectNotFoundInTWC if the project is not found in TWC
     * @throws TWCServicesNotAvailable if TWC services are not available
     */
    @JvmStatic
    fun checkTWOProjectAvailability(project: Project) {
        try {
            getTWCProjectDescriptor(project)?: throw Exception()
        } catch (e: Exception) {
            throw ProjectNotFoundInTWC()
        }
    }

    /**
     * Gets the project descriptor of the current project.
     * @param project the project to get the descriptor from
     * @return the project descriptor
     * @throws NotConnectedToTWC if the user is not connected to TWC
     * @throws ProjectNotFoundInTWC if the project is not found in TWC
     * @throws TWCServicesNotAvailable if TWC services are not available
     */
    @JvmStatic
    fun getTWCProjectDescriptor(project: Project): ProjectDescriptor? =
        iTeamworkService.getProjectDescriptorByQualifiedName(project.name)

    /**
     * Is the current project a TWC project, by checking the project descriptor existence.
     */
    @JvmStatic
    fun isItTWCProject(project: Project = OMFUtils.getProject()): Boolean = getTWCProjectDescriptor(project) != null

    /**
     * Is TWC available, by checking the twc services connection.
     */
    @JvmStatic
    val isTWCAvailable: Boolean get() = iTeamworkService.isConnected

    /**
     * Checks if twc is available.
     * @throws NotConnectedToTWC if the user is not connected to TWC
     */
    @JvmStatic
    fun checkIfTWCIsAvailable() {
        if (isTWCAvailable) throw NotConnectedToTWC()
    }

}