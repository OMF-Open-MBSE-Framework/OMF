package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.magicdraw;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.HookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.exceptions.HooksExecutionException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.IMagicdrawLifeCycleHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.IOnMagicDrawStartHook;

public class MagicDrawHookExecutor extends HookExecutor<IMagicdrawLifeCycleHook> {

   public void triggerOnMagicDrawStartHooks() {
       try {
           getHooksHolders().stream()
                   .filter(IOnMagicDrawStartHook.class::isInstance)
                   .map(IOnMagicDrawStartHook.class::cast)
                   .forEach(IOnMagicDrawStartHook::triggerOnMagicDrawStartHook);
       } catch (Exception e) {
           OMFErrorHandler.getInstance().handleException(new HooksExecutionException("Error while triggering onMagicDrawStart hooks", e));
       }

   }


}
