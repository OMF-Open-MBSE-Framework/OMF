package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project;

public interface TriggerOnProjectHook extends TriggerOnProjectOpenHook,
        TriggerOnProjectClosedHook,
        TriggerOnProjectCreatedHook,
        TriggerOnProjectDeletedHook,
        TriggerOnProjectSavedHook {
}
