package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks;

public interface LifeCycleHook extends HookFeatureItem{
    void triggerOnRegisteringHook();

    void triggerOnUnregisteringHook();

    void triggerOnProjectOpenHook();

    void triggerOnProjectCloseHook();

    void triggerOnMagicdrawStartupHook();

    /**
     * Override this to inject code to be run on feature activation
     */
    void onRegistering();

    /**
     * Override this to inject code to be run on feature deactivation
     */
    void onUnregistering();

    /**
     * Override this to inject code to be run on project opening
     */
    void onProjectOpen();

    /**
     * Override this to inject code to be run on project closing
     */
    void onProjectClose();

    /**
     * Override this to inject code to be run on MagicDraw startup
     */
    void onMagicdrawStartup();

}
