package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.project.ProjectLifeCycleHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.ProjectListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Registerer for Project Hooks. <br>
 * It registers the all hooks stored in a HookHolder to the ProjectListener,
 * which will trigger them when the project lifecycle events are triggered.
 * @see ProjectLifeCycleHook
 */
public class ProjectLifeCycleHookFeatureItemRegisterer implements FeatureItemRegisterer<ProjectLifeCycleHook> {
    private FeatureRegisterer featureRegister;
    private ProjectListener projectListener;

    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        setFeatureRegisterer(featureRegisterer);
        projectListener = featureRegisterer.getPlugin().getProjectListener();
        if(projectListener == null) {
            throw new OMFFeatureRegisteringException("ProjectListener is null, cannot register project lifecycle hooks." +
                    "Please register the ProjectListener in the main plugin class.");
        }
    }

    /**
     * Register all Project Hooks from its holder in the ProjectListener.
     * @param hooks list of hooks
     */
    @Override
    public void registerFeatureItems(List<ProjectLifeCycleHook> hooks) {
        hooks.forEach(this::registerFeatureItem);
    }

    /**
     *  Register all Project Hooks from its holder in the ProjectListener.
     * @param ProjectLifeCycleHook hookHolder to register
     */
    @Override
    public void registerFeatureItem(ProjectLifeCycleHook ProjectLifeCycleHook) {
        try {
            if(ProjectLifeCycleHook == null || !ProjectLifeCycleHook.isActivated()) return;
            projectListener.getProjectHookExecutor().addHook(ProjectLifeCycleHook);
        }catch (Exception e) {
            throw new OMFFeatureRegisteringException(
                    "[Feature] Could not register hookHolder: " + ProjectLifeCycleHook.getClass().getSimpleName()
                            + " for mdFeature: " + ProjectLifeCycleHook.getFeature().getName());
        }
    }

    /**
     * Unregister all Project Hooks from its holder in the ProjectListener.
     * @param mdFeature list of hooks
     */
    @Override
    public void unregisterFeatureItems(List<ProjectLifeCycleHook> mdFeature) {
        mdFeature.forEach(this::unregisterFeatureItem);
    }

    /**
     * Unregister all Project Hooks from its holder in the ProjectListener.
     * @param hook hook to unregister
     */
    @Override
    public void unregisterFeatureItem(ProjectLifeCycleHook hook) {
        try {
            if(hook == null) return;
            projectListener.getProjectHookExecutor().removeHook(hook);
        }catch (Exception e) {
            throw new OMFFeatureRegisteringException(
                    "[Feature] Could not unregister hook: " + hook.getClass().getSimpleName()
                            + " for mdFeature: " + hook.getFeature().getName());
        }
    }

    /**
     * Register all the Project Hooks from its Holder in the ProjectListener.
     * @param feature feature
     */
    @Override
    public void registerFeatureItems(MDFeature feature) {
        registerFeatureItems(feature.getLifeCycleHooks().stream()
                .filter(ProjectLifeCycleHook.class::isInstance)
                .map(ProjectLifeCycleHook.class::cast)
                .collect(Collectors.toList()));
    }

    /**
     * Unregister all the Project Hooks from its Holder in the ProjectListener.
     * @param feature feature
     */
    @Override
    public void unregisterFeatureItems(MDFeature feature) {
        unregisterFeatureItems(feature.getLifeCycleHooks().stream()
                .filter(ProjectLifeCycleHook.class::isInstance)
                .map(ProjectLifeCycleHook.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegister;
    }

    @Override
    public void setFeatureRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegister = featureRegisterer;
    }
}
