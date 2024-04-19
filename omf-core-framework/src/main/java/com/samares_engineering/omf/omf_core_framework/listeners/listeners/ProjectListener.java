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
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.OMFCoreException;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.general.GenericException;
import com.samares_engineering.omf.omf_core_framework.factory.FactoryManager;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.ProjectHookExecutor;
import com.samares_engineering.omf.omf_core_framework.listeners.ListenerManager;
import com.samares_engineering.omf.omf_core_framework.plugin.APlugin;
import com.samares_engineering.omf.omf_core_framework.utils.OMFUtils;
import com.samares_engineering.omf.omf_core_framework.utils.profile.Profile;
import com.samares_engineering.omf.omf_core_framework.utils.utils.VersionUtils;

import javax.swing.*;
import java.util.List;

public class ProjectListener implements ProjectPartLoadedListener {
    public static final String PROFILE_NAME = "";
    private final APlugin plugin;
    private final ProjectHookExecutor projectHookExecutor;

    public ProjectListener(APlugin plugin){
        this.plugin = plugin;
        this.projectHookExecutor = new ProjectHookExecutor();
    }

    @Override
    public void projectOpened(Project project) {
        openProject(project);
    }

    @Override
    public void projectClosed(Project project) {
        closeProject();
    }

    @Override
    public void projectSaved(Project project, boolean b) {
        projectHookExecutor.triggerOnProjectSavedHook();
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
        projectHookExecutor.triggerOnProjectCreatedHook(); //Maybe too late, as openProject will trigger openHook, but Core is not yet initialized
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
        openProject(project);
    }

    @Override
    public void projectPreOpenedFromGUI(Project project) {
        ProjectPartLoadedListener.super.projectPreOpenedFromGUI(project);
        openProject(project);
    }

    @Override
    public void projectActivatedFromGUI(Project project) {
        openProject(project);
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
//        for (IAttachedProject iAttachedProject : OMFUtils.getProject().getPrimaryProject().getProjects()) {
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
        return doesProjectUseProfile(OMFUtils.getProject(), profileName);
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
        coreInitialisation(project);
        listenerInitialisation();
//        featureOpenProjectHandling();//TODO: Delete this line
        projectHookExecutor.triggerOnProjectOpenHook();
    }

    protected void closeProject() {
        coreClosingReInitialisation();
//        featureCloseProjectHandling(); //TODO: Delete this line
        projectHookExecutor.triggerOnProjectClosedHook();
    }


//    private void featureOpenProjectHandling() {
//        FeatureRegisterer featureRegisterer = plugin.getFeatureRegisterer();
//        if(featureRegisterer == null) return;
//        List<MDFeature> registeredFeatures = new ArrayList<>(featureRegisterer.getRegisteredFeatures());
//
//        projectOnlyFeatureRegistering(registeredFeatures);
//        openProjectFeatureTrigger(registeredFeatures);
//    }

//    private static void openProjectFeatureTrigger(List<MDFeature> registeredFeatures) {
//        for (MDFeature registeredFeature : registeredFeatures) {
//            try {
//                registeredFeature.triggerOnProjectOpenHook();
//            }catch (Exception exception) {
//                OMFErrorHandler.handleException(
//                        new OMFFeatureException("Error occurred during Feature ProjectOpen trigger",
//                                registeredFeature,
//                                exception, GenericException.ECriticality.CRITICAL), false);
//            }
//        }
//    }

    private void projectOnlyFeatureRegistering(List<MDFeature> registeredFeatures) {
        try {
            plugin.getFeatureRegister().registerProjectOnlyItemsOfFeatures(registeredFeatures);
        }catch (Exception exception) {
            OMFErrorHandler.handleException(
                    new OMFCoreException("Error occurred during Project opening Feature initialisation",
                            exception, GenericException.ECriticality.CRITICAL), false);
        }
    }

    private static void listenerInitialisation() {
        try {
            ListenerManager.getInstance().registerAllListeners();
            ListenerManager.getInstance().activateAllListeners();
        }catch (Exception exception) {
            OMFErrorHandler.handleException(
                    new OMFCoreException("Error occurred during Project opening Listener initialisation",
                            exception, GenericException.ECriticality.CRITICAL), false);
        }
    }

    private static void coreInitialisation(Project project) {
        try {
            FactoryManager.initAllFactories(project);
            Profile.getInstance();
        }catch (Exception exception){
            OMFErrorHandler.handleException(
                    new OMFCoreException("Error occurred during Project opening initialisation",
                            exception, GenericException.ECriticality.CRITICAL), false);
        }
    }

//    private void featureCloseProjectHandling() {
//        FeatureRegisterer featureRegisterer = plugin.getFeatureRegisterer();
//        if (featureRegisterer == null) return;
//        List<MDFeature> registeredFeatures = featureRegisterer.getRegisteredFeatures();
//        projectOnlyFeatureUnRegistering(registeredFeatures);
//
//        onProjectCloseFeatureTrigger(registeredFeatures);
//    }
//
//    private static void onProjectCloseFeatureTrigger(List<MDFeature> registeredFeatures) {
//        for (MDFeature registeredFeature : registeredFeatures) {
//            try {
//                registeredFeature.triggerOnProjectCloseHook();
//            }catch (Exception exception) {
//                OMFErrorHandler.handleException(
//                        new OMFFeatureException("Error occurred during Feature ProjectClose trigger",
//                                registeredFeature,
//                                exception, GenericException.ECriticality.CRITICAL), false);
//            }
//        }
//    }

    private void projectOnlyFeatureUnRegistering(List<MDFeature> registeredFeatures) {
        try {
            plugin.getFeatureRegister().unregisterProjectOnlyItemsOfFeatures(registeredFeatures);
        }catch (Exception exception) {
            OMFErrorHandler.handleException(
                    new OMFCoreException("Error occurred during Project closing Feature removal",
                            exception, GenericException.ECriticality.CRITICAL), false);
        }
    }

    private static void coreClosingReInitialisation() {
        try {
            ListenerManager.getInstance().removeAllListeners();
        }catch (Exception exception) {
            OMFErrorHandler.handleException(
                    new OMFCoreException("Error occurred during Project closing Listener removal",
                            exception, GenericException.ECriticality.CRITICAL), false);
        }
    }

    public ProjectHookExecutor getProjectHookExecutor() {
        return projectHookExecutor;
    }

}
