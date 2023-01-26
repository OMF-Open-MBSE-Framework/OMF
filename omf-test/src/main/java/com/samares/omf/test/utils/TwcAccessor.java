package com.samares.omf.test.utils;

import com.nomagic.ci.persistence.PersistenceException;
import com.nomagic.magicdraw.core.Application;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.ProjectUtilities;
import com.nomagic.magicdraw.core.project.ProjectDescriptor;
import com.nomagic.magicdraw.esi.EsiUtils;
import com.nomagic.magicdraw.teamwork2.ITeamworkService;
import com.nomagic.magicdraw.teamwork2.ServerLoginInfo;
import com.nomagic.task.EmptyProgressStatus;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.utils.errorManagement.exceptions.GenericException;
import com.samares.omf.core.utils.errorManagement.exceptions.OMFException;

import javax.annotation.CheckForNull;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
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
     * @throws OMFException Exception
     */
    public void openProject(String projectName) throws OMFException {
        ProjectDescriptor projectDescriptor;
        try {
            projectDescriptor = iTeamworkService.getProjectDescriptorByQualifiedName(projectName);
        }catch (Exception exception){
            throw new OMFException("[TWC Accessor]- Cannot open project : " + projectName, GenericException.ECriticality.CRITICAL);
        }

        if(projectDescriptor != null)
            Application.getInstance().getProjectsManager().loadProject(projectDescriptor, EmptyProgressStatus.getDefault());
    }


    /**
     * Open the project's branch on twc server
     * @param projectName String
     * @param branchName String
     * @throws OMFException Exception
     */
    public void openBranchProject(String projectName, String branchName) throws OMFException {
        ProjectDescriptor projectDescriptor;
        try {
            projectDescriptor = iTeamworkService.getProjectDescriptorByQualifiedName(projectName);
        }catch (Exception exception){
            throw new OMFException("[TWC Accessor]- Cannot open project : " + projectName, GenericException.ECriticality.CRITICAL);
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
     * @throws OMFException Exception
     */
    public void createBranch(String projectName, String branchName, String branchDescription) throws OMFException {

        ProjectDescriptor projectDescriptor;
        try {
            projectDescriptor = iTeamworkService.getProjectDescriptorByQualifiedName(projectName);
        } catch (Exception exception) {
            throw new OMFException("[TWC Accessor]- Cannot open project : " + projectName, GenericException.ECriticality.CRITICAL);
        }

        Collection<EsiUtils.EsiBranchInfo> branches = EsiUtils.getBranches(projectDescriptor);

        if(branches.stream().anyMatch(info -> info.getName().equalsIgnoreCase(branchName))){
           EsiUtils.deleteBranch(projectDescriptor, branchName);
        }

        EsiUtils.createBranch(projectDescriptor, EsiUtils.getLastVersion(projectDescriptor), branchName, branchDescription);
    }

    public void saveFromTwcToLocal(String projectName, String localPath) throws OMFException {
        openProject(projectName);
        File file = new File(localPath, projectName + ".mdzip");
        EsiUtils.convertToLocal(OMFUtils.currentProject, file);
    }

    /**
     * Create new project in the twc server
     * @return created project
     * @throws OMFException Exception
     */
    public Project createProject(String projectName) throws OMFException {
        final Project project;
        try {
            project = EsiUtils.createProject("Project1", "category");
        } catch (PersistenceException e) {
            throw new OMFException("[TWC Accessor]- Cannot create project : " + projectName, GenericException.ECriticality.CRITICAL);
        }

        final org.eclipse.emf.common.util.URI locationURI = project.getPrimaryProject().getLocationURI();
        ProjectDescriptor projectDescriptorFound = getExistingProjectDescriptor(locationURI);

        try {
            EsiUtils.setProjectName(projectDescriptorFound, projectName);
        } catch (PersistenceException e) {
            throw new OMFException("[TWC Accessor]- Cannot set name of project : " + projectName, GenericException.ECriticality.CRITICAL);
        }

        return project;
    }


    /**
     * Find project descriptor for given URI
     * @param locationURI project URI
     * @return descriptor for project.
     * @throws OMFException Exception
     */
    @CheckForNull
    private ProjectDescriptor getExistingProjectDescriptor(org.eclipse.emf.common.util.URI locationURI) throws OMFException
    {
        final URI projectURI = ProjectUtilities.getURI(locationURI);
        try {
            return EsiUtils.getRemoteProjectDescriptors().stream()
                    .filter(projectDescriptor -> projectDescriptor.getURI().equals(projectURI))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            throw new OMFException("[TWC Accessor]- Cannot access project URI: " + projectURI, GenericException.ECriticality.CRITICAL);
        }
    }


    /**
     * Find project descriptor using project name
     * @param projectName project name
     * @return project descriptor or null
     */
    @CheckForNull
    public ProjectDescriptor getExistingProjectDescriptor(String projectName) throws OMFException {
        try {
            return iTeamworkService.getProjectDescriptorByQualifiedName(projectName);
        } catch (Exception e) {
            throw new OMFException("[TWC Accessor]- Cannot access projectDescriptor : " + projectName, GenericException.ECriticality.CRITICAL);
        }
    }

}
