package com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.nonprojectonly.hooks;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.errors.exceptions.core.FeatureRegisteringException;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.MagicdrawLifeCycleHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.itemregisterer.FeatureItemRegisterer;
import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Registerer for MagicDraw Hooks. <br>
 * It registers the all hooks stored in the plugin HookExecutor.
 * which will trigger them when the MagicDraw lifecycle events are triggered.
 * @see MagicdrawLifeCycleHook
 */
public class MagicDrawLifeCycleHookFeatureItemRegisterer implements FeatureItemRegisterer<MagicdrawLifeCycleHook> {
    private FeatureRegisterer featureRegister;
    private OMFPlugin plugin;
    final List<MagicdrawLifeCycleHook> registeredFeatureItems = new ArrayList<>();

    @Override
    public void init(FeatureRegisterer featureRegisterer) {
        setFeatureRegisterer(featureRegisterer);
        plugin = featureRegisterer.getPlugin();
        if(plugin == null) {
            throw new CoreException2("¨Plugin is null, cannot register MagicDraw lifecycle hooks." +
                    "The plugin shall never be null, and always be accessible for OMF Framework to work properly.");
        }
    }

    /**
     * Register all MagicDraw hooks in the Plugin HookExecutor.
     * @param hooks list of hooks
     */
    @Override
    public void registerFeatureItems(List<MagicdrawLifeCycleHook> hooks) {
        hooks.forEach(this::registerFeatureItem);
    }

    /**
     *  Register all MagicDraw hooks in the Plugin HookExecutor.
     * @param MagicdrawLifeCycleHook HookExecutor where the hooks are stored
     */
    @Override
    public void registerFeatureItem(MagicdrawLifeCycleHook MagicdrawLifeCycleHook) {
        try {
            if(MagicdrawLifeCycleHook == null || !MagicdrawLifeCycleHook.isActivated()) return;
            plugin.getMagicDrawHookExecutor().addHook(MagicdrawLifeCycleHook);
            registeredFeatureItems.add(MagicdrawLifeCycleHook);
        }catch (Exception e) {
            throw new FeatureRegisteringException(
                    "[Feature] Could not register hook: " + MagicdrawLifeCycleHook.getClass().getSimpleName()
                            + " for mdFeature: " + MagicdrawLifeCycleHook.getFeature().getName());
        }
    }

    /**
     * Unregister all MagicDraw hooks in the Plugin HookExecutor.
     * @param mdFeature list of hooks
     */
    @Override
    public void unregisterFeatureItems(List<MagicdrawLifeCycleHook> mdFeature) {
        mdFeature.forEach(this::unregisterFeatureItem);
    }

    /**
     * Unregister all MagicDraw hooks in the Plugin HookExecutor.
     * @param hook hook to unregister
     */
    @Override
    public void unregisterFeatureItem(MagicdrawLifeCycleHook hook) {
        try {
            if(hook == null) return;
            plugin.getMagicDrawHookExecutor().removeHook(hook);
            registeredFeatureItems.remove(hook);
        }catch (Exception e) {
            throw new FeatureRegisteringException(
                    "[Feature] Could not unregister hook: " + hook.getClass().getSimpleName()
                            + " for mdFeature: " + hook.getFeature().getName());
        }
    }

    /**
     * Register all the MagicDraw hooks in the Plugin HookExecutor.
     * @param feature feature
     */
    @Override
    public void registerFeatureItems(OMFFeature feature) {
        registerFeatureItems(feature.getLifeCycleHooks().stream()
                .filter(MagicdrawLifeCycleHook.class::isInstance)
                .map(MagicdrawLifeCycleHook.class::cast)
                .collect(Collectors.toList()));
    }

    /**
     * Unregister all the MagicDraw hooks in the Plugin HookExecutor.
     * @param feature feature
     */
    @Override
    public void unregisterFeatureItems(OMFFeature feature) {
        unregisterFeatureItems(feature.getLifeCycleHooks().stream()
                .filter(MagicdrawLifeCycleHook.class::isInstance)
                .map(MagicdrawLifeCycleHook.class::cast)
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

    @Override
    public List<MagicdrawLifeCycleHook> getRegisteredFeatureItems() {
        return registeredFeatureItems;
    }
}
