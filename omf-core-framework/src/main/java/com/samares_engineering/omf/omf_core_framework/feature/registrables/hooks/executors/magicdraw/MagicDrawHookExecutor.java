package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.magicdraw;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.HookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.exceptions.HooksExecutionException;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.MagicdrawLifeCycleHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.OnMagicDrawStartHook;

public class MagicDrawHookExecutor extends HookExecutor<MagicdrawLifeCycleHook> {

   public void triggerOnMagicDrawStartHooks() {
       try {
           getHooksHolders().stream()
                   .filter(OnMagicDrawStartHook.class::isInstance)
                   .map(OnMagicDrawStartHook.class::cast)
                   .forEach(OnMagicDrawStartHook::triggerOnMagicDrawStartHook);
       } catch (Exception e) {
           OMFErrorHandler.getInstance().handleException(new HooksExecutionException("Error while triggering onMagicDrawStart hooks", e));
       }

   }


}
