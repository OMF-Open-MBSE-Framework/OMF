package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.feature;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.FeatureRegisterer;
import com.samares_engineering.omf.omf_core_framework.feature.OMFFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.HookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.exceptions.HooksExecutionException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature.FeatureLifeCycleHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.feature.OnFeatureUnregisteringHook;

public class FeatureHookExecutor extends HookExecutor<FeatureLifeCycleHook> {

    public FeatureHookExecutor(FeatureRegisterer featureRegisterer) {
        super();
        init(featureRegisterer.getPlugin());
    }

    /**
    * Trigger all onFeatureRegistering hooks, which will be executed by the HookExecutor.
    * @see HookExecutor
    * @param feature the feature that is being registered
    */
   public void triggerOnFeatureRegisteringHooks(OMFFeature feature) {
       try {
           getHooksHolders().stream()
                   .filter(OnFeatureUnregisteringHook.class::isInstance)
                   .map(OnFeatureUnregisteringHook.class::cast)
                   .forEach(hook -> hook.triggerOnFeatureUnregisteringHook(feature));
       } catch (Exception e) {
           OMFErrorHandler.getInstance().handleException(new HooksExecutionException("Error while triggering onFeatureRegistering hooks", e));
       }

   }

    /**
     * Trigger all onFeatureUnregistering hooks, which will be executed by the HookExecutor.
     * @see HookExecutor
     * @param feature the feature that is being unregistered
     */
    public void triggerOnFeatureUnregisteringHooks(OMFFeature feature) {
        try {
            getHooksHolders().stream()
                    .filter(OnFeatureUnregisteringHook.class::isInstance)
                    .map(OnFeatureUnregisteringHook.class::cast)
                    .forEach(hook -> hook.triggerOnFeatureUnregisteringHook(feature));
        } catch (Exception e) {
            OMFErrorHandler.getInstance().handleException(new HooksExecutionException("Error while triggering onFeatureUnregistering hooks", e));
        }
    }

}
