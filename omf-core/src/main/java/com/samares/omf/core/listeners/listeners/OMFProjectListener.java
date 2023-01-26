/*******************************************************************************
 * @copyright Copyright (c) 2020-2021 Samares-Engineering
 * @Licence: EPL 2.0
 * @Author:   Quentin Cespédès, Clément Mezerette, Hugo Stinson
 * @since     0.0.0
 ******************************************************************************/
package com.samares.omf.core.listeners.listeners;

import com.nomagic.ci.persistence.IAttachedProject;
import com.nomagic.ci.persistence.IProject;
import com.nomagic.magicdraw.core.Project;
import com.nomagic.magicdraw.core.project.ProjectPartLoadedListener;
import com.samares.omf.core.feature.FeatureRegister;
import com.samares.omf.core.feature.IFeatureRegisterer;
import com.samares.omf.core.feature.MDFeature;
import com.samares.omf.core.listeners.OMFListenerManager;
import com.samares.omf.core.utils.OMFUtils;
import com.samares.omf.core.utils.factory.FactoryManager;
import com.samares.omf.core.utils.profile.Profile;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class OMFProjectListener implements ProjectPartLoadedListener, IFeatureRegisterer {
    public static final String PROFILE_NAME = "";
    private FeatureRegister featureRegisterer;
    private List<MDFeature> l_delayedFeature;
    private List<MDFeature> l_optionProjectFeature;

    public OMFProjectListener(){
        l_delayedFeature = new ArrayList<>();
        l_optionProjectFeature = new ArrayList<>();
    }
    public OMFProjectListener(FeatureRegister featureRegister){
        this();
        this.featureRegisterer = featureRegister;
    }


    public void addFeatureToRegisterAtProjectOpening(List<MDFeature> l_featureToRegister){
        l_delayedFeature.addAll(l_featureToRegister);
    }

    public void addProjectOptionToRegister(List<MDFeature> allProjectOptionsFeatures) {
        l_optionProjectFeature.addAll(allProjectOptionsFeatures);
    }

    public void removeFeatureFromRegisteringList(MDFeature feature){
        l_delayedFeature.remove(feature);
    }
    public void removeAllFeatureFromRegisteringList(List<MDFeature> l_featureToRemove){
        l_delayedFeature.removeAll(l_featureToRemove);
    }

    @Override
    public void registerFeature(MDFeature mdFeature) {
        featureRegisterer.registerFeature(mdFeature);
    }

    @Override
    public void unregisterFeature(MDFeature mdFeature) {
        featureRegisterer.unregisterFeature(mdFeature);
    }

    @Override
    public void registerAllFeatures(List<MDFeature> l_feature) {
        IFeatureRegisterer.super.registerAllFeatures(l_feature);
    }

    @Override
    public void unregisterAllFeatures(List<MDFeature> l_feature) {
        IFeatureRegisterer.super.unregisterAllFeatures(l_feature);
    }


    private void registerAllProjectOptionFeatures(List<MDFeature> l_optionProjectFeature) {
        l_optionProjectFeature.forEach(feature -> featureRegisterer.getOptionRegisterer().registerProjectOptions(feature));
    }
    private void unRegisterAllProjectOptionFeatures(List<MDFeature> l_optionProjectFeature) {
        l_optionProjectFeature.forEach(feature -> featureRegisterer.getOptionRegisterer().unregisterProjectOptions(feature));
    }

    @Override
    public void projectOpened(Project project) {
//        openProject(project);
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
            String minCoreVersion = OMFUtils.versionCsvReader();
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
//        checkProfileVersion();
        OMFListenerManager.getInstance().registerAllListeners();
        OMFListenerManager.getInstance().activateAllListeners();
        registerAllFeatures(l_delayedFeature);
        registerAllProjectOptionFeatures(l_optionProjectFeature);
//        ProjectOptions.addConfigurator(OMFProjectOptionsConfigurator.getInstance());
    }


    protected void closeProject() {
        if(OMFUtils.currentProject == null)
            return;
        OMFListenerManager.getInstance().removeAllListeners();
        OMFUtils.currentProject = null;
        unregisterAllFeatures(l_delayedFeature);
        unRegisterAllProjectOptionFeatures(l_optionProjectFeature);
    }


}
