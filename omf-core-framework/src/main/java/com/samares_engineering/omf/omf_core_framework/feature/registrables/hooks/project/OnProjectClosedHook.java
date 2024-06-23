package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project;

import com.nomagic.magicdraw.core.Project;

public interface OnProjectClosedHook extends ProjectLifeCycleHook {
    /**
     * NOTE: This method is called by the framework, do not call it directly or override it.
     * Trigger the hook when the project is closed.
     * @param project the project that is closed
     */
    default  void triggerOnProjectClosedHook(Project project) {
        executeHook(() -> onProjectClosed(project), "onProjectClosed");
    }

    /**
     * This method is called when a project is closed. <br>
     * Developers should implement this method to add custom logic.
     * @param project the project that was closed
     */
    void onProjectClosed(Project project);
}
