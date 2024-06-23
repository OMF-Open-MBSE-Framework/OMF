package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project;

import com.nomagic.magicdraw.core.Project;

public interface OnProjectOpenedHook extends ProjectLifeCycleHook {

    /**
     * NOTE: This method is called by the framework, do not call it directly or override it.
     * This method is used to trigger the hook when a project is opened.
     * @param project the project that was opened
     */
    default void triggerOnProjectOpenedHook(Project project) {
        executeHook(() -> onProjectOpened(project), "onProjectOpened");
    }

    /**
     * This method is called when a project is opened. <br>
     * Developers should implement this method to add custom logic.
     * @param project the project that was opened
     */
     void onProjectOpened(Project project);
}
