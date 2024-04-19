package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.feature.OMFFeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.listeners.listeners.ProjectListener;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Registerer for Project LifeCycle Hooks. </BR>
 * It registers the hooks in the ProjectListener, which will trigger them when the project lifecycle events are triggered.
 * @see OnProjectHook
 */
public class ProjectLifeCycleHookFeatureItemRegisterer implements FeatureItemRegisterer<OnProjectHook> {
    private FeatureRegisterer featureRegister;
    private ProjectListener projectListener;

    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        setFeatureRegisterer(featureRegisterer);
        projectListener = featureRegisterer.getPlugin().getProjectListener();
        if(projectListener == null) {
            throw new OMFFeatureRegisteringException("ProjectListener is null, cannot register hooks");
        }
    }

    /**
     * Register all the Project LifeCycle Hooks in the ProjectListener.
     * @param hooks list of hooks
     */
    @Override
    public void registerFeatureItems(List<OnProjectHook> hooks) {
        hooks.forEach(this::registerFeatureItem);
    }

    /**
     *  Register a Project hook LifeCycle in the ProjectListener.
     * @param hook hook to register
     */
    @Override
    public void registerFeatureItem(OnProjectHook hook) {
        try {
            if(hook == null || !hook.isActivated()) return;
            projectListener.getProjectHookExecutor().addHook(hook);
        }catch (Exception e) {
            throw new OMFFeatureRegisteringException(
                    "[Feature] Could not register hook: " + hook.getClass().getSimpleName()
                            + " for mdFeature: " + hook.getFeature().getName());
        }
    }

    /**
     * Unregister all the Project LifeCycle Hooks in the ProjectListener.
     * @param mdFeature list of hooks
     */
    @Override
    public void unregisterFeatureItems(List<OnProjectHook> mdFeature) {
        mdFeature.forEach(this::unregisterFeatureItem);
    }

    /**
     * Unregister a Project hook LifeCycle in the ProjectListener.
     * @param hook hook to unregister
     */
    @Override
    public void unregisterFeatureItem(OnProjectHook hook) {
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
     * Register all the Project LifeCycle Hooks of the Feature in the ProjectListener.
     * @param feature feature
     */
    @Override
    public void registerFeatureItems(MDFeature feature) {
        registerFeatureItems(feature.getLifeCycleHooks().stream()
                .filter(OnProjectHook.class::isInstance)
                .map(OnProjectHook.class::cast)
                .collect(Collectors.toList()));
    }

    /**
     * Unregister all the Project LifeCycle Hooks of the Feature in the ProjectListener.
     * @param feature feature
     */
    @Override
    public void unregisterFeatureItems(MDFeature feature) {
        unregisterFeatureItems(feature.getLifeCycleHooks().stream()
                .filter(OnProjectHook.class::isInstance)
                .map(OnProjectHook.class::cast)
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
