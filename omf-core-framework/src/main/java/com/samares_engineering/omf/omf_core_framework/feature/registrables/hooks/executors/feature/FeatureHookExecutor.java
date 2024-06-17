package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.feature;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.MDFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.HookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.exceptions.HooksExecutionException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature.IFeatureLifeCycleHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature.IOnFeatureUnregisteringHook;

public class FeatureHookExecutor extends HookExecutor<IFeatureLifeCycleHook> {

    public FeatureHookExecutor(FeatureRegisterer featureRegisterer) {
        super();
        init(featureRegisterer.getPlugin());
    }

    /**
    * Trigger all onFeatureRegistering hooks, which will be executed by the HookExecutor.
    * @see HookExecutor
    * @param feature the feature that is being registered
    */
   public void triggerOnFeatureRegisteringHooks(MDFeature feature) {
       try {
           getHooksHolders().stream()
                   .filter(IOnFeatureUnregisteringHook.class::isInstance)
                   .map(IOnFeatureUnregisteringHook.class::cast)
                   .forEach(hook -> hook.triggerOnFeatureUnregisteringHook(feature));
       } catch (Exception e) {
           ErrorHandler.getInstance().handleException(new HooksExecutionException("Error while triggering onFeatureRegistering hooks", e));
       }

   }

    /**
     * Trigger all onFeatureUnregistering hooks, which will be executed by the HookExecutor.
     * @see HookExecutor
     * @param feature the feature that is being unregistered
     */
    public void triggerOnFeatureUnregisteringHooks(MDFeature feature) {
        try {
            getHooksHolders().stream()
                    .filter(IOnFeatureUnregisteringHook.class::isInstance)
                    .map(IOnFeatureUnregisteringHook.class::cast)
                    .forEach(hook -> hook.triggerOnFeatureUnregisteringHook(feature));
        } catch (Exception e) {
            ErrorHandler.getInstance().handleException(new HooksExecutionException("Error while triggering onFeatureUnregistering hooks", e));
        }
    }

}
