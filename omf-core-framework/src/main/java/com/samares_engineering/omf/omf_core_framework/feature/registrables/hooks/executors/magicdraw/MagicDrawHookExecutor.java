package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.magicdraw;

import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler2;
import com.samares_engineering.omf.omf_core_framework.errormanagement2.exceptions.CoreException2;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.HookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.IOnMagicDrawStartHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.MagicdrawLifeCycleHook;

public class MagicDrawHookExecutor extends HookExecutor<MagicdrawLifeCycleHook> {

   public void triggerOnMagicDrawStartHooks() {
       try {
           getHooksHolders().stream()
                   .filter(IOnMagicDrawStartHook.class::isInstance)
                   .map(IOnMagicDrawStartHook.class::cast)
                   .forEach(IOnMagicDrawStartHook::onMagicDrawStart);
       } catch (Exception e) {
           ErrorHandler2.getInstance().handleException(new CoreException2("Error while triggering onMagicDrawStart hooks", e));
       }

   }


}
