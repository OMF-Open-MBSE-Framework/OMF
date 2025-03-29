package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project;

import com.nomagic.magicdraw.core.Project;

/**
 * This interface is used to define a hook that is triggered when a project is saved.
 * Developers should implement this interface to add custom logic that should be executed when a project is saved.
 */
public interface OnProjectSavedHook extends ProjectLifeCycleHook {

    /**
     * NOTE: This method is called by the framework, do not call it directly or override it.
     * Trigger the hook when a project is saved.
     * @param project the project that was saved
     */
    default void triggerOnProjectSavedHook(Project project) {
        executeHook(() -> onProjectSaved(project), "onProjectSaved");
    }

    /**
     * This method is called when a project is saved. <br>
     * Developers should implement this method to add custom logic.
     * @param project the project that was saved
     */
    void onProjectSaved(Project project);
}
