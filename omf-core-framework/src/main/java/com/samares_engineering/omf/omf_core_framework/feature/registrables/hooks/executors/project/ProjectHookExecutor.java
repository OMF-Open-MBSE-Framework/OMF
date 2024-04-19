package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.project;

import com.nomagic.magicdraw.core.Project;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.HookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.*;

public class ProjectHookExecutor extends HookExecutor<ProjectLifeCycleHook> {

   public void triggerOnProjectOpenedHooks(Project project) {
       try {
           getHooksHolders().stream()
                   .filter(IOnProjectOpenedHook.class::isInstance)
                   .forEach(hook -> ((IOnProjectOpenedHook) hook).triggerOnProjectOpenHook(project));
       }catch (Exception e){
           ErrorHandler2.getInstance().handleException(new CoreException2("Error while triggering onProjectOpened hooks", e));
       }
   }

    public void triggerOnProjectClosedHooks(Project project) {
       try {
           getHooksHolders().stream()
                   .filter(IOnProjectClosedHook.class::isInstance)
                   .forEach(hook -> ((IOnProjectClosedHook) hook).triggerOnProjectClosedHook(project));
       }catch (Exception e) {
           ErrorHandler2.getInstance().handleException(new CoreException2("Error while triggering onProjectClosed hooks", e));
       }
    }

    public void triggerOnProjectSavedHooks(Project project) {
        try {
            getHooksHolders().stream()
                    .filter(IOnProjectSavedHook.class::isInstance)
                    .forEach(hook -> ((IOnProjectSavedHook) hook).triggerOnProjectSavedHook(project));
        } catch (Exception e) {
            ErrorHandler2.getInstance().handleException(new CoreException2("Error while triggering onProjectSaved hooks", e));
        }
    }

    public void triggerOnProjectCreatedHooks(Project project) {
        try {
            getHooksHolders().stream()
                    .filter(IOnProjectCreatedHook.class::isInstance)
                    .forEach(hook -> ((IOnProjectCreatedHook) hook).triggerOnProjectCreatedHook(project));
        } catch (Exception e) {
            ErrorHandler2.getInstance().handleException(new CoreException2("Error while triggering onProjectCreated hooks", e));
        }
    }




}
