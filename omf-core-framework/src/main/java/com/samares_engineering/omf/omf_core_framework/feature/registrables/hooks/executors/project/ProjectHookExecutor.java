package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.project;

import com.nomagic.magicdraw.core.Project;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.HookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.*;

public class ProjectHookExecutor extends HookExecutor<ProjectLifeCycleHook> {

   public void triggerOnProjectOpenedHooks(Project project) {
       getHooksHolders().stream()
               .filter(IOnProjectOpenedHook.class::isInstance)
               .forEach(hook -> ((IOnProjectOpenedHook) hook).triggerOnProjectOpenHook(project));
   }

    public void triggerOnProjectClosedHooks(Project project) {
         getHooksHolders().stream()
                .filter(IOnProjectClosedHook.class::isInstance)
                .forEach(hook -> ((IOnProjectClosedHook) hook).triggerOnProjectClosedHook(project));
    }

    public void triggerOnProjectSavedHooks(Project project) {
        getHooksHolders().stream()
                .filter(IOnProjectSavedHook.class::isInstance)
                .forEach(hook -> ((IOnProjectSavedHook) hook).triggerOnProjectSavedHook(project));
    }

    public void triggerOnProjectCreatedHooks(Project project) {
        getHooksHolders().stream()
                .filter(IOnProjectCreatedHook.class::isInstance)
                .forEach(hook -> ((IOnProjectCreatedHook) hook).triggerOnProjectCreatedHook(project));
    }




}
