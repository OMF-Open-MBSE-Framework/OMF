package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base;


import com.samares_engineering.omf.omf_core_framework.plugin.OMFPlugin;

import java.util.ArrayList;
import java.util.List;

public abstract class HookExecutor<H extends Hook> {
    List<H> hooksHolders;
    private OMFPlugin plugin;

    public void addHook(H hook) {
        hooksHolders.add(hook);
    }
    public void addAllHooks(List<H> hooks) {
        this.hooksHolders.addAll(hooks);
    }
    public void removeHook(H hook) {
        hooksHolders.remove(hook);
    }
    public void removeAllHooks(List<H> hooks) {
        this.hooksHolders.removeAll(hooks);
    }

    public void clearHooks() {
        hooksHolders.clear();
    }

    public List<H> getHooksHolders() {
        return hooksHolders;
    }


    public void init(OMFPlugin OMFPlugin) {
        this.plugin = OMFPlugin;
        this.hooksHolders = new ArrayList<>();
    }

    public OMFPlugin getPlugin() {
        return plugin;
    }
}
