package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.project;

import com.nomagic.magicdraw.core.Project;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.HookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.exceptions.HooksExecutionException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.*;

public class ProjectHookExecutor extends HookExecutor<ProjectLifeCycleHook> {

   public void triggerOnProjectOpenedHooks(Project project) {
       try {
           getHooksHolders().stream()
                   .filter(OnProjectOpenedHook.class::isInstance)
                   .map(OnProjectOpenedHook.class::cast)
                   .forEach(hook -> hook.triggerOnProjectOpenedHook(project));
       }catch (Exception e){
           OMFErrorHandler.getInstance().handleException(new HooksExecutionException("Error while triggering onProjectOpened hooks", e));
       }
   }

    public void triggerOnProjectClosedHooks(Project project) {
       try {
           getHooksHolders().stream()
                   .filter(OnProjectClosedHook.class::isInstance)
                   .map(OnProjectClosedHook.class::cast)
                     .forEach(hook -> hook.triggerOnProjectClosedHook(project));
       }catch (Exception e) {
           OMFErrorHandler.getInstance().handleException(new HooksExecutionException("Error while triggering onProjectClosed hooks", e));
       }
    }

    public void triggerOnProjectSavedHooks(Project project) {
        try {
            getHooksHolders().stream()
                    .filter(OnProjectSavedHook.class::isInstance)
                    .map(OnProjectSavedHook.class::cast)
                    .forEach(hook -> hook.triggerOnProjectSavedHook(project));
        } catch (Exception e) {
            OMFErrorHandler.getInstance().handleException(new HooksExecutionException("Error while triggering onProjectSaved hooks", e));
        }
    }

    public void triggerOnProjectCreatedHooks(Project project) {
        try {
            getHooksHolders().stream()
                    .filter(OnProjectCreatedHook.class::isInstance)
                    .map(OnProjectCreatedHook.class::cast)
                    .forEach(hook -> hook.triggerOnProjectCreatedHook(project));
        } catch (Exception e) {
            OMFErrorHandler.getInstance().handleException(new HooksExecutionException("Error while triggering onProjectCreated hooks", e));
        }
    }




}
