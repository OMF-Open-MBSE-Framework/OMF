package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project;

import com.nomagic.magicdraw.core.Project;

public interface OnProjectCreatedHook extends ProjectLifeCycleHook {

    /**
     * NOTE: This method is called by the framework, do not call it directly or override it.
     * This method triggers the onProjectCreated hook.
     * @param project the project that was created
     */
    default void triggerOnProjectCreatedHook(Project project){
        executeHook(() -> onProjectCreated(project), "onProjectCreated");
    }

    /**
     * This method is called when a project is created. <br>
     * Developers should implement this method to add custom logic.
     * @param project the project that was created
     */
    void onProjectCreated(Project project);
}
