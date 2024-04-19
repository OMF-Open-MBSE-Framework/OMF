package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.executors;


import com.samares_engineering.omf.omf_core_framework.errormanagement2.ErrorHandler2;
import com.samares_engineering.omf.omf_core_framework.errors.OMFErrorHandler;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.HookFeatureItem;

import java.util.List;

public abstract class HookExecutor<H extends HookFeatureItem> {
    List<H> hooks;

    public void addHook(H hook) {
        hooks.add(hook);
    }
    public void addAllHooks(List<H> hooks) {
        this.hooks.addAll(hooks);
    }
    public void removeHook(H hook) {
        hooks.remove(hook);
    }
    public void removeAllHooks(List<H> hooks) {
        this.hooks.removeAll(hooks);
    }

    public void clearHooks() {
        hooks.clear();
    }

    public List<H> getHooks() {
        return hooks;
    }

    public void executeHooks() {
        for (H hook : hooks) {
            executeHook(hook);
        }
    }

    private void executeHook(H hook) {
        try {
            hook.execute();
        } catch (Exception e) {
            ErrorHandler2.getInstance().handleException(e);
        }
    }
}
