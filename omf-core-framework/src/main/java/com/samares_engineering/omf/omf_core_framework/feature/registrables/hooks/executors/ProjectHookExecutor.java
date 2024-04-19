package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.TriggerOnProjectHook;

public class ProjectHookExecutor extends HookExecutor<OnProjectHook> implements TriggerOnProjectHook {

    @Override
    public void triggerOnProjectOpenHook() {
        hooks.forEach(OnProjectHook::triggerOnProjectOpenHook);
    }

    @Override
    public void triggerOnProjectClosedHook() {
        hooks.forEach(OnProjectHook::triggerOnProjectClosedHook);
    }

    @Override
    public void triggerOnProjectCreatedHook() {
        hooks.forEach(OnProjectHook::triggerOnProjectCreatedHook);
    }

    @Override
    public void triggerOnProjectDeletedHook() {
        hooks.forEach(OnProjectHook::triggerOnProjectDeletedHook);
    }

    @Override
    public void triggerOnProjectSavedHook() {
        hooks.forEach(OnProjectHook::triggerOnProjectSavedHook);
    }
}
