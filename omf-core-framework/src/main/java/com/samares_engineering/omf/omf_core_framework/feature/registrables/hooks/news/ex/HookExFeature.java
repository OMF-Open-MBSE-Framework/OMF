package com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.news.ex;

import com.samares_engineering.omf.omf_core_framework.feature.SimpleFeature;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.LifeCycleHook;

import java.util.List;

public class HookExFeature extends SimpleFeature {

    public HookExFeature() {
        super("HookExFeature");
    }

    @Override
    protected List<LifeCycleHook> initLifeCycleHooks() {
        return super.initLifeCycleHooks();
    }
}
