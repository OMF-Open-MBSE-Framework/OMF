/*******************************************************************************
 * @copyright Copyright (c) 2022-2023 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares_engineering.omf.omf_core_framework.listeners.listeners;

import com.nomagic.ci.persistence.IAttachedProject;
import com.nomagic.ci.persistence.IProject;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectPartLoadedListener;
import com.samares_engineering.omf.omf_core_framework.factory.FactoryManager;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.VersionUtils;

import javax.swing.*;

public class ProjectListener implements ProjectPartLoadedListener {
    public static final String PROFILE_NAME = "";
    private final APlugin plugin;

    public ProjectListener(APlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public void projectOpened(Project project) {
        if (OMFUtils.currentProject != project) {
            openProject(project);
        }
    }
    @Override
    public void projectClosed(Project project) {
        closeProject();
    }
    @Override
    public void projectSaved(Project project, boolean b) {
    }
    @Override
    public void projectActivated(Project project) {
        openProject(project);
    }
    @Override
    public void projectDeActivated(Project project) {
        closeProject();
    }

    @Override
    public void projectReplaced(Project project, Project project1) {
        closeProject();
        openProject(project);
    }

    @Override
    public void projectCreated(Project project) {
        openProject(project);
    }

    @Override
    public void projectPreClosed(Project project) {

    }

    @Override
    public void projectPreClosedFinal(Project project) {

    }

    @Override
    public void projectPreSaved(Project project, boolean b) {

    }

    @Override
    public void projectPreActivated(Project project) {

    }

    @Override
    public void projectPreDeActivated(Project project) {
        closeProject();
    }

    @Override
    public void projectOpenedFromGUI(Project project) {
        if(project != OMFUtils.currentProject ) {
            openProject(project);
        }
    }

    @Override
    public void projectPreOpenedFromGUI(Project project) {
        ProjectPartLoadedListener.super.projectPreOpenedFromGUI(project);
        if(project != OMFUtils.currentProject ) {
            openProject(project);
        }
    }

    @Override
    public void projectActivatedFromGUI(Project project) {
        if(project != OMFUtils.currentProject ){
            openProject(project);
        }
    }

    @Override
    public void projectPartLoaded(Project project, IProject iProject) {

    }

//    private boolean checkVersion() {
//        OMFEnvironmentOptionsGroup.getInstance();
//        String version = null;
//
//        // We retrieve the version of the plugin store in a csv
//        String minVersionRequired = OMFUtils.versionCsvReader();
//
//        // We iterate trough every project (profile) associate to find the correct one
//        for (IAttachedProject iAttachedProject : OMFUtils.currentProject.getPrimaryProject().getProjects()) {
//            if (iAttachedProject.getName() != null && iAttachedProject.getName().equals(PROFILE_NAME)) {
//                version = ProjectUtilities.getInternalVersion(iAttachedProject);
//            }
//        }
//        if(version == null || minVersionRequired == null){
//            OMFEnvironmentOptionsGroup.isPluginCompatible = false;
//            return false;
//        }
//        // We compare the versions numbers
//        OMFUtils.Version profileVersion    = new OMFUtils.Version(minVersionRequired);
//        OMFUtils.Version versionToCompare  = new OMFUtils.Version(version);
//        if(profileVersion.compareTo(versionToCompare) <= 0) {
//            OMFEnvironmentOptionsGroup.isPluginCompatible = true;
//            return true;
//        }
//        return false;
//    }

    /**
     * Display a warning message if profil version is not correct
     */
    protected void notifyUserAboutPluginCompatibility(boolean isValidVersion){
        if(!isValidVersion) {
            String minCoreVersion = VersionUtils.versionCsvReader();
            String warningMessage = "Profile version is outdated and not compatible with this Plugin version";
            warningMessage += (minCoreVersion != null && !minCoreVersion.equals("")) ?
                    "\nPlease use a profile with the minimum version for the plugin compatibility : " + minCoreVersion
                    : "\nPlease see the User Guide to check the version to use";

            JOptionPane.showMessageDialog(null, warningMessage, "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Check if project using a defined profile (e.g. SysML project). Enable/disable automations accordingly
     * @return boolean isCurrentProjectIsUsingProfile
     */
    protected boolean doesProjectUseProfile(String profileName){
        return doesProjectUseProfile(OMFUtils.currentProject, profileName);
    }
    protected boolean doesProjectUseProfile(Project project, String profileName){
        boolean isCurrentProjectIsUsingProfile = true;

        // We iterate trough every project (profile)
        for (IAttachedProject iAttachedProject : project.getPrimaryProject().getProjects()) {
            if (iAttachedProject.getName() != null && iAttachedProject.getName().equals(profileName)) {
                isCurrentProjectIsUsingProfile = false;
            }
        }
        //TODO REMOVE THIS FROM FUNCTION, MODIFICATION /IMPACT SHALL BE DONE OUTSIDE
//        if(!isCurrentProjectIsUsingProfile){
//            OMFEnvironmentOptionsGroup.getInstance();
//            OMFEnvironmentOptionsGroup.isPluginCompatible = false;
//        }
        return isCurrentProjectIsUsingProfile;
    }

    protected void openProject(Project project) {
        OMFUtils.currentProject = project;
        FactoryManager.initAllFactories(project);
        Profile.getInstance();
        ListenerManager.getInstance().registerAllListeners();
        ListenerManager.getInstance().activateAllListeners();
        plugin.getFeatureRegister().registerProjectOnlyItemsOfFeatures(plugin.getFeatures());
        plugin.getFeatures().forEach(MDFeature::onProjectOpen);
    }

    protected void closeProject() {
        if(OMFUtils.currentProject == null)
            return;
        ListenerManager.getInstance().removeAllListeners();
        OMFUtils.currentProject = null;
        plugin.getFeatureRegister().unregisterDelayedItemsOfFeatures(plugin.getFeatures());
        plugin.getFeatures().forEach(MDFeature::onProjectClose);
    }
}
