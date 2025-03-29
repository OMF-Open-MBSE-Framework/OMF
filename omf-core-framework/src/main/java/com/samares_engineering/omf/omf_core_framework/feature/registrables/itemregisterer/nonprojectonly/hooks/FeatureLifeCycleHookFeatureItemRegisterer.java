package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks;

import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature.FeatureLifeCycleHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Registerer for Feature Hooks. <br>
 * It registers the all hooks stored in a HookExecutor to the FeatureRegisterer,
 * which will trigger them when the Feature lifecycle events are triggered.
 * @see FeatureLifeCycleHook
 */
public class FeatureLifeCycleHookFeatureItemRegisterer implements FeatureItemRegisterer<FeatureLifeCycleHook> {
    private FeatureRegisterer featureRegisterer;
    List<FeatureLifeCycleHook> registeredFeatureItems = new ArrayList<>();

    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        setFeatureRegisterer(featureRegisterer);
        if(this.featureRegisterer == null) {
            throw new FeatureRegisteringException("FeatureRegisterer is null, cannot register Feature lifecycle hooks." +
                    "Please register the featureRegister in the main plugin class.");
        }
    }

    /**
     * Register all Feature Hooks from its holder in the FeatureRegisterer.
     * @param hooks list of hooks
     */
    @Override
    public void registerFeatureItems(List<FeatureLifeCycleHook> hooks) {
        hooks.forEach(this::registerFeatureItem);
    }

    /**
     *  Register all Feature Hooks from its holder in the FeatureRegisterer.
     * @param hook HookExecutor to register
     */
    @Override
    public void registerFeatureItem(FeatureLifeCycleHook hook) {
        try {
            if(hook == null || !hook.isActivated()) return;
            featureRegisterer.getFeatureHookExecutor().addHook(hook);
            registeredFeatureItems.add(hook);

        }catch (Exception e) {
            throw new FeatureRegisteringException(
                    "[Feature] Could not register HookExecutor: " + hook.getClass().getSimpleName()
                            + " for mdFeature: " + hook.getFeature().getName());
        }
    }

    /**
     * Unregister all Feature Hooks from its holder in the FeatureRegisterer.
     * @param mdFeature list of hooks
     */
    @Override
    public void unregisterFeatureItems(List<FeatureLifeCycleHook> mdFeature) {
        mdFeature.forEach(this::unregisterFeatureItem);
    }

    /**
     * Unregister all Feature Hooks from its holder in the FeatureRegisterer.
     * @param hook hook to unregister
     */
    @Override
    public void unregisterFeatureItem(FeatureLifeCycleHook hook) {
        try {
            if(hook == null) return;
            featureRegisterer.getFeatureHookExecutor().removeHook(hook);
            registeredFeatureItems.remove(hook);
        }catch (Exception e) {
            throw new FeatureRegisteringException(
                    "[Feature] Could not unregister hook: " + hook.getClass().getSimpleName()
                            + " for mdFeature: " + hook.getFeature().getName());
        }
    }

    /**
     * Register all the Feature Hooks from its Holder in the FeatureRegisterer.
     * @param feature feature
     */
    @Override
    public void registerFeatureItems(OMFFeature feature) {
        registerFeatureItems(feature.getLifeCycleHooks().stream()
                .filter(FeatureLifeCycleHook.class::isInstance)
                .map(FeatureLifeCycleHook.class::cast)
                .collect(Collectors.toList()));
    }

    /**
     * Unregister all the Feature Hooks from its Holder in the FeatureRegisterer.
     * @param feature feature
     */
    @Override
    public void unregisterFeatureItems(OMFFeature feature) {
        unregisterFeatureItems(feature.getLifeCycleHooks().stream()
                .filter(FeatureLifeCycleHook.class::isInstance)
                .map(FeatureLifeCycleHook.class::cast)
                .collect(Collectors.toList()));
    }

    @Override
    public FeatureRegisterer getFeatureRegisterer() {
        return featureRegisterer;
    }

    @Override
    public void setFeatureRegisterer(FeatureRegisterer featureRegisterer) {
        this.featureRegisterer = featureRegisterer;
    }
    @Override
    public List<FeatureLifeCycleHook> getRegisteredFeatureItems() {
        return registeredFeatureItems;
    }
}
