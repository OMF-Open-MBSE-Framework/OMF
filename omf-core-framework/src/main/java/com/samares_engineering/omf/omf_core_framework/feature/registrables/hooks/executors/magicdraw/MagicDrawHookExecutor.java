package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors.magicdraw;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.HookExecutor;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.IOnMagicDrawStartHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.magicdraw.MagicdrawLifeCycleHook;

public class MagicDrawHookExecutor extends HookExecutor<MagicdrawLifeCycleHook> {

   public void triggerOnMagicDrawStartHooks() {
       getHooksHolders().stream()
               .filter(IOnMagicDrawStartHook.class::isInstance)
               .map(IOnMagicDrawStartHook.class::cast)
                .forEach(IOnMagicDrawStartHook::onMagicDrawStart);

   }


}
