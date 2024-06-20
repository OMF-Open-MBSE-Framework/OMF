package com.samares_engineering.omf.omf_core_framework.feature;

import com.samares_engineering.omf.omf_core_framework.feature.registrables.actions.UIAction;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.hooks.base.IHook;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.liveactions.liveaction_engine.ALiveActionEngine;
import com.samares_engineering.omf.omf_core_framework.feature.registrables.options.option.IOption;

import java.util.Collections;
import java.util.List;

public abstract class SimpleFeature extends AFeature{
    protected SimpleFeature(String name) {
        super(name);
    }

    @Override
    protected List<UIAction> initFeatureActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<ALiveActionEngine> initLiveActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<ALiveActionEngine> initProjectOnlyLiveActions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IOption> initOptions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IOption> initProjectOnlyOptions() {
        return Collections.emptyList();
    }

    @Override
    protected List<IHook> initLifeCycleHooks() {
        return Collections.emptyList();
    }

    @Override
    protected EnvOptionsHelper initEnvOptionsHelper() {return null;}


}
