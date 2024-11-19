/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson, Calliopé Danton Laloy
 * @since     0.0.0
 ******************************************************************************/

package com.samares_engineering.omf.omf_core_framework.utils;

import com.nomagic.ci.persistence.PersistenceException;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.ProjectUtilities;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.esi.EsiUtils;
import com.nomagic.magicdraw.teamwork2.ITeamworkService;
import com.nomagic.magicdraw.teamwork2.ServerLoginInfo;
import com.nomagic.task.EmptyProgressStatus;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.GenericException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.LegacyOMFException;

import javax.annotation.CheckForNull;
import java.io.File;
import java.net.URI;
import java.util.Collection;


@SuppressWarnings({"unused", "squid:S106", "ConstantConditions"})
public class TwcAccessor
{

    private static String serverAddress;
    private static String serverUser;
    private static String userPassword;
    private static ITeamworkService iTeamworkService;
    public String projectName;


    public TwcAccessor(String serverAddress, String serverUser, String userPassword){
        TwcAccessor.serverAddress = serverAddress;
        TwcAccessor.serverUser = serverUser;
        TwcAccessor.userPassword = userPassword;
        TwcAccessor.iTeamworkService = login();
    }


    /**
     * Login to the twc server
     * @return ITeamworkService
     */
    public ITeamworkService login() {
        ITeamworkService iTeamworkService = EsiUtils.getTeamworkService();
        iTeamworkService.login(new ServerLoginInfo(serverAddress, serverUser, userPassword, false), true);

        boolean isConnected = iTeamworkService.isConnected();
        assert isConnected;

        System.out.println("------------------- Connected to TeamWork Cloud -------------------");

        return iTeamworkService;
    }


    /**
     * Open a project on twc server with the project name as parameter
     * @param projectName String
     * @throws LegacyOMFException Exception
     */
    public void openProject(String projectName) throws LegacyOMFException {
        ProjectDescriptor projectDescriptor;
        try {
            projectDescriptor = iTeamworkService.getProjectDescriptorByQualifiedName(projectName);
        }catch (Exception exception){
            throw new LegacyOMFException("[TWC Accessor]- Cannot open project : " + projectName, GenericException.ECriticality.CRITICAL);
        }

        if(projectDescriptor != null)
            Application.getInstance().getProjectsManager().loadProject(projectDescriptor, EmptyProgressStatus.getDefault());
    }


    /**
     * Open the project's branch on twc server
     * @param projectName String
     * @param branchName String
     * @throws LegacyOMFException Exception
     */
    public void openBranchProject(String projectName, String branchName) throws LegacyOMFException {
        ProjectDescriptor projectDescriptor;
        try {
            projectDescriptor = iTeamworkService.getProjectDescriptorByQualifiedName(projectName);
        }catch (Exception exception){
            throw new LegacyOMFException("[TWC Accessor]- Cannot open project : " + projectName, GenericException.ECriticality.CRITICAL);
        }

        if(projectDescriptor != null){
            final ProjectDescriptor branchDescriptor = EsiUtils.getDescriptorForBranch(projectDescriptor, branchName);
            Application.getInstance().getProjectsManager().loadProject(branchDescriptor,EmptyProgressStatus.getDefault());
        }
    }


    /**
     * Create a project's branch on twc server
     * @param projectName String
     * @param branchName String
     * @param branchDescription String
     * @throws LegacyOMFException Exception
     */
    public void createBranch(String projectName, String branchName, String branchDescription) throws LegacyOMFException {

        ProjectDescriptor projectDescriptor;
        try {
            projectDescriptor = iTeamworkService.getProjectDescriptorByQualifiedName(projectName);
        } catch (Exception exception) {
            throw new LegacyOMFException("[TWC Accessor]- Cannot open project : " + projectName, GenericException.ECriticality.CRITICAL);
        }

        Collection<EsiUtils.EsiBranchInfo> branches = EsiUtils.getBranches(projectDescriptor);

        if(branches.stream().anyMatch(info -> info.getName().equalsIgnoreCase(branchName))){
           EsiUtils.deleteBranch(projectDescriptor, branchName);
        }

        EsiUtils.createBranch(projectDescriptor, EsiUtils.getLastVersion(projectDescriptor), branchName, branchDescription);
    }

    public void saveFromTwcToLocal(String projectName, String localPath) throws LegacyOMFException {
        openProject(projectName);
        File file = new File(localPath, projectName + ".mdzip");
        EsiUtils.convertToLocal(OMFUtils.getProject(), file);
    }

    /**
     * Create new project in the twc server
     * @param projectName String
     * @return created project
     * @throws LegacyOMFException Exception if project cannot be created
     */
    public Project createProject(String projectName) throws LegacyOMFException {
        final Project project;
        try {
            project = EsiUtils.createProject("Project1", "category");
        } catch (PersistenceException e) {
            throw new LegacyOMFException("[TWC Accessor]- Cannot create project : " + projectName, GenericException.ECriticality.CRITICAL);
        }

        final org.eclipse.emf.common.util.URI locationURI = project.getPrimaryProject().getLocationURI();
        ProjectDescriptor projectDescriptorFound = getExistingProjectDescriptor(locationURI);

        try {
            EsiUtils.setProjectName(projectDescriptorFound, projectName);
        } catch (PersistenceException e) {
            throw new LegacyOMFException("[TWC Accessor]- Cannot set name of project : " + projectName, GenericException.ECriticality.CRITICAL);
        }

        return project;
    }


    /**
     * Find project descriptor for given URI
     * @param locationURI project URI
     * @return descriptor for project.
     * @throws LegacyOMFException Exception
     */
    @CheckForNull
    private ProjectDescriptor getExistingProjectDescriptor(org.eclipse.emf.common.util.URI locationURI) throws LegacyOMFException
    {
        final URI projectURI = ProjectUtilities.getURI(locationURI);
        try {
            return EsiUtils.getRemoteProjectDescriptors().stream()
                    .filter(projectDescriptor -> projectDescriptor.getURI().equals(projectURI))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            throw new LegacyOMFException("[TWC Accessor]- Cannot access project URI: " + projectURI, GenericException.ECriticality.CRITICAL);
        }
    }


    /**
     * Find project descriptor using project name
     * @param projectName project name
     * @return project descriptor or null
     * @throws LegacyOMFException Exception if project descriptor not found
     */
    @CheckForNull
    public ProjectDescriptor getExistingProjectDescriptor(String projectName) throws LegacyOMFException {
        try {
            return iTeamworkService.getProjectDescriptorByQualifiedName(projectName);
        } catch (Exception e) {
            throw new LegacyOMFException("[TWC Accessor]- Cannot access projectDescriptor : " + projectName, GenericException.ECriticality.CRITICAL);
        }
    }

}
